package com.wgcloud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @version v3.3
 * @ClassName:ContainerInfo.java
 * @author: http://www.wgstart.com
 * @date: 2025年9月8日
 * @Description: 容器信息
 * @Copyright: 2017-2025 wgcloud. All rights reserved.
 */
@Data
public class ContainerInfo extends BaseEntity {

    private static final long serialVersionUID = 879979812204191284L;

    /**
     * 主机ID
     */
    private String hostId;

    /**
     * 主机名
     */
    private String hostName;

    /**
     * 容器ID
     */
    private String containerId;

    /**
     * 容器名称
     */
    private String names;

    /**
     * 镜像
     */
    private String image;

    /**
     * 状态
     */
    private String state;

    /**
     * CPU使用率
     */
    private Double cpuPer;

    /**
     * 内存使用率
     */
    private Double memPer;

    /**
     * 内存使用量
     */
    private String memCache;

    /**
     * 已运行时间
     */
    private String uptime;

    /**
     * 告警状态：0-正常，1-已告警
     */
    private String alertState;

    /**
     * 心跳时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastHeartbeat;

    /* 分页参 */
    private Integer page;
    private Integer pageSize;
}
