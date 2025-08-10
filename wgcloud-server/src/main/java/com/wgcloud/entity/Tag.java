package com.wgcloud.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Tag {
    private String id;
    private String tagName;
    private String tagDesc;
    private String tagColor;
    private Date createTime;

    /* 分页参 */
    private Integer page;
    private Integer pageSize;
}