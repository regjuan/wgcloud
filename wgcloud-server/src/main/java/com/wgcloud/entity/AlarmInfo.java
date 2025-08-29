package com.wgcloud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class AlarmInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主机名或IP地址
     */
    private String hostname;

    /**
     * 告警标题/类型
     */
    private String logTitle;

    /**
     * 告警详情
     */
    private String infoContent;

    /**
     * 告警来源
     */
    private String source;

    /**
     * 状态
     */
    private String state;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
