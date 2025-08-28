package com.wgcloud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

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

    private String infoTitle;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;




}