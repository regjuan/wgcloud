package com.wgcloud.entity;

import com.wgcloud.util.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @version v2.3
 * @ClassName:DockerState.java
 * @author: http://www.wgstart.com
 * @date: 2024年01月16日
 * @Description: Docker容器状态监控
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
public class DockerState extends BaseEntity {

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
     * CPU使用率 (%)
     */
    private Double cpuPer;

    /**
     * 内存使用率 (%)
     */
    private Double memPer;

    /**
     * 内存使用量 (MB)
     */
    private Double memUsage;

    /**
     * 内存限制 (MB)
     */
    private Double memLimit;

    /**
     * 网络输入流量 (Bytes)
     */
    private Long netInput;

    /**
     * 网络输出流量 (Bytes)
     */
    private Long netOutput;

    /**
     * 磁盘读取 (Bytes)
     */
    private Long diskRead;

    /**
     * 磁盘写入 (Bytes)
     */
    private Long diskWrite;

    /**
     * 进程数量
     */
    private Integer processCount;

    /**
     * 容器状态
     */
    private String status;

    /**
     * 添加时间
     * MM-dd hh:mm:ss
     */
    private String dateStr;

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

    public Double getCpuPer() {
        return cpuPer;
    }

    public void setCpuPer(Double cpuPer) {
        this.cpuPer = cpuPer;
    }

    public Double getMemPer() {
        return memPer;
    }

    public void setMemPer(Double memPer) {
        this.memPer = memPer;
    }

    public Double getMemUsage() {
        return memUsage;
    }

    public void setMemUsage(Double memUsage) {
        this.memUsage = memUsage;
    }

    public Double getMemLimit() {
        return memLimit;
    }

    public void setMemLimit(Double memLimit) {
        this.memLimit = memLimit;
    }

    public Long getNetInput() {
        return netInput;
    }

    public void setNetInput(Long netInput) {
        this.netInput = netInput;
    }

    public Long getNetOutput() {
        return netOutput;
    }

    public void setNetOutput(Long netOutput) {
        this.netOutput = netOutput;
    }

    public Long getDiskRead() {
        return diskRead;
    }

    public void setDiskRead(Long diskRead) {
        this.diskRead = diskRead;
    }

    public Long getDiskWrite() {
        return diskWrite;
    }

    public void setDiskWrite(Long diskWrite) {
        this.diskWrite = diskWrite;
    }

    public Integer getProcessCount() {
        return processCount;
    }

    public void setProcessCount(Integer processCount) {
        this.processCount = processCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDateStr() {
        String str = DateUtil.getDateTimeString(createTime);
        if (!StringUtils.isEmpty(str) && str.length() > 16) {
            return str.substring(5);
        }
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
} 