package com.wgcloud.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MonTaskStatusDto {
    private String taskId;
    private String taskName;
    private String filePath;
    private String status;
    private Date lastAlertTime;
    private int todayAlerts;

}
