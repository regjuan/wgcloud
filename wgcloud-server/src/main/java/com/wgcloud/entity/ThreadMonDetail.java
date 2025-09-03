
package com.wgcloud.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ThreadMonDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String threadMonId;

    private String hostname;

    private String processKeyword;

    private String status;

    private Date lastHeartbeat;

    private Date createTime;


}
