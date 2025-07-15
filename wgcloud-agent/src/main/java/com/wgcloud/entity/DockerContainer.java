package com.wgcloud.entity;

import java.util.Date;

/**
 * @version V2.3
 * @ClassName:DockerContainer.java
 * @author: wgcloud
 * @date: 2024年01月16日
 * @Description: Docker容器基础信息
 * @Copyright: 2017-2024 www.wgstart.com. All rights reserved.
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
} 