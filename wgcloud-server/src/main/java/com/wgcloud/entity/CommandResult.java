package com.wgcloud.entity;

import lombok.Data;
import java.util.Date;

@Data
public class CommandResult {
    private String id;
    private String taskId;
    private String commandId;
    private String hostname;
    private String status;
    private String stdout;
    private String stderr;
    private Integer exitCode;
    private Date startTime;
    private Date endTime;
    private Date expireTime;

    /* 分页参*/
    private Integer page;
    private Integer pageSize;

    /* VO字段 */
    private String taskName;
    private String commandName;
}
