package com.wgcloud.util;

import com.wgcloud.entity.PingTask;
import com.wgcloud.service.PingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PING监控管理器 - 管理所有PING监控线程
 * @author WGCLOUD
 */
@Component
public class PingMonitorManager {
    
    private static final Logger logger = LoggerFactory.getLogger(PingMonitorManager.class);
    
    private final Map<String, PingMonitorThread> activeThreads = new ConcurrentHashMap<>();
    
    @Autowired
    private PingService pingService;
    
    /**
     * 启动应用时恢复所有启用的PING任务
     */
    @PostConstruct
    public void initializePingTasks() {
        try {
            List<PingTask> enabledTasks = pingService.selectAllEnabled();
            logger.info("初始化PING监控任务，共{}个启用的任务", enabledTasks.size());
            
            for (PingTask task : enabledTasks) {
                startPingTask(task);
            }
            
            logger.info("PING监控管理器初始化完成，启动了{}个监控线程", activeThreads.size());
        } catch (Exception e) {
            logger.error("初始化PING监控任务失败", e);
        }
    }
    
    /**
     * 应用关闭时停止所有监控线程
     */
    @PreDestroy
    public void shutdown() {
        logger.info("正在关闭PING监控管理器...");
        
        for (Map.Entry<String, PingMonitorThread> entry : activeThreads.entrySet()) {
            String taskId = entry.getKey();
            PingMonitorThread thread = entry.getValue();
            try {
                thread.stopMonitoring();
                logger.info("已停止PING监控任务: {}", taskId);
            } catch (Exception e) {
                logger.error("停止PING监控任务失败: {}", taskId, e);
            }
        }
        
        activeThreads.clear();
        logger.info("PING监控管理器已关闭");
    }
    
    /**
     * 启动PING监控任务
     */
    public synchronized void startPingTask(PingTask task) {
        if (task == null || task.getId() == null) {
            logger.warn("无效的PING任务，无法启动");
            return;
        }
        
        // 先停止已存在的任务
        stopPingTask(task.getId());
        
        try {
            PingMonitorThread thread = new PingMonitorThread(task, pingService);
            activeThreads.put(task.getId(), thread);
            thread.start();
            
            logger.info("启动PING监控任务: {} - {}", task.getTaskName(), task.getIpList());
        } catch (Exception e) {
            logger.error("启动PING监控任务失败: {}", task.getTaskName(), e);
        }
    }
    
    /**
     * 停止PING监控任务
     */
    public synchronized void stopPingTask(String taskId) {
        if (taskId == null) {
            return;
        }
        
        PingMonitorThread thread = activeThreads.remove(taskId);
        if (thread != null) {
            try {
                thread.stopMonitoring();
                logger.info("停止PING监控任务: {}", taskId);
            } catch (Exception e) {
                logger.error("停止PING监控任务失败: {}", taskId, e);
            }
        }
    }
    
    /**
     * 重启PING监控任务（配置更新后）
     */
    public synchronized void restartPingTask(PingTask task) {
        if (task == null) {
            return;
        }
        
        stopPingTask(task.getId());
        
        if (task.getIsEnabled()) {
            startPingTask(task);
        }
    }
    
    /**
     * 重启PING监控任务（根据任务ID）
     */
    public synchronized void restartPingTask(String taskId) {
        try {
            PingTask task = pingService.selectById(taskId);
            if (task != null) {
                restartPingTask(task);
            }
        } catch (Exception e) {
            logger.error("重启PING监控任务失败: {}", taskId, e);
        }
    }
    
    /**
     * 获取所有运行中的任务状态
     */
    public Map<String, PingTaskStatus> getRunningTasksStatus() {
        Map<String, PingTaskStatus> statusMap = new HashMap<>();
        
        for (Map.Entry<String, PingMonitorThread> entry : activeThreads.entrySet()) {
            String taskId = entry.getKey();
            PingMonitorThread thread = entry.getValue();
            
            PingTaskStatus status = new PingTaskStatus();
            status.setTaskId(taskId);
            status.setTaskName(thread.getTask().getTaskName());
            status.setRunning(thread.isRunning());
            status.setLastPingTime(thread.getLastPingTime());
            status.setTotalPingCount(thread.getTotalPingCount());
            status.setFailureCount(thread.getFailureCount());
            status.setSuccessRate(thread.getSuccessRate());
            
            statusMap.put(taskId, status);
        }
        
        return statusMap;
    }
    
