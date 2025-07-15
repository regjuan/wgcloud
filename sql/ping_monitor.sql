-- PING监控系统数据库表结构
-- 作者：WGCLOUD开发团队
-- 创建时间：2024年

-- 1. PING任务表
CREATE TABLE `ping_task` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `task_name` varchar(100) NOT NULL COMMENT '任务名称',
  `description` varchar(500) DEFAULT NULL COMMENT '任务描述',
  `ip_list` text NOT NULL COMMENT 'IP列表，逗号分隔',
  `ping_interval` int NOT NULL DEFAULT 60 COMMENT 'PING间隔（秒）',
  `timeout` int NOT NULL DEFAULT 5000 COMMENT '超时时间（毫秒）',
  `record_all_results` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否记录所有结果（0：仅失败，1：全部）',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用（0：禁用，1：启用）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `tags` varchar(500) DEFAULT NULL COMMENT '标签，JSON格式',
  PRIMARY KEY (`id`),
  KEY `idx_task_name` (`task_name`),
  KEY `idx_enabled` (`is_enabled`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='PING监控任务表';

-- 2. PING结果表
CREATE TABLE `ping_result` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `task_id` varchar(36) NOT NULL COMMENT 'PING任务ID',
  `target_ip` varchar(45) NOT NULL COMMENT '目标IP地址',
  `is_reachable` tinyint(1) NOT NULL COMMENT '是否可达（0：不可达，1：可达）',
  `response_time` bigint NOT NULL DEFAULT 0 COMMENT '响应时间（毫秒）',
  `packet_loss` decimal(5,2) NOT NULL DEFAULT 0.00 COMMENT '丢包率（百分比）',
  `ping_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'PING时间',
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_target_ip` (`target_ip`),
  KEY `idx_ping_time` (`ping_time`),
  KEY `idx_is_reachable` (`is_reachable`),
  KEY `idx_task_ping_time` (`task_id`, `ping_time`),
  CONSTRAINT `fk_ping_result_task` FOREIGN KEY (`task_id`) REFERENCES `ping_task` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='PING监控结果表';

-- 3. PING统计表（可选，用于性能优化）
CREATE TABLE `ping_statistics` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `task_id` varchar(36) NOT NULL COMMENT 'PING任务ID',
  `target_ip` varchar(45) NOT NULL COMMENT '目标IP地址',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `total_pings` int NOT NULL DEFAULT 0 COMMENT '总PING次数',
  `success_pings` int NOT NULL DEFAULT 0 COMMENT '成功PING次数',
  `failure_pings` int NOT NULL DEFAULT 0 COMMENT '失败PING次数',
  `avg_response_time` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '平均响应时间（毫秒）',
  `max_response_time` bigint NOT NULL DEFAULT 0 COMMENT '最大响应时间（毫秒）',
  `min_response_time` bigint NOT NULL DEFAULT 0 COMMENT '最小响应时间（毫秒）',
  `availability_rate` decimal(5,2) NOT NULL DEFAULT 0.00 COMMENT '可用性（百分比）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_ip_date` (`task_id`, `target_ip`, `stat_date`),
  KEY `idx_stat_date` (`stat_date`),
  KEY `idx_task_date` (`task_id`, `stat_date`),
  CONSTRAINT `fk_ping_stat_task` FOREIGN KEY (`task_id`) REFERENCES `ping_task` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='PING监控统计表';

-- 插入初始示例数据
INSERT INTO `ping_task` (`id`, `task_name`, `description`, `ip_list`, `ping_interval`, `timeout`, `record_all_results`, `is_enabled`) VALUES
('ping-task-001', '网关监控', '监控网络出口网关连通性', '192.168.1.1,8.8.8.8', 30, 3000, 0, 1),
('ping-task-002', '服务器监控', '监控重要服务器连通性', '192.168.1.100,192.168.1.101,192.168.1.102', 60, 5000, 1, 1),
('ping-task-003', 'DNS服务器监控', '监控DNS服务器可用性', '8.8.8.8,114.114.114.114,223.5.5.5', 30, 3000, 0, 1);

-- 创建索引优化查询性能
-- 针对时间范围查询的复合索引
CREATE INDEX `idx_task_time_reachable` ON `ping_result` (`task_id`, `ping_time`, `is_reachable`);

-- 针对IP统计查询的复合索引  
CREATE INDEX `idx_ip_time_reachable` ON `ping_result` (`target_ip`, `ping_time`, `is_reachable`);

-- 数据清理：自动删除30天前的PING结果数据（可根据需要调整）
-- 可以添加到定时任务中执行
-- DELETE FROM ping_result WHERE ping_time < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- 创建视图：PING任务运行状态视图
CREATE VIEW `v_ping_task_status` AS
SELECT 
    t.id,
    t.task_name,
    t.description,
    t.ip_list,
    t.ping_interval,
    t.is_enabled,
    t.create_time,
    (SELECT COUNT(*) FROM ping_result r WHERE r.task_id = t.id AND r.ping_time >= DATE_SUB(NOW(), INTERVAL 1 DAY)) as last_24h_pings,
    (SELECT COUNT(*) FROM ping_result r WHERE r.task_id = t.id AND r.ping_time >= DATE_SUB(NOW(), INTERVAL 1 DAY) AND r.is_reachable = 0) as last_24h_failures,
    (SELECT ping_time FROM ping_result r WHERE r.task_id = t.id ORDER BY r.ping_time DESC LIMIT 1) as last_ping_time,
    CASE 
        WHEN (SELECT COUNT(*) FROM ping_result r WHERE r.task_id = t.id AND r.ping_time >= DATE_SUB(NOW(), INTERVAL 1 DAY)) > 0 
        THEN ROUND((1 - (SELECT COUNT(*) FROM ping_result r WHERE r.task_id = t.id AND r.ping_time >= DATE_SUB(NOW(), INTERVAL 1 DAY) AND r.is_reachable = 0) / (SELECT COUNT(*) FROM ping_result r WHERE r.task_id = t.id AND r.ping_time >= DATE_SUB(NOW(), INTERVAL 1 DAY))) * 100, 2)
        ELSE 0 
    END as availability_24h
FROM ping_task t;

-- 创建视图：IP连通性统计视图
CREATE VIEW `v_ip_connectivity_stats` AS
SELECT 
    r.target_ip,
    COUNT(*) as total_pings,
    SUM(CASE WHEN r.is_reachable = 1 THEN 1 ELSE 0 END) as success_pings,
    SUM(CASE WHEN r.is_reachable = 0 THEN 1 ELSE 0 END) as failure_pings,
    ROUND(AVG(CASE WHEN r.is_reachable = 1 THEN r.response_time ELSE NULL END), 2) as avg_response_time,
    MAX(CASE WHEN r.is_reachable = 1 THEN r.response_time ELSE 0 END) as max_response_time,
    MIN(CASE WHEN r.is_reachable = 1 THEN r.response_time ELSE NULL END) as min_response_time,
    ROUND((SUM(CASE WHEN r.is_reachable = 1 THEN 1 ELSE 0 END) / COUNT(*)) * 100, 2) as availability_rate,
    MAX(r.ping_time) as last_ping_time
FROM ping_result r 
WHERE r.ping_time >= DATE_SUB(NOW(), INTERVAL 24 HOUR)
GROUP BY r.target_ip;

-- 数据库权限设置（根据实际情况调整）
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ping_task TO 'wgcloud'@'%';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ping_result TO 'wgcloud'@'%';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ping_statistics TO 'wgcloud'@'%';
-- GRANT SELECT ON v_ping_task_status TO 'wgcloud'@'%';
-- GRANT SELECT ON v_ip_connectivity_stats TO 'wgcloud'@'%';

-- 表空间优化建议（MySQL 8.0+）
-- ALTER TABLE ping_result PARTITION BY RANGE (TO_DAYS(ping_time)) (
--     PARTITION p202401 VALUES LESS THAN (TO_DAYS('2024-02-01')),
--     PARTITION p202402 VALUES LESS THAN (TO_DAYS('2024-03-01')),
--     PARTITION p202403 VALUES LESS THAN (TO_DAYS('2024-04-01')),
--     PARTITION pfuture VALUES LESS THAN MAXVALUE
-- ); 