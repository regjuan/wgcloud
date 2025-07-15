/*
Navicat MySQL Data Transfer

Source Server         : 127.0.0.1
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : wgcloud

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2021-11-26 20:01:46
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for app_info
-- ----------------------------
DROP TABLE IF EXISTS `APP_INFO`;
CREATE TABLE `APP_INFO` (
  `ID` char(32) NOT NULL,
  `HOST_NAME` char(30) DEFAULT NULL,
  `APP_PID` char(200) DEFAULT NULL,
  `CREATE_TIME` timestamp NULL DEFAULT NULL,
  `APP_NAME` varchar(50) DEFAULT NULL,
  `CPU_PER` double(8,2) DEFAULT NULL,
  `MEM_PER` double(10,2) DEFAULT NULL,
  `APP_TYPE` char(1) DEFAULT NULL,
  `STATE` char(1) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for app_state
-- ----------------------------
DROP TABLE IF EXISTS `APP_STATE`;
CREATE TABLE `APP_STATE` (
  `ID` char(32) NOT NULL,
  `APP_INFO_ID` char(32) DEFAULT NULL,
  `CPU_PER` double(8,2) DEFAULT NULL,
  `MEM_PER` double(10,2) DEFAULT NULL,
  `CREATE_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `APP_STAT_INDEX` (`APP_INFO_ID`,`CREATE_TIME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for cpu_state
-- ----------------------------
DROP TABLE IF EXISTS `CPU_STATE`;
CREATE TABLE `CPU_STATE` (
  `ID` char(32) NOT NULL,
  `HOST_NAME` char(30) DEFAULT NULL,
  `USER` char(30) DEFAULT NULL,
  `SYS` double(8,2) DEFAULT NULL,
  `IDLE` double(8,2) DEFAULT NULL,
  `IOWAIT` double(8,2) DEFAULT NULL,
  `IRQ` char(30) DEFAULT NULL,
  `SOFT` char(30) DEFAULT NULL,
  `DATE_STR` char(30) DEFAULT NULL,
  `CREATE_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `CPU_ACC_HOST_INDEX` (`HOST_NAME`,`CREATE_TIME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for db_info
-- ----------------------------
DROP TABLE IF EXISTS `DB_INFO`;
CREATE TABLE `DB_INFO` (
  `ID` char(32) NOT NULL,
  `DBTYPE` char(32) DEFAULT NULL,
  `USER` varchar(50) DEFAULT NULL,
  `PASSWD` varchar(50) DEFAULT NULL,
  `IP` char(20) DEFAULT NULL,
  `PORT` char(10) DEFAULT NULL,
  `CREATE_TIME` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `DBNAME` char(50) DEFAULT NULL,
  `DB_STATE` char(1) DEFAULT NULL,
  `ALIAS_NAME` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for db_table
-- ----------------------------
DROP TABLE IF EXISTS `DB_TABLE`;
CREATE TABLE `DB_TABLE` (
  `ID` char(32) NOT NULL,
  `TABLE_NAME` varchar(50) DEFAULT NULL,
  `WHERE_VAL` varchar(200) DEFAULT NULL,
  `CREATE_TIME` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `REMARK` varchar(50) DEFAULT NULL,
  `TABLE_COUNT` bigint(20) DEFAULT NULL,
  `DATE_STR` char(30) DEFAULT NULL,
  `DBINFO_ID` char(32) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for db_table_count
-- ----------------------------
DROP TABLE IF EXISTS `DB_TABLE_COUNT`;
CREATE TABLE `DB_TABLE_COUNT` (
  `ID` char(32) NOT NULL,
  `DB_TABLE_ID` char(32) DEFAULT NULL,
  `TABLE_COUNT` bigint(20) DEFAULT NULL,
  `DATE_STR` char(30) DEFAULT NULL,
  `CREATE_TIME` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for desk_state
-- ----------------------------
DROP TABLE IF EXISTS `DESK_STATE`;
CREATE TABLE `DESK_STATE` (
  `ID` char(32) NOT NULL,
  `HOST_NAME` char(30) DEFAULT NULL,
  `FILE_STSTEM` char(50) DEFAULT NULL,
  `SIZE` char(30) DEFAULT NULL,
  `USED` char(30) DEFAULT NULL,
  `AVAIL` char(30) DEFAULT NULL,
  `USE_PER` char(10) DEFAULT NULL,
  `DATE_STR` char(30) DEFAULT NULL,
  `CREATE_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `DESK_ACC_HOST_INDEX` (`HOST_NAME`,`CREATE_TIME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for heath_monitor
-- ----------------------------
DROP TABLE IF EXISTS `HEATH_MONITOR`;
CREATE TABLE `HEATH_MONITOR` (
  `ID` char(32) NOT NULL,
  `APP_NAME` char(50) DEFAULT NULL,
  `HEATH_URL` varchar(255) DEFAULT NULL,
  `CREATE_TIME` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `HEATH_STATUS` char(10) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for host_info
-- ----------------------------
DROP TABLE IF EXISTS `HOST_INFO`;
CREATE TABLE `HOST_INFO` (
  `ID` char(32) NOT NULL,
  `IP` char(30) DEFAULT NULL,
  `PORT` char(20) DEFAULT NULL,
  `ROOT` char(50) DEFAULT NULL,
  `PASSWD` char(50) DEFAULT NULL,
  `CREATE_TIME` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `REMARK` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for intrusion_info
-- ----------------------------
DROP TABLE IF EXISTS `INTRUSION_INFO`;
CREATE TABLE `INTRUSION_INFO` (
  `ID` char(32) NOT NULL,
  `HOST_NAME` char(30) DEFAULT NULL,
  `LSMOD` text,
  `PASSWD_INFO` varchar(100) DEFAULT NULL,
  `CRONTAB` text,
  `PROMISC` varchar(100) DEFAULT NULL,
  `RPCINFO` text,
  `CREATE_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for log_info
-- ----------------------------
DROP TABLE IF EXISTS `LOG_INFO`;
CREATE TABLE `LOG_INFO` (
  `ID` char(32) NOT NULL,
  `HOST_NAME` char(50) DEFAULT NULL,
  `INFO_CONTENT` text,
  `STATE` char(1) DEFAULT NULL,
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for mail_set
-- ----------------------------
DROP TABLE IF EXISTS `MAIL_SET`;
CREATE TABLE `MAIL_SET` (
  `ID` char(32) COLLATE utf8_unicode_ci NOT NULL,
  `SEND_MAIL` char(60) COLLATE utf8_unicode_ci DEFAULT NULL,
  `FROM_MAIL_NAME` char(60) COLLATE utf8_unicode_ci DEFAULT NULL,
  `FROM_PWD` char(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SMTP_HOST` char(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SMTP_PORT` char(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `SMTP_SSL` char(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `TO_MAIL` char(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `CPU_PER` char(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `CREATE_TIME` timestamp NULL DEFAULT NULL,
  `MEM_PER` char(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `HEATH_PER` char(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for mem_state
-- ----------------------------
DROP TABLE IF EXISTS `MEM_STATE`;
CREATE TABLE `MEM_STATE` (
  `ID` char(32) NOT NULL,
  `HOST_NAME` char(30) DEFAULT NULL,
  `TOTAL` char(30) DEFAULT NULL,
  `USED` char(30) DEFAULT NULL,
  `FREE` char(30) DEFAULT NULL,
  `USE_PER` double(8,2) DEFAULT NULL,
  `DATE_STR` char(30) DEFAULT NULL,
  `CREATE_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `MEM_ACC_HOST_INDEX` (`HOST_NAME`,`CREATE_TIME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for netio_state
-- ----------------------------
DROP TABLE IF EXISTS `NETIO_STATE`;
CREATE TABLE `NETIO_STATE` (
  `ID` char(32) NOT NULL,
  `HOST_NAME` char(30) DEFAULT NULL,
  `RXPCK` char(30) DEFAULT NULL,
  `TXPCK` char(30) DEFAULT NULL,
  `RXBYT` char(30) DEFAULT NULL,
  `TXBYT` char(30) DEFAULT NULL,
  `RXCMP` char(30) DEFAULT NULL,
  `TXCMP` char(30) DEFAULT NULL,
  `RXMCST` char(30) DEFAULT NULL,
  `DATE_STR` char(30) DEFAULT NULL,
  `CREATE_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `NETIO_ACC_HOST_INDEX` (`HOST_NAME`,`CREATE_TIME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for system_info
-- ----------------------------
DROP TABLE IF EXISTS `SYSTEM_INFO`;
CREATE TABLE `SYSTEM_INFO` (
  `ID` char(32) NOT NULL,
  `HOST_NAME` char(30) DEFAULT NULL,
  `VERSION` char(100) DEFAULT NULL,
  `VERSION_DETAIL` char(200) DEFAULT NULL,
  `CPU_PER` double(8,2) DEFAULT NULL,
  `MEM_PER` double(8,2) DEFAULT NULL,
  `CPU_CORE_NUM` char(10) DEFAULT NULL,
  `CREATE_TIME` timestamp NULL DEFAULT NULL,
  `CPU_XH` char(150) DEFAULT NULL,
  `STATE` char(1) DEFAULT NULL,
  `REMARK` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sys_load_state
-- ----------------------------
DROP TABLE IF EXISTS `SYS_LOAD_STATE`;
CREATE TABLE `SYS_LOAD_STATE` (
  `ID` char(32) NOT NULL,
  `HOST_NAME` char(30) DEFAULT NULL,
  `ONE_LOAD` double(8,2) DEFAULT NULL,
  `FIVE_LOAD` double(8,2) DEFAULT NULL,
  `FIFTEEN_LOAD` double(8,2) DEFAULT NULL,
  `USERS` char(10) DEFAULT NULL,
  `DATE_STR` char(30) DEFAULT NULL,
  `CREATE_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `LOAD_ACC_HOST_INDEX` (`HOST_NAME`,`CREATE_TIME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tcp_state
-- ----------------------------
DROP TABLE IF EXISTS `TCP_STATE`;
CREATE TABLE `TCP_STATE` (
  `ID` char(32) NOT NULL,
  `HOST_NAME` char(30) DEFAULT NULL,
  `ACTIVE` char(30) DEFAULT NULL,
  `PASSIVE` char(30) DEFAULT NULL,
  `RETRANS` char(30) DEFAULT NULL,
  `DATE_STR` char(30) DEFAULT NULL,
  `CREATE_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `TCP_ACC_HOST_INDEX` (`HOST_NAME`,`CREATE_TIME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for ping_task
-- ----------------------------
DROP TABLE IF EXISTS `ping_task`;
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

-- ----------------------------
-- Table structure for ping_result
-- ----------------------------
DROP TABLE IF EXISTS `ping_result`;
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

-- ----------------------------
-- Table structure for command_task
-- ----------------------------
DROP TABLE IF EXISTS `command_task`;
CREATE TABLE `command_task` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `command_name` varchar(100) NOT NULL COMMENT '指令名称',
  `command_content` text NOT NULL COMMENT '指令内容',
  `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT '任务状态：pending-等待执行，deploying-部署中，completed-已完成，failed-执行失败，partial-部分成功',
  `executed_count` int NOT NULL DEFAULT 0 COMMENT '已执行数量',
  `total_count` int NOT NULL DEFAULT 0 COMMENT '总下发数量',
  `deploy_time` timestamp NULL DEFAULT NULL COMMENT '下发时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `target_tags` text COMMENT '目标标签（JSON格式存储）',
  `target_hosts` text COMMENT '目标主机列表（JSON格式存储）',
  `task_type` varchar(20) NOT NULL DEFAULT 'immediate' COMMENT '任务类型：immediate-立即执行，scheduled-定时执行',
  `scheduled_time` timestamp NULL DEFAULT NULL COMMENT '预定执行时间（定时任务用）',
  `timeout_seconds` int NOT NULL DEFAULT 300 COMMENT '超时时间（秒）',
  `creator` varchar(50) NOT NULL COMMENT '创建人',
  `description` varchar(500) DEFAULT NULL COMMENT '任务描述',
  `result_summary` text COMMENT '执行结果摘要',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_creator` (`creator`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_deploy_time` (`deploy_time`),
  KEY `idx_task_type` (`task_type`),
  KEY `idx_scheduled_time` (`scheduled_time`),
  KEY `idx_command_name` (`command_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='指令下发任务表';

-- ----------------------------
-- Table structure for command_execution_record
-- ----------------------------
DROP TABLE IF EXISTS `command_execution_record`;
CREATE TABLE `command_execution_record` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `command_task_id` varchar(36) NOT NULL COMMENT '指令任务ID',
  `hostname` varchar(100) NOT NULL COMMENT '主机IP或主机名',
  `host_description` varchar(200) DEFAULT NULL COMMENT '主机描述',
  `command_content` text NOT NULL COMMENT '下发指令内容',
  `execute_time` timestamp NULL DEFAULT NULL COMMENT '指令下发时间',
  `complete_time` timestamp NULL DEFAULT NULL COMMENT '执行完成时间',
  `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT '执行状态：success-成功，failed-失败，timeout-超时，pending-等待执行，running-执行中',
  `result` text COMMENT '指令执行结果',
  `error_message` text COMMENT '错误信息',
  `execution_time` bigint DEFAULT NULL COMMENT '执行耗时（毫秒）',
  `exit_code` int DEFAULT NULL COMMENT '退出码',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `retry_count` int NOT NULL DEFAULT 0 COMMENT '重试次数',
  `executor_info` varchar(100) DEFAULT NULL COMMENT '执行节点信息',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`command_task_id`),
  KEY `idx_hostname` (`hostname`),
  KEY `idx_status` (`status`),
  KEY `idx_execute_time` (`execute_time`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_task_status` (`command_task_id`, `status`),
  KEY `idx_task_hostname` (`command_task_id`, `hostname`),
  CONSTRAINT `fk_command_record_task` FOREIGN KEY (`command_task_id`) REFERENCES `command_task` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='指令执行记录表';