    /**
     * 获取指定任务的运行状态
     */
    public PingTaskStatus getTaskStatus(String taskId) {
        PingMonitorThread thread = activeThreads.get(taskId);
        if (thread == null) {
            return null;
        }
        
        PingTaskStatus status = new PingTaskStatus();
        status.setTaskId(taskId);
        status.setTaskName(thread.getTask().getTaskName());
        status.setRunning(thread.isRunning());
        status.setLastPingTime(thread.getLastPingTime());
        status.setTotalPingCount(thread.getTotalPingCount());
        status.setFailureCount(thread.getFailureCount());
        status.setSuccessRate(thread.getSuccessRate());
        
        return status;
    }
    
    /**
     * 检查任务是否正在运行
     */
    public boolean isTaskRunning(String taskId) {
        PingMonitorThread thread = activeThreads.get(taskId);
        return thread != null && thread.isRunning();
    }
    
    /**
     * 获取运行中的任务数量
     */
    public int getRunningTaskCount() {
        return activeThreads.size();
    }
    
    /**
     * 重置任务统计信息
     */
    public void resetTaskStatistics(String taskId) {
        PingMonitorThread thread = activeThreads.get(taskId);
        if (thread != null) {
            thread.resetStatistics();
        }
    }
    
    /**
     * 重新加载所有PING任务
     */
    public synchronized void reloadAllTasks() {
        logger.info("重新加载所有PING任务...");
        
        // 停止所有现有任务
        for (String taskId : activeThreads.keySet()) {
            stopPingTask(taskId);
        }
        
        // 重新初始化
        initializePingTasks();
    }
    
    /**
     * 获取管理器状态信息
     */
    public Map<String, Object> getManagerStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("runningTaskCount", getRunningTaskCount());
        status.put("totalThreads", activeThreads.size());
        
        int runningThreads = 0;
        for (PingMonitorThread thread : activeThreads.values()) {
            if (thread.isRunning()) {
                runningThreads++;
            }
        }
        status.put("runningThreads", runningThreads);
        
        return status;
    }
    
    /**
     * PING任务运行状态类
     */
    public static class PingTaskStatus {
        private String taskId;
        private String taskName;
        private boolean running;
        private java.util.Date lastPingTime;
        private long totalPingCount;
        private long failureCount;
        private double successRate;
        
        // Getter and Setter methods
        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        
        public String getTaskName() { return taskName; }
        public void setTaskName(String taskName) { this.taskName = taskName; }
        
        public boolean isRunning() { return running; }
        public void setRunning(boolean running) { this.running = running; }
        
        public java.util.Date getLastPingTime() { return lastPingTime; }
        public void setLastPingTime(java.util.Date lastPingTime) { this.lastPingTime = lastPingTime; }
        
        public long getTotalPingCount() { return totalPingCount; }
        public void setTotalPingCount(long totalPingCount) { this.totalPingCount = totalPingCount; }
        
        public long getFailureCount() { return failureCount; }
        public void setFailureCount(long failureCount) { this.failureCount = failureCount; }
        
        public double getSuccessRate() { return successRate; }
        public void setSuccessRate(double successRate) { this.successRate = successRate; }
        
        @Override
        public String toString() {
            return "PingTaskStatus{" +
                    "taskId='" + taskId + '\'' +
                    ", taskName='" + taskName + '\'' +
                    ", running=" + running +
                    ", totalPingCount=" + totalPingCount +
                    ", failureCount=" + failureCount +
                    ", successRate=" + successRate + "%" +
                    ", lastPingTime=" + lastPingTime +
                    '}';
        }
    }
} 