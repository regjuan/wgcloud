

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for alarm_info
-- ----------------------------
DROP TABLE IF EXISTS `alarm_info`;
CREATE TABLE `alarm_info` (
                              `ID` char(32) NOT NULL,
                              `HOST_NAME` varchar(255) DEFAULT NULL COMMENT '主机名或IP地址',
                              `LOG_TITLE` varchar(255) DEFAULT NULL COMMENT '告警标题/类型',
                              `INFO_CONTENT` text DEFAULT NULL COMMENT '告警详情',
                              `SOURCE` varchar(100) DEFAULT NULL COMMENT '告警来源',
                              `STATE` char(1) DEFAULT NULL COMMENT '告警状态',
                              `CREATE_TIME` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
                              PRIMARY KEY (`ID`),
                              KEY `ALARM_INFO_HOST_INDEX` (`HOST_NAME`,`CREATE_TIME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='告警信息表';

-- ----------------------------
-- Table structure for app_info
-- ----------------------------
DROP TABLE IF EXISTS `app_info`;
CREATE TABLE `app_info` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for app_state
-- ----------------------------
DROP TABLE IF EXISTS `app_state`;
CREATE TABLE `app_state` (
                             `ID` char(32) NOT NULL,
                             `APP_INFO_ID` char(32) DEFAULT NULL,
                             `CPU_PER` double(8,2) DEFAULT NULL,
                             `MEM_PER` double(10,2) DEFAULT NULL,
                             `CREATE_TIME` timestamp NULL DEFAULT NULL,
                             PRIMARY KEY (`ID`),
                             KEY `APP_STAT_INDEX` (`APP_INFO_ID`,`CREATE_TIME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for command
-- ----------------------------
DROP TABLE IF EXISTS `command`;
CREATE TABLE `command` (
                           `ID` varchar(50) NOT NULL,
                           `CMD_NAME` varchar(100) NOT NULL,
                           `CMD_CONTENT` text DEFAULT NULL,
                           `CMD_TYPE` varchar(20) DEFAULT 'SHELL',
                           `TIMEOUT` int(11) DEFAULT 60,
                           `CREATE_TIME` datetime DEFAULT NULL,
                           PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='指令库';

-- ----------------------------
-- Table structure for command_result
-- ----------------------------
DROP TABLE IF EXISTS `command_result`;
CREATE TABLE `command_result` (
                                  `ID` varchar(50) NOT NULL,
                                  `TASK_ID` varchar(50) DEFAULT NULL,
                                  `COMMAND_ID` varchar(50) NOT NULL,
                                  `HOSTNAME` varchar(100) NOT NULL,
                                  `STATUS` varchar(20) NOT NULL,
                                  `STDOUT` text DEFAULT NULL,
                                  `STDERR` text DEFAULT NULL,
                                  `EXIT_CODE` int(11) DEFAULT NULL,
                                  `START_TIME` datetime DEFAULT NULL,
                                  `END_TIME` datetime DEFAULT NULL,
                                  `EXPIRE_TIME` datetime DEFAULT NULL,
                                  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='指令执行历史结果';

-- ----------------------------
-- Table structure for cpu_state
-- ----------------------------
DROP TABLE IF EXISTS `cpu_state`;
CREATE TABLE `cpu_state` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for db_info
-- ----------------------------
DROP TABLE IF EXISTS `db_info`;
CREATE TABLE `db_info` (
                           `ID` char(32) NOT NULL,
                           `DBTYPE` char(32) DEFAULT NULL,
                           `USER` varchar(50) DEFAULT NULL,
                           `PASSWD` varchar(50) DEFAULT NULL,
                           `IP` char(20) DEFAULT NULL,
                           `PORT` char(10) DEFAULT NULL,
                           `CREATE_TIME` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp(),
                           `DBNAME` char(50) DEFAULT NULL,
                           `DB_STATE` char(1) DEFAULT NULL,
                           `ALIAS_NAME` varchar(50) DEFAULT NULL,
                           PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for db_table
-- ----------------------------
DROP TABLE IF EXISTS `db_table`;
CREATE TABLE `db_table` (
                            `ID` char(32) NOT NULL,
                            `TABLE_NAME` varchar(50) DEFAULT NULL,
                            `WHERE_VAL` varchar(200) DEFAULT NULL,
                            `CREATE_TIME` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp(),
                            `REMARK` varchar(50) DEFAULT NULL,
                            `TABLE_COUNT` bigint(20) DEFAULT NULL,
                            `DATE_STR` char(30) DEFAULT NULL,
                            `DBINFO_ID` char(32) DEFAULT NULL,
                            PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for db_table_count
-- ----------------------------
DROP TABLE IF EXISTS `db_table_count`;
CREATE TABLE `db_table_count` (
                                  `ID` char(32) NOT NULL,
                                  `DB_TABLE_ID` char(32) DEFAULT NULL,
                                  `TABLE_COUNT` bigint(20) DEFAULT NULL,
                                  `DATE_STR` char(30) DEFAULT NULL,
                                  `CREATE_TIME` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp(),
                                  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for desk_state
-- ----------------------------
DROP TABLE IF EXISTS `desk_state`;
CREATE TABLE `desk_state` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for heath_monitor
-- ----------------------------
DROP TABLE IF EXISTS `heath_monitor`;
CREATE TABLE `heath_monitor` (
                                 `ID` char(32) NOT NULL,
                                 `APP_NAME` char(50) DEFAULT NULL,
                                 `HEATH_URL` varchar(255) DEFAULT NULL,
                                 `CREATE_TIME` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp(),
                                 `HEATH_STATUS` char(10) DEFAULT NULL,
                                 PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for host_info
-- ----------------------------
DROP TABLE IF EXISTS `host_info`;
CREATE TABLE `host_info` (
                             `ID` char(32) NOT NULL,
                             `IP` char(30) DEFAULT NULL,
                             `PORT` char(20) DEFAULT NULL,
                             `ROOT` char(50) DEFAULT NULL,
                             `PASSWD` char(50) DEFAULT NULL,
                             `CREATE_TIME` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp(),
                             `REMARK` varchar(255) DEFAULT NULL,
                             PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for intrusion_info
-- ----------------------------
DROP TABLE IF EXISTS `intrusion_info`;
CREATE TABLE `intrusion_info` (
                                  `ID` char(32) NOT NULL,
                                  `HOST_NAME` char(30) DEFAULT NULL,
                                  `LSMOD` text DEFAULT NULL,
                                  `PASSWD_INFO` varchar(100) DEFAULT NULL,
                                  `CRONTAB` text DEFAULT NULL,
                                  `PROMISC` varchar(100) DEFAULT NULL,
                                  `RPCINFO` text DEFAULT NULL,
                                  `CREATE_TIME` timestamp NULL DEFAULT NULL,
                                  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for log_info
-- ----------------------------
DROP TABLE IF EXISTS `log_info`;
CREATE TABLE `log_info` (
                            `ID` char(32) NOT NULL,
                            `HOST_NAME` char(50) DEFAULT NULL,
                            `INFO_CONTENT` text DEFAULT NULL,
                            `STATE` char(1) DEFAULT NULL,
                            `CREATE_TIME` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                            `INFO_TITLE` text DEFAULT NULL,
                            `LOG_MON_ID` char(32) NOT NULL,
                            PRIMARY KEY (`ID`,`LOG_MON_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for log_mon
-- ----------------------------
DROP TABLE IF EXISTS `log_mon`;
CREATE TABLE `log_mon` (
                           `ID` varchar(32) NOT NULL COMMENT '主键ID',
                           `APP_NAME` varchar(255) NOT NULL COMMENT '应用名称',
                           `FILE_PATH` varchar(500) NOT NULL COMMENT '文件路径',
                           `MATCH_KEYWORDS` varchar(500) DEFAULT NULL COMMENT '匹配关键词',
                           `UN_MATCH_KEYWORDS` varchar(500) DEFAULT NULL COMMENT '不匹配关键词',
                           `TARGET_TAGS` varchar(255) DEFAULT NULL COMMENT '目标标签',
                           `CREATE_TIME` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
                           `ACTIVE` tinyint(4) DEFAULT NULL COMMENT '任务状态',
                           PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='日志监控表';

-- ----------------------------
-- Table structure for mail_set
-- ----------------------------
DROP TABLE IF EXISTS `mail_set`;
CREATE TABLE `mail_set` (
                            `ID` char(32) NOT NULL,
                            `SEND_MAIL` char(60) DEFAULT NULL,
                            `FROM_MAIL_NAME` char(60) DEFAULT NULL,
                            `FROM_PWD` char(30) DEFAULT NULL,
                            `SMTP_HOST` char(50) DEFAULT NULL,
                            `SMTP_PORT` char(30) DEFAULT NULL,
                            `SMTP_SSL` char(30) DEFAULT NULL,
                            `TO_MAIL` char(200) DEFAULT NULL,
                            `CPU_PER` char(30) DEFAULT NULL,
                            `CREATE_TIME` timestamp NULL DEFAULT NULL,
                            `MEM_PER` char(30) DEFAULT NULL,
                            `HEATH_PER` char(30) DEFAULT NULL,
                            PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for mem_state
-- ----------------------------
DROP TABLE IF EXISTS `mem_state`;
CREATE TABLE `mem_state` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for netio_state
-- ----------------------------
DROP TABLE IF EXISTS `netio_state`;
CREATE TABLE `netio_state` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for playbook
-- ----------------------------
DROP TABLE IF EXISTS `playbook`;
CREATE TABLE `playbook` (
                            `ID` varchar(50) NOT NULL,
                            `PLAYBOOK_NAME` varchar(100) NOT NULL,
                            `PLAYBOOK_DESC` varchar(500) DEFAULT NULL,
                            `CRON_EXPRESSION` varchar(100) DEFAULT NULL COMMENT 'CORN表达式，为空则为手动预案',
                            `IS_ENABLED` tinyint(1) DEFAULT 1 COMMENT '是否启用 0-否 1-是',
                            `TASK_STEPS` text DEFAULT NULL COMMENT '任务步骤，JSON格式',
                            `CREATE_TIME` datetime DEFAULT NULL,
                            `TIMEOUT_SECONDS` int(11) DEFAULT NULL,
                            PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='自动化预案/任务';

-- ----------------------------
-- Table structure for sys_load_state
-- ----------------------------
DROP TABLE IF EXISTS `sys_load_state`;
CREATE TABLE `sys_load_state` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for system_info
-- ----------------------------
DROP TABLE IF EXISTS `system_info`;
CREATE TABLE `system_info` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
                       `ID` varchar(50) NOT NULL,
                       `TAG_NAME` varchar(100) NOT NULL,
                       `TAG_DESC` varchar(255) DEFAULT NULL,
                       `TAG_COLOR` varchar(20) DEFAULT '#409EFF',
                       `CREATE_TIME` datetime DEFAULT NULL,
                       `LOG_PATH` text DEFAULT NULL,
                       PRIMARY KEY (`ID`),
                       UNIQUE KEY `UK_TAG_NAME` (`TAG_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='标签表';

-- ----------------------------
-- Table structure for tag_relation
-- ----------------------------
DROP TABLE IF EXISTS `tag_relation`;
CREATE TABLE `tag_relation` (
                                `ID` varchar(50) NOT NULL,
                                `TAG_ID` varchar(50) NOT NULL,
                                `RELATION_ID` varchar(50) NOT NULL,
                                `RELATION_TYPE` varchar(50) NOT NULL,
                                PRIMARY KEY (`ID`),
                                KEY `IDX_TAG_RELATION` (`TAG_ID`,`RELATION_TYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='标签关系表';

-- ----------------------------
-- Table structure for tcp_state
-- ----------------------------
DROP TABLE IF EXISTS `tcp_state`;
CREATE TABLE `tcp_state` (
                             `ID` char(32) NOT NULL,
                             `HOST_NAME` char(30) DEFAULT NULL,
                             `ACTIVE` char(30) DEFAULT NULL,
                             `PASSIVE` char(30) DEFAULT NULL,
                             `RETRANS` char(30) DEFAULT NULL,
                             `DATE_STR` char(30) DEFAULT NULL,
                             `CREATE_TIME` timestamp NULL DEFAULT NULL,
                             PRIMARY KEY (`ID`),
                             KEY `TCP_ACC_HOST_INDEX` (`HOST_NAME`,`CREATE_TIME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for thread_mon
-- ----------------------------
DROP TABLE IF EXISTS `thread_mon`;
CREATE TABLE `thread_mon` (
                              `ID` char(32) NOT NULL COMMENT '主键',
                              `TASK_NAME` varchar(100) DEFAULT NULL COMMENT '任务名称',
                              `PROCESS_KEYWORD` varchar(200) DEFAULT NULL COMMENT '进程关键字',
                              `ALERT_RULES` varchar(1000) DEFAULT NULL COMMENT '告警规则JSON',
                              `ACTIVE` char(1) DEFAULT '1' COMMENT '任务状态，1-开启，0-停止',
                              `TARGET_TAGS` text DEFAULT NULL COMMENT '目标标签JSON',
                              `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
                              PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='进程/线程监控任务表';

-- ----------------------------
-- Table structure for thread_mon_detail
-- ----------------------------
DROP TABLE IF EXISTS `thread_mon_detail`;
CREATE TABLE `thread_mon_detail` (
                                     `ID` char(36) NOT NULL,
                                     `THREAD_MON_ID` char(36) DEFAULT NULL,
                                     `HOSTNAME` varchar(255) DEFAULT NULL,
                                     `PROCESS_KEYWORD` varchar(255) DEFAULT NULL,
                                     `STATUS` varchar(50) DEFAULT NULL,
                                     `LAST_HEARTBEAT` datetime DEFAULT NULL,
                                     `CREATE_TIME` datetime DEFAULT NULL,
                                     PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for thread_state
-- ----------------------------
DROP TABLE IF EXISTS `thread_state`;
CREATE TABLE `thread_state` (
                                `ID` char(36) NOT NULL,
                                `THREAD_MON_ID` char(36) DEFAULT NULL,
                                `TOTAL_THREADS` int(11) DEFAULT NULL,
                                `RUNNABLE_THREADS` int(11) DEFAULT NULL,
                                `BLOCKED_THREADS` int(11) DEFAULT NULL,
                                `WAITING_THREADS` int(11) DEFAULT NULL,
                                `TIMED_WAITING_THREADS` int(11) DEFAULT NULL,
                                `CREATE_TIME` datetime DEFAULT NULL,
                                `HOSTNAME` varchar(255) DEFAULT NULL,
                                PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

SET FOREIGN_KEY_CHECKS = 1;


-- ----------------------------
-- Table structure for container_info
-- ----------------------------
DROP TABLE IF EXISTS `container_info`;
CREATE TABLE `container_info` (
   `ID` char(32) NOT NULL COMMENT '主键',
   `HOST_ID` char(32) NOT NULL COMMENT '主机ID',
   `HOST_NAME` varchar(100) DEFAULT NULL COMMENT '主机名',
   `CONTAINER_ID` varchar(100) NOT NULL COMMENT '容器ID',
   `NAMES` varchar(255) NOT NULL COMMENT '容器名称',
   `IMAGE` varchar(255) DEFAULT NULL COMMENT '镜像',
   `STATE` varchar(50) DEFAULT NULL COMMENT '状态',
   `CPU_PER` double(8,2) DEFAULT NULL COMMENT 'CPU使用率',
   `MEM_PER` double(8,2) DEFAULT NULL COMMENT '内存使用率',
   `MEM_CACHE` varchar(50) DEFAULT NULL COMMENT '内存使用量',
   `UPTIME` varchar(100) DEFAULT NULL COMMENT '已运行时间',
   `ALERT_STATE` char(1) DEFAULT '0' COMMENT '告警状态：0-正常，1-已告警',
   `LAST_HEARTBEAT` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '心跳时间',
   PRIMARY KEY (`ID`),
   UNIQUE KEY `UK_HOST_CONTAINER` (`HOST_ID`, `NAMES`),
   KEY `CONTAINER_INFO_HOST_INDEX` (`HOST_ID`,`LAST_HEARTBEAT`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='容器信息表';