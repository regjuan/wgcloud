package com.wgcloud.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Command {
    private String id;
    private String cmdName;
    private String cmdContent;
    private String cmdType;
    private Integer timeout;
    private Date createTime;

    /* 分页参 */
    private Integer page;
    private Integer pageSize;

    /* VO */
    private String tags;
    private String tagNameList;
}
