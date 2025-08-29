package com.wgcloud.dto;

import lombok.Data;

import java.util.List;

@Data
public class LogMonitorReportByHostDto {
    private String hostname;
    private List<String> tags;
    private String overallStatus;
    private List<MonTaskStatusDto> monTasks;

}
