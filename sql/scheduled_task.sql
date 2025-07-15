-- ========================================
-- 计划任务模块数据库建表脚本
-- 适用于MySQL 5.7+
-- 作者: WGCLOUD
-- 创建时间: 2024-01-16
-- ========================================

-- 1. 计划任务表
CREATE TABLE IF NOT EXISTS `scheduled_task` (
  `id` varchar(50) NOT NULL COMMENT '主键ID',
  `hostname` varchar(100) NOT NULL COMMENT '目标主机IP',
  `task_name` varchar(200) NOT NULL COMMENT '计划任务名称',
  `cron_expression` varchar(100) NOT NULL COMMENT '执行时间（Cron表达式）',
  `command` text NOT NULL COMMENT '执行指令',
  `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT '执行状态：success-成功/failed-失败/running-运行中/pending-等待中',
  `last_execute_time` datetime DEFAULT NULL COMMENT '最新执行时间',
  `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用：1-启用/0-禁用',
  `tags` varchar(500) DEFAULT NULL COMMENT '标签（JSON格式存储）',
  `description` varchar(1000) DEFAULT NULL COMMENT '任务描述',
  `timeout_seconds` int DEFAULT 300 COMMENT '超时时间（秒）',
  `retry_count` int NOT NULL DEFAULT 0 COMMENT '重试次数',
  `max_retry_count` int NOT NULL DEFAULT 3 COMMENT '最大重试次数',
  `last_result` text DEFAULT NULL COMMENT '执行结果',
  `last_error_message` text DEFAULT NULL COMMENT '错误信息',
  `last_execution_time` bigint DEFAULT NULL COMMENT '执行耗时（毫秒）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(100) DEFAULT NULL COMMENT '创建人',
  `next_execute_time` datetime DEFAULT NULL COMMENT '下次执行时间',
  PRIMARY KEY (`id`),
  KEY `idx_hostname` (`hostname`),
  KEY `idx_task_name` (`task_name`),
  KEY `idx_status` (`status`),
  KEY `idx_enabled` (`enabled`),
  KEY `idx_next_execute_time` (`next_execute_time`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_creator` (`creator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计划任务表';

-- 2. 计划任务执行记录表
CREATE TABLE IF NOT EXISTS `scheduled_task_record` (
  `id` varchar(50) NOT NULL COMMENT '主键ID',
  `task_id` varchar(50) NOT NULL COMMENT '计划任务ID',
  `task_name` varchar(200) NOT NULL COMMENT '任务名称',
  `hostname` varchar(100) NOT NULL COMMENT '目标主机IP',
  `command` text NOT NULL COMMENT '执行指令',
  `execute_time` datetime NOT NULL COMMENT '执行时间',
  `complete_time` datetime DEFAULT NULL COMMENT '执行完成时间',
  `status` varchar(20) NOT NULL DEFAULT 'running' COMMENT '执行状态：success-成功/failed-失败/timeout-超时/running-运行中',
  `result` text DEFAULT NULL COMMENT '执行结果',
  `error_message` text DEFAULT NULL COMMENT '错误信息',
  `duration` bigint DEFAULT NULL COMMENT '执行耗时（毫秒）',
  `exit_code` int DEFAULT NULL COMMENT '退出码',
  `retry_count` int NOT NULL DEFAULT 0 COMMENT '重试次数',
  `executor_info` varchar(200) DEFAULT NULL COMMENT '执行节点信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `manual_trigger` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否由手动触发：1-是/0-否',
  `trigger_by` varchar(100) DEFAULT NULL COMMENT '触发人',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_hostname` (`hostname`),
  KEY `idx_status` (`status`),
  KEY `idx_execute_time` (`execute_time`),
  KEY `idx_manual_trigger` (`manual_trigger`),
  KEY `idx_trigger_by` (`trigger_by`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_scheduled_task_record_task_id` FOREIGN KEY (`task_id`) REFERENCES `scheduled_task` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计划任务执行记录表';

-- 3. 插入示例数据
INSERT INTO `scheduled_task` (`id`, `hostname`, `task_name`, `cron_expression`, `command`, `status`, `enabled`, `description`, `timeout_seconds`, `max_retry_count`, `creator`) VALUES
('demo-task-001', '127.0.0.1', '系统信息检查', '0 */10 * * * ?', 'uname -a && df -h', 'pending', 1, '每10分钟检查一次系统信息', 60, 3, 'admin'),
('demo-task-002', '127.0.0.1', '日志清理', '0 0 2 * * ?', 'find /tmp -name "*.log" -mtime +7 -delete', 'pending', 1, '每天凌晨2点清理7天前的日志文件', 300, 2, 'admin'),
('demo-task-003', '127.0.0.1', '数据库备份', '0 0 1 * * ?', 'mysqldump -u root -p[password] wgcloud > /backup/wgcloud_$(date +%Y%m%d).sql', 'pending', 0, '每天凌晨1点备份数据库（已禁用）', 1800, 1, 'admin');

-- 4. 创建视图：任务执行统计
CREATE OR REPLACE VIEW `v_scheduled_task_statistics` AS
SELECT 
    st.id,
    st.task_name,
    st.hostname,
    st.status,
    st.enabled,
    st.last_execute_time,
    st.next_execute_time,
    st.creator,
    st.create_time,
    COUNT(str.id) as total_executions,
    SUM(CASE WHEN str.status = 'success' THEN 1 ELSE 0 END) as success_count,
    SUM(CASE WHEN str.status = 'failed' THEN 1 ELSE 0 END) as failed_count,
    SUM(CASE WHEN str.status = 'timeout' THEN 1 ELSE 0 END) as timeout_count,
    AVG(str.duration) as avg_duration,
    MAX(str.duration) as max_duration,
    MIN(str.duration) as min_duration,
    MAX(str.execute_time) as last_record_time
FROM scheduled_task st
LEFT JOIN scheduled_task_record str ON st.id = str.task_id
GROUP BY st.id, st.task_name, st.hostname, st.status, st.enabled, st.last_execute_time, st.next_execute_time, st.creator, st.create_time;

-- 5. 创建存储过程：清理过期执行记录
DELIMITER $$
CREATE PROCEDURE `sp_cleanup_expired_task_records`(IN `days_to_keep` INT)
BEGIN
    DECLARE affected_rows INT DEFAULT 0;
    
    -- 删除过期的执行记录
    DELETE FROM scheduled_task_record 
    WHERE create_time < DATE_SUB(NOW(), INTERVAL days_to_keep DAY);
    
    -- 获取受影响的行数
    SET affected_rows = ROW_COUNT();
    
    -- 输出结果
    SELECT CONCAT('已清理 ', affected_rows, ' 条过期执行记录') AS result;
END$$
DELIMITER ;

-- 6. 创建存储过程：任务执行统计报告
DELIMITER $$
CREATE PROCEDURE `sp_task_execution_report`(IN `start_date` DATE, IN `end_date` DATE)
BEGIN
    -- 按日期统计执行情况
    SELECT 
        DATE(execute_time) as execute_date,
        COUNT(*) as total_executions,
        SUM(CASE WHEN status = 'success' THEN 1 ELSE 0 END) as success_count,
        SUM(CASE WHEN status = 'failed' THEN 1 ELSE 0 END) as failed_count,
        SUM(CASE WHEN status = 'timeout' THEN 1 ELSE 0 END) as timeout_count,
        AVG(duration) as avg_duration,
        MAX(duration) as max_duration
    FROM scheduled_task_record
    WHERE DATE(execute_time) BETWEEN start_date AND end_date
    GROUP BY DATE(execute_time)
    ORDER BY execute_date DESC;
    
    -- 按任务统计执行情况
    SELECT 
        task_name,
        hostname,
        COUNT(*) as total_executions,
        SUM(CASE WHEN status = 'success' THEN 1 ELSE 0 END) as success_count,
        SUM(CASE WHEN status = 'failed' THEN 1 ELSE 0 END) as failed_count,
        ROUND(SUM(CASE WHEN status = 'success' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as success_rate,
        AVG(duration) as avg_duration
    FROM scheduled_task_record
    WHERE DATE(execute_time) BETWEEN start_date AND end_date
    GROUP BY task_name, hostname
    ORDER BY total_executions DESC;
END$$
DELIMITER ;

-- 7. 添加索引优化
-- 复合索引：任务执行记录按任务ID和执行时间查询
CREATE INDEX `idx_task_execute_time` ON `scheduled_task_record` (`task_id`, `execute_time` DESC);

-- 复合索引：按状态和执行时间查询
CREATE INDEX `idx_status_execute_time` ON `scheduled_task_record` (`status`, `execute_time` DESC);

-- 复合索引：按主机名和执行时间查询
CREATE INDEX `idx_hostname_execute_time` ON `scheduled_task_record` (`hostname`, `execute_time` DESC);

-- 8. 插入示例执行记录（可选）
/*
INSERT INTO `scheduled_task_record` (`id`, `task_id`, `task_name`, `hostname`, `command`, `execute_time`, `complete_time`, `status`, `result`, `duration`, `exit_code`, `manual_trigger`, `trigger_by`) VALUES
('record-001', 'demo-task-001', '系统信息检查', '127.0.0.1', 'uname -a && df -h', '2024-01-16 10:00:00', '2024-01-16 10:00:05', 'success', 'Linux localhost 5.4.0-74-generic #83-Ubuntu SMP\n/dev/sda1       20G   15G  4.2G  79% /', 5000, 0, 1, 'admin'),
('record-002', 'demo-task-002', '日志清理', '127.0.0.1', 'find /tmp -name "*.log" -mtime +7 -delete', '2024-01-16 02:00:00', '2024-01-16 02:00:02', 'success', '', 2000, 0, 0, NULL);
*/

-- 建表脚本完成
SELECT 'WGCLOUD计划任务模块建表脚本执行完成！' AS message; 