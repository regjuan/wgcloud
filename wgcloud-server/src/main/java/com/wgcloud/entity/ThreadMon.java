package com.wgcloud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class ThreadMon extends BaseEntity {

    private static final long serialVersionUID = -2913111613773445950L;

    /**
     * 监控任务名称
     */
    private String taskName;

    /**
     * 进程关键字
     */
    private String processKeyword;

    /**
     * 告警规则(JSON)
     */
    private String alertRules;

    /**
     * 任务状态，1-开启，0-停止
     */
    private String active;

    /**
     * 目标标签列表(JSON)
     */
    private String targetTags;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 目标标签列表（非数据库字段）
     */
    private transient List<String> targetTagsList;

}
