package com.wgcloud.entity;

import com.wgcloud.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class ThreadState extends BaseEntity {

    private static final long serialVersionUID = -2913111613773445951L;

    /**
     * 线程监控任务ID
     */
    private String threadMonId;

    /**
     * 总线程数
     */
    private Integer totalThreads;

    /**
     * RUNNABLE状态线程数
     */
    private Integer runnableThreads;

    /**
     * BLOCKED状态线程数
     */
    private Integer blockedThreads;

    /**
     * WAITING状态线程数
     */
    private Integer waitingThreads;

    /**
     * TIMED_WAITING状态线程数
     */
    private Integer timedWaitingThreads;

    /**
     * 添加时间 MM-dd hh:mm:ss
     */
    private String dateStr;

    /**
     * 创建时间
     */
    private Date createTime;

    public String getThreadMonId() {
        return threadMonId;
    }

    public void setThreadMonId(String threadMonId) {
        this.threadMonId = threadMonId;
    }

    public Integer getTotalThreads() {
        return totalThreads;
    }

    public void setTotalThreads(Integer totalThreads) {
        this.totalThreads = totalThreads;
    }

    public Integer getRunnableThreads() {
        return runnableThreads;
    }

    public void setRunnableThreads(Integer runnableThreads) {
        this.runnableThreads = runnableThreads;
    }

    public Integer getBlockedThreads() {
        return blockedThreads;
    }

    public void setBlockedThreads(Integer blockedThreads) {
        this.blockedThreads = blockedThreads;
    }

    public Integer getWaitingThreads() {
        return waitingThreads;
    }

    public void setWaitingThreads(Integer waitingThreads) {
        this.waitingThreads = waitingThreads;
    }

    public Integer getTimedWaitingThreads() {
        return timedWaitingThreads;
    }

    public void setTimedWaitingThreads(Integer timedWaitingThreads) {
        this.timedWaitingThreads = timedWaitingThreads;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
