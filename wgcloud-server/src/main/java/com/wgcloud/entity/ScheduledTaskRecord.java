package com.wgcloud.entity;

import java.util.Date;

/**
 * @version v2.3
 * @ClassName:ScheduledTaskRecord.java
 * @author: http://www.wgstart.com
 * @date: 2024年01月16日
 * @Description: 计划任务执行记录实体类
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
public class ScheduledTaskRecord extends BaseEntity {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = -2913111613773445953L;

    /**
     * 计划任务ID
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 目标主机IP
     */
    private String hostname;

    /**
     * 执行指令
     */
    private String command;

    /**
     * 执行时间
     */
    private Date executeTime;

    /**
     * 执行完成时间
     */
    private Date completeTime;

    /**
     * 执行状态：success-成功/failed-失败/timeout-超时/running-运行中
     */
    private String status;

    /**
     * 执行结果
     */
    private String result;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 执行耗时（毫秒）
     */
    private Long duration;

    /**
     * 退出码
     */
    private Integer exitCode;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 执行节点信息
     */
    private String executorInfo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否由手动触发
     */
    private Boolean manualTrigger;

    /**
     * 触发人
     */
    private String triggerBy;

    public ScheduledTaskRecord() {
        this.status = "running";
        this.manualTrigger = false;
        this.retryCount = 0;
        this.createTime = new Date();
        this.updateTime = new Date();
        this.executeTime = new Date();
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Date getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Date executeTime) {
        this.executeTime = executeTime;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Integer getExitCode() {
        return exitCode;
    }

    public void setExitCode(Integer exitCode) {
        this.exitCode = exitCode;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getExecutorInfo() {
        return executorInfo;
    }

    public void setExecutorInfo(String executorInfo) {
        this.executorInfo = executorInfo;
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

    public Boolean getManualTrigger() {
        return manualTrigger;
    }

    public void setManualTrigger(Boolean manualTrigger) {
        this.manualTrigger = manualTrigger;
    }

    public String getTriggerBy() {
        return triggerBy;
    }

    public void setTriggerBy(String triggerBy) {
        this.triggerBy = triggerBy;
    }
} 