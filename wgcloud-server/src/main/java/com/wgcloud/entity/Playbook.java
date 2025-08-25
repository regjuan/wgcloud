package com.wgcloud.entity;

import com.wgcloud.dto.TaskStep;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class Playbook {
    private String id;
    private String playbookName;
    private String playbookDesc;
    private String cronExpression;
    private Integer isEnabled;
    // 存储规则JSON字符串
    private String taskSteps;
    private Date createTime;
    private Integer timeoutSeconds;

    /* 分页参 */
    private Integer page;
    private Integer pageSize;

    private List<TaskStep> taskStepList;
}