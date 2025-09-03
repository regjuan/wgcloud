package com.wgcloud.entity;

import com.wgcloud.util.DateUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

@Data
public class ThreadState extends BaseEntity {

    private static final long serialVersionUID = -2913111613773445951L;

    private String hostname;
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
