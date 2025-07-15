package com.wgcloud.entity;

import java.util.Date;

/**
 * @version v2.3
 * @ClassName:ScheduledTask.java
 * @author: http://www.wgstart.com
 * @date: 2024年01月16日
 * @Description: 计划任务实体类
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
public class ScheduledTask extends BaseEntity {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = -2913111613773445952L;

    /**
     * 目标主机IP
     */
    private String hostname;

    /**
     * 计划任务名称
     */
    private String taskName;

    /**
     * 执行时间（Cron表达式）
     */
    private String cronExpression;

    /**
     * 执行指令
     */
    private String command;

    /**
     * 执行状态：success-成功/failed-失败/running-运行中/pending-等待中
     */
    private String status;

    /**
     * 最新执行时间
     */
    private Date lastExecuteTime;

    /**
     * 是否启用：true-启用/false-禁用
     */
    private Boolean enabled;

    /**
     * 标签（JSON格式存储）
     */
    private String tags;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 超时时间（秒）
     */
    private Integer timeoutSeconds;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    private Integer maxRetryCount;

    /**
     * 执行结果
     */
    private String lastResult;

    /**
     * 错误信息
     */
    private String lastErrorMessage;

    /**
     * 执行耗时（毫秒）
     */
    private Long lastExecutionTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 下次执行时间
     */
    private Date nextExecuteTime;

    public ScheduledTask() {
        this.enabled = true;
        this.status = "pending";
        this.retryCount = 0;
        this.maxRetryCount = 3;
        this.timeoutSeconds = 300; // 默认5分钟超时
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastExecuteTime() {
        return lastExecuteTime;
    }

    public void setLastExecuteTime(Date lastExecuteTime) {
        this.lastExecuteTime = lastExecuteTime;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public String getLastResult() {
        return lastResult;
    }

    public void setLastResult(String lastResult) {
        this.lastResult = lastResult;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public void setLastErrorMessage(String lastErrorMessage) {
        this.lastErrorMessage = lastErrorMessage;
    }

    public Long getLastExecutionTime() {
        return lastExecutionTime;
    }

    public void setLastExecutionTime(Long lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getNextExecuteTime() {
        return nextExecuteTime;
    }

    public void setNextExecuteTime(Date nextExecuteTime) {
        this.nextExecuteTime = nextExecuteTime;
    }
} 