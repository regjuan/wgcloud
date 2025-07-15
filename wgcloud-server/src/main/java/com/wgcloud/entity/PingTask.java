package com.wgcloud.entity;

import java.util.Date;
import java.util.List;

/**
 * Ping监控任务配置 - Server端执行
 * @author WGCLOUD
 */
public class PingTask {
    
    /**
     * 任务ID
     */
    private String id;
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * IP列表，多个IP用逗号分隔
     */
    private String ipList;
    
    /**
     * ping间隔时间（秒）
     */
    private int pingInterval;
    
    /**
     * 超时时间（毫秒）
     */
    private int timeout;
    
    /**
     * 是否记录所有结果（0：仅失败，1：全部）
     */
    private boolean recordAllResults;
    
    /**
     * 是否启用（0：禁用，1：启用）
     */
    private boolean isEnabled;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 标签，JSON格式
     */
    private String tags;
    
    /**
     * 是否启用告警
     */
    private boolean alertEnabled;
    
    /**
     * 响应时间告警阈值（毫秒）
     */
    private double responseTimeThreshold;
    
    /**
     * 丢包率告警阈值（百分比）
     */
    private double packetLossThreshold;
    
    /**
     * 连续失败次数告警阈值
     */
    private int failureCountThreshold;
    
    public PingTask() {
        this.createTime = new Date();
        this.updateTime = new Date();
        this.isEnabled = true;
        this.recordAllResults = false;
        this.pingInterval = 60; // 默认60秒
        this.timeout = 5000; // 默认5秒超时
        this.alertEnabled = true;
        this.responseTimeThreshold = 1000; // 默认1秒响应时间告警
        this.packetLossThreshold = 10; // 默认10%丢包率告警
        this.failureCountThreshold = 3; // 默认连续失败3次告警
    }
    
    // Getter and Setter methods
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTaskName() {
        return taskName;
    }
    
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getIpList() {
        return ipList;
    }
    
    public void setIpList(String ipList) {
        this.ipList = ipList;
    }
    
    public int getPingInterval() {
        return pingInterval;
    }
    
    public void setPingInterval(int pingInterval) {
        this.pingInterval = pingInterval;
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public boolean isRecordAllResults() {
        return recordAllResults;
    }
    
    public void setRecordAllResults(boolean recordAllResults) {
        this.recordAllResults = recordAllResults;
    }
    
    public boolean getIsEnabled() {
        return isEnabled;
    }
    
    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
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
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public boolean isAlertEnabled() {
        return alertEnabled;
    }
    
    public void setAlertEnabled(boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }
    
    public double getResponseTimeThreshold() {
        return responseTimeThreshold;
    }
    
    public void setResponseTimeThreshold(double responseTimeThreshold) {
        this.responseTimeThreshold = responseTimeThreshold;
    }
    
    public double getPacketLossThreshold() {
        return packetLossThreshold;
    }
    
    public void setPacketLossThreshold(double packetLossThreshold) {
        this.packetLossThreshold = packetLossThreshold;
    }
    
    public int getFailureCountThreshold() {
        return failureCountThreshold;
    }
    
    public void setFailureCountThreshold(int failureCountThreshold) {
        this.failureCountThreshold = failureCountThreshold;
    }
    
    @Override
    public String toString() {
        return "PingTask{" +
                "id='" + id + '\'' +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", ipList='" + ipList + '\'' +
                ", pingInterval=" + pingInterval +
                ", timeout=" + timeout +
                ", recordAllResults=" + recordAllResults +
                ", isEnabled=" + isEnabled +
                ", tags='" + tags + '\'' +
                ", alertEnabled=" + alertEnabled +
                ", responseTimeThreshold=" + responseTimeThreshold +
                ", packetLossThreshold=" + packetLossThreshold +
                ", failureCountThreshold=" + failureCountThreshold +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
} 