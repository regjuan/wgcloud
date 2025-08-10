package com.wgcloud.dto;

import lombok.Data;
import java.util.List;

@Data
public class TaskStep {
    private String stepName;
    private String commandId;
    private String targetType;
    private List<String> targets;

    // 非JSON字段，用于UI显示
    private String commandName;
    private String targetNames; // e.g., tag names
}
