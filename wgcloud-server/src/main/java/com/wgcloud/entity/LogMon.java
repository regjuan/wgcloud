package com.wgcloud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;


@Data
public class LogMon extends BaseEntity {

    private static final long serialVersionUID = 1565538727002722891L;

    /**
     * 监控任务名称
     */
    private String appName;

    /**
     * 监控的文件或目录路径
     */
    private String filePath;

    /**
     * 匹配关键字(逗号隔开)
     */
    private String matchKeywords;

    /**
     * 排除关键字(逗号隔开)
     */
    private String unMatchKeywords;

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
