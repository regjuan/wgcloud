package com.wgcloud.entity;

import java.util.Date;

/**
 * Ping监控结果
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
     * 主机名
     */
    private String hostname;
    
    /**
     * 目标IP
     */
    private String targetIp;
    
    /**
     * 是否可达
     */
    private boolean reachable;
    
    /**
     * 响应时间（毫秒）
     */
    private double responseTime;
    
    /**
     * 丢包率（百分比）
     */
    private double packetLoss;
    
    /**
     * ping包数量
     */
    private int packetCount;
    
    /**
     * 接收包数量
     */
    private int receivedCount;
    
    /**
     * 最小响应时间（毫秒）
     */
    private double minTime;
    
    /**
     * 最大响应时间（毫秒）
     */
    private double maxTime;
    
    /**
     * 平均响应时间（毫秒）
     */
    private double avgTime;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    public PingResult() {
        this.createTime = new Date();
    }
    
    public PingResult(String targetIp, boolean reachable, double responseTime, double packetLoss) {
        this();
        this.targetIp = targetIp;
        this.reachable = reachable;
        this.responseTime = responseTime;
        this.packetLoss = packetLoss;
    }
    
    public PingResult(String targetIp, boolean reachable, String errorMessage) {
        this();
        this.targetIp = targetIp;
        this.reachable = reachable;
        this.errorMessage = errorMessage;
    }
    
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
    
    public String getHostname() {
        return hostname;
    }
    
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    
    public String getTargetIp() {
        return targetIp;
    }
    
    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }
    
    public boolean isReachable() {
        return reachable;
    }
    
    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }
    
    public double getResponseTime() {
        return responseTime;
    }
    
    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }
    
    public double getPacketLoss() {
        return packetLoss;
    }
    
    public void setPacketLoss(double packetLoss) {
        this.packetLoss = packetLoss;
    }
    
    public int getPacketCount() {
        return packetCount;
    }
    
    public void setPacketCount(int packetCount) {
        this.packetCount = packetCount;
    }
    
    public int getReceivedCount() {
        return receivedCount;
    }
    
    public void setReceivedCount(int receivedCount) {
        this.receivedCount = receivedCount;
    }
    
    public double getMinTime() {
        return minTime;
    }
    
    public void setMinTime(double minTime) {
        this.minTime = minTime;
    }
    
    public double getMaxTime() {
        return maxTime;
    }
    
    public void setMaxTime(double maxTime) {
        this.maxTime = maxTime;
    }
    
    public double getAvgTime() {
        return avgTime;
    }
    
    public void setAvgTime(double avgTime) {
        this.avgTime = avgTime;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    @Override
    public String toString() {
        return "PingResult{" +
                "id='" + id + '\'' +
                ", taskId='" + taskId + '\'' +
                ", hostname='" + hostname + '\'' +
                ", targetIp='" + targetIp + '\'' +
                ", reachable=" + reachable +
                ", responseTime=" + responseTime +
                ", packetLoss=" + packetLoss +
                ", packetCount=" + packetCount +
                ", receivedCount=" + receivedCount +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime +
                ", avgTime=" + avgTime +
                ", errorMessage='" + errorMessage + '\'' +
                ", createTime=" + createTime +
                '}';
    }
} 