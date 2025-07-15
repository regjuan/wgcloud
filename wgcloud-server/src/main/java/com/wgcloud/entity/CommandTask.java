package com.wgcloud.entity;

import java.util.Date;
import java.util.List;

/**
 * @version v2.3
 * @ClassName:CommandTask.java
 * @author: http://www.wgstart.com
 * @date: 2024年01月16日
 * @Description: 指令下发批量执行任务
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
public class CommandTask extends BaseEntity {

    /**
     *
     */
    private static final long serialVersionUID = -2913111613773445950L;

    /**
     * 指令名称
     */
    private String commandName;

    /**
     * 指令内容
     */
    private String commandContent;

    /**
     * 指令状态：已完成/进行中/失败
     */
    private String status;

    /**
     * 已执行数量
     */
    private Integer executedCount;

    /**
     * 总下发数量
     */
    private Integer totalCount;

    /**
     * 下发时间
     */
    private Date deployTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 目标标签（JSON格式存储）
     */
    private String targetTags;

    /**
     * 目标主机列表（JSON格式存储）
     */
    private String targetHosts;

    /**
     * 任务类型：immediate-立即执行，scheduled-定时执行
     */
    private String taskType;

    /**
     * 预定执行时间（定时任务用）
     */
    private Date scheduledTime;

    /**
     * 超时时间（秒）
     */
    private Integer timeoutSeconds;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 执行结果摘要
     */
    private String resultSummary;

    public CommandTask() {
        this.createTime = new Date();
        this.updateTime = new Date();
        this.executedCount = 0;
        this.totalCount = 0;
        this.status = "pending";
        this.taskType = "immediate";
        this.timeoutSeconds = 300; // 默认5分钟超时
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandContent() {
        return commandContent;
    }

    public void setCommandContent(String commandContent) {
        this.commandContent = commandContent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getExecutedCount() {
        return executedCount;
    }

    public void setExecutedCount(Integer executedCount) {
        this.executedCount = executedCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Date getDeployTime() {
        return deployTime;
    }

    public void setDeployTime(Date deployTime) {
        this.deployTime = deployTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTargetTags() {
        return targetTags;
    }

    public void setTargetTags(String targetTags) {
        this.targetTags = targetTags;
    }

    public String getTargetHosts() {
        return targetHosts;
    }

    public void setTargetHosts(String targetHosts) {
        this.targetHosts = targetHosts;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Date getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResultSummary() {
        return resultSummary;
    }

    public void setResultSummary(String resultSummary) {
        this.resultSummary = resultSummary;
    }
} 