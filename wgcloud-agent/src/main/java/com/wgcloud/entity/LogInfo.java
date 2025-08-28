package com.wgcloud.entity;

import lombok.Data;

import java.util.Date;


@Data
public class LogInfo extends BaseEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1565538727002722890L;

    private String LogMonId;

    /**
     * host名称
     */
    private String hostname;

    /**
     * 描述
     */
    private String infoContent;

    /**
     * 0成功，1失败
     */
    private String state;


    /**
     * 创建时间
     */
    private Date createTime;




}