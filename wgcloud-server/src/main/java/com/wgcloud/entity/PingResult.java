package com.wgcloud.entity;

import java.util.Date;

/**
 * Ping监控结果 - Server端执行结果
 * @author WGCLOUD
 */
public class PingResult {
    
    /**
     * 结果ID
     */
    private String id;
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 目标IP地址
     */
    private String targetIp;
    
    /**
     * 是否可达（0：不可达，1：可达）
     */
    private boolean isReachable;
    
    /**
     * 响应时间（毫秒）
     */
    private long responseTime;
    
    /**
     * 丢包率（百分比）
     */
    private double packetLoss;
    
    /**
     * PING时间
     */
    private Date pingTime;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    public PingResult() {
        this.pingTime = new Date();
        this.isReachable = false;
        this.responseTime = 0;
        this.packetLoss = 100.0;
    }
    
    public PingResult(boolean isReachable, long responseTime, double packetLoss, String errorMessage) {
        this();
        this.isReachable = isReachable;
        this.responseTime = responseTime;
        this.packetLoss = packetLoss;
        this.errorMessage = errorMessage;
    }
    
    // Getter and Setter methods
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public String getTargetIp() {
        return targetIp;
    }
    
    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }
    
    public boolean getIsReachable() {
        return isReachable;
    }
    
    public void setIsReachable(boolean isReachable) {
        this.isReachable = isReachable;
    }
    
    public long getResponseTime() {
        return responseTime;
    }
    
    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }
    
    public double getPacketLoss() {
        return packetLoss;
    }
    
    public void setPacketLoss(double packetLoss) {
        this.packetLoss = packetLoss;
    }
    
    public Date getPingTime() {
        return pingTime;
    }
    
    public void setPingTime(Date pingTime) {
        this.pingTime = pingTime;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    @Override
    public String toString() {
        return "PingResult{" +
                "id='" + id + '\'' +
                ", taskId='" + taskId + '\'' +
                ", targetIp='" + targetIp + '\'' +
                ", isReachable=" + isReachable +
                ", responseTime=" + responseTime +
                ", packetLoss=" + packetLoss +
                ", pingTime=" + pingTime +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
} 