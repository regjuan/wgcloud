package com.wgcloud.entity;

import java.util.Date;

/**
 * @version v2.3
 * @ClassName:DockerContainer.java
 * @author: http://www.wgstart.com
 * @date: 2024年01月16日
 * @Description: Docker容器基础信息
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
public class DockerContainer extends BaseEntity {

    /**
     *
     */
    private static final long serialVersionUID = -2913111613773445949L;

    /**
     * host名称
     */
    private String hostname;

    /**
     * 容器ID
     */
    private String containerId;

    /**
     * 容器名称
     */
    private String containerName;

    /**
     * 镜像名称
     */
    private String image;

    /**
     * 容器状态 (running, stopped, paused等)
     */
    private String status;

    /**
     * 端口映射
     */
    private String ports;

    /**
     * 启动命令
     */
    private String command;

    /**
     * 容器创建时间
     */
    private Date containerCreateTime;

    /**
     * 运行时间
     */
    private String uptime;

    /**
     * 容器大小
     */
    private String size;

    /**
     * 网络模式
     */
    private String networkMode;

    /**
     * 挂载卷
     */
    private String volumes;

    /**
     * 容器状态，1正常，2异常，3停止
     */
    private String state;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否启用告警，1启用，0不启用
     */
    private String alertEnabled;

    /**
     * CPU告警阈值
     */
    private Double cpuAlertThreshold;

    /**
     * 内存告警阈值
     */
    private Double memAlertThreshold;

    /**
     * 创建时间
     */
    private Date createTime;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPorts() {
        return ports;
    }

    public void setPorts(String ports) {
        this.ports = ports;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Date getContainerCreateTime() {
        return containerCreateTime;
    }

    public void setContainerCreateTime(Date containerCreateTime) {
        this.containerCreateTime = containerCreateTime;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getNetworkMode() {
        return networkMode;
    }

    public void setNetworkMode(String networkMode) {
        this.networkMode = networkMode;
    }

    public String getVolumes() {
        return volumes;
    }

    public void setVolumes(String volumes) {
        this.volumes = volumes;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAlertEnabled() {
        return alertEnabled;
    }

    public void setAlertEnabled(String alertEnabled) {
        this.alertEnabled = alertEnabled;
    }

    public Double getCpuAlertThreshold() {
        return cpuAlertThreshold;
    }

    public void setCpuAlertThreshold(Double cpuAlertThreshold) {
        this.cpuAlertThreshold = cpuAlertThreshold;
    }

    public Double getMemAlertThreshold() {
        return memAlertThreshold;
    }

    public void setMemAlertThreshold(Double memAlertThreshold) {
        this.memAlertThreshold = memAlertThreshold;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
} 