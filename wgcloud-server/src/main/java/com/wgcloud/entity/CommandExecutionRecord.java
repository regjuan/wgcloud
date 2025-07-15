package com.wgcloud.entity;

import java.util.Date;

/**
 * @version v2.3
 * @ClassName:CommandExecutionRecord.java
 * @author: http://www.wgstart.com
 * @date: 2024年01月16日
 * @Description: 指令执行记录
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
public class CommandExecutionRecord extends BaseEntity {

    /**
     *
     */
    private static final long serialVersionUID = -2913111613773445951L;

    /**
     * 指令任务ID
     */
    private String commandTaskId;

    /**
     * 主机IP
     */
    private String hostname;

    /**
     * 主机描述
     */
    private String hostDescription;

    /**
     * 下发指令内容
     */
    private String commandContent;

    /**
     * 指令下发时间
     */
    private Date executeTime;

    /**
     * 执行完成时间
     */
    private Date completeTime;

    /**
     * 执行状态：success-成功/failed-失败/timeout-超时/pending-等待执行/running-执行中
     */
    private String status;

    /**
     * 指令执行结果
     */
    private String result;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 执行耗时（毫秒）
     */
    private Long executionTime;

    /**
     * 退出码
     */
    private Integer exitCode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 执行节点信息
     */
    private String executorInfo;

    public CommandExecutionRecord() {
        this.createTime = new Date();
        this.updateTime = new Date();
        this.status = "pending";
        this.retryCount = 0;
    }

    public String getCommandTaskId() {
        return commandTaskId;
    }

    public void setCommandTaskId(String commandTaskId) {
        this.commandTaskId = commandTaskId;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getHostDescription() {
        return hostDescription;
    }

    public void setHostDescription(String hostDescription) {
        this.hostDescription = hostDescription;
    }

    public String getCommandContent() {
        return commandContent;
    }

    public void setCommandContent(String commandContent) {
        this.commandContent = commandContent;
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

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public Integer getExitCode() {
        return exitCode;
    }

    public void setExitCode(Integer exitCode) {
        this.exitCode = exitCode;
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
} 