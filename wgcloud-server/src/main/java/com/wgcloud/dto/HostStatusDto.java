package com.wgcloud.dto;

import lombok.Data;

import java.util.List;

@Data
public class HostStatusDto {
    private String hostname;
    private String ip;
    private List<String> tags;
    private String overallStatus;
    private List<MonTaskStatusDto> monTasks;

}
