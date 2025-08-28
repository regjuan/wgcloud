package com.wgcloud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @version v2.3
 * @ClassName:SystemInfo.java
 * @author: http://www.wgstart.com
 * @date: 2019年11月16日
 * @Description: 查看系统信息
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Data
public class SystemInfo extends BaseEntity {


    /**
     *
     */
    private static final long serialVersionUID = 879979812204191283L;


    /**
     * host名称 / 即 IP
     */
    private String hostname;

    /**
     * 系统版本信息
     */
    private String version;

    /**
     * 系统版本详细信息
     */
    private String versionDetail;

    /**
     * 内存使用率
     */
    private Double memPer;

    /**
     * core的个数(即核数)
     */
    private String cpuCoreNum;

    /**
     * cpu使用率
     */
    private Double cpuPer;

    /**
     * CPU型号信息
     */
    private String cpuXh;


    /**
     * 主机状态，1正常，2下线
     */
    private String state;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    //磁盘总使用率%
    private Double diskPer;

    /**
     * 主机备注
     */
    private String remark;

    /**
     * 标签
     */
    private String tags;

    /**
     * 标签名称列表
     */
    private String tagNameList;



}