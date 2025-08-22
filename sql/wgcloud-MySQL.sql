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


DROP TABLE IF EXISTS `TAG`;
CREATE TABLE `TAG` (
    `ID` varchar(50) NOT NULL,
    `TAG_NAME` varchar(100) NOT NULL,
    `TAG_DESC` varchar(255) DEFAULT NULL,
    `TAG_COLOR` varchar(20) DEFAULT '#409EFF',
    `CREATE_TIME` datetime DEFAULT NULL,
    PRIMARY KEY (`ID`),
    UNIQUE KEY `UK_TAG_NAME` (`TAG_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

DROP TABLE IF EXISTS `TAG_RELATION`;
CREATE TABLE `TAG_RELATION` (
    `ID` varchar(50) NOT NULL,
    `TAG_ID` varchar(50) NOT NULL,
    `RELATION_ID` varchar(50) NOT NULL,
    `RELATION_TYPE` varchar(50) NOT NULL,
    PRIMARY KEY (`ID`),
    KEY `IDX_TAG_RELATION` (`TAG_ID`,`RELATION_TYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='主机 - 标签关系表';


DROP TABLE IF EXISTS `COMMAND`;
CREATE TABLE `COMMAND` (
    `ID` varchar(50) NOT NULL,
    `CMD_NAME` varchar(100) NOT NULL,
    `CMD_CONTENT` text,
    `CMD_TYPE` varchar(20) DEFAULT 'SHELL',
    `TIMEOUT` int(11) DEFAULT 60,
    `CREATE_TIME` datetime DEFAULT NULL,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指令库';

DROP TABLE IF EXISTS `PLAYBOOK`;
CREATE TABLE `PLAYBOOK` (
    `ID` varchar(50) NOT NULL,
    `PLAYBOOK_NAME` varchar(100) NOT NULL,
    `PLAYBOOK_DESC` varchar(500) DEFAULT NULL,
    `CRON_EXPRESSION` varchar(100) DEFAULT NULL COMMENT 'CORN表达式，为空则为手动预案',
    `IS_ENABLED` tinyint(1) DEFAULT 1 COMMENT '是否启用 0-否 1-是',
    `TASK_STEPS` text COMMENT '任务步骤，JSON格式',
    `CREATE_TIME` datetime DEFAULT NULL,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自动化预案/任务';

DROP TABLE IF EXISTS `COMMAND_RESULT`;
CREATE TABLE `COMMAND_RESULT` (
                                  `ID` varchar(50) NOT NULL,
                                  `TASK_ID` varchar(50) DEFAULT NULL,
                                  `COMMAND_ID` varchar(50) NOT NULL,
                                  `HOSTNAME` varchar(100) NOT NULL,
                                  `STATUS` varchar(20) NOT NULL,
                                  `STDOUT` text,
                                  `STDERR` text,
                                  `EXIT_CODE` int(11) DEFAULT NULL,
                                  `START_TIME` datetime DEFAULT NULL,
                                  `END_TIME` datetime DEFAULT NULL,
                                  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指令执行历史结果';