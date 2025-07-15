package com.wgcloud.util;

import com.wgcloud.entity.PingResult;
import com.wgcloud.entity.PingTask;
import com.wgcloud.service.PingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;

/**
 * PING监控线程，每个任务一个线程持续运行
 * @author WGCLOUD
 */
public class PingMonitorThread extends Thread {
    
    private static final Logger logger = LoggerFactory.getLogger(PingMonitorThread.class);
    
    private final PingTask task;
    private final PingService pingService;
    private volatile boolean running = false;
    private Date lastPingTime;
    private long totalPingCount = 0;
    private long failureCount = 0;
    
    public PingMonitorThread(PingTask task, PingService pingService) {
        this.task = task;
        this.pingService = pingService;
        this.setName("PingMonitor-" + task.getTaskName());
        this.setDaemon(true);
    }
    
    @Override
    public void run() {
        running = true;
        logger.info("PING监控线程启动: {}", task.getTaskName());
        
        String[] ips = task.getIpList().split(",");
        
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                for (String ip : ips) {
                    if (!running) break;
                    
                    String targetIp = ip.trim();
                    if (targetIp.isEmpty()) continue;
                    
                    PingResult result = performPing(targetIp);
                    totalPingCount++;
                    
                    if (!result.getIsReachable()) {
                        failureCount++;
                    }
                    
                    // 根据配置决定是否记录结果
                    if (task.isRecordAllResults() || !result.getIsReachable()) {
                        result.setTaskId(task.getId());
                        result.setTargetIp(targetIp);
                        result.setPingTime(new Date());
                        
                        try {
                            pingService.savePingResult(result);
                        } catch (Exception e) {
                            logger.error("保存PING结果失败: {}", e.getMessage());
                        }
                        
                        // 如果PING失败，记录告警日志
                        if (!result.getIsReachable()) {
                            logger.warn("PING失败告警 - 任务: {}, 目标IP: {}, 错误: {}", 
                                       task.getTaskName(), targetIp, result.getErrorMessage());
                            // TODO: 触发告警通知
                            // triggerAlert(task, targetIp, result);
                        }
                    }
                    
                    lastPingTime = new Date();
                }
                
                // 等待下次PING
                Thread.sleep(task.getPingInterval() * 1000L);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("PING监控异常: {}", task.getTaskName(), e);
                try {
                    Thread.sleep(5000); // 异常后等待5秒再继续
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        running = false;
        logger.info("PING监控线程停止: {}", task.getTaskName());
    }
    
    /**
     * 执行实际的PING操作
     */
    private PingResult performPing(String host) {
        try {
            // 使用Java原生InetAddress进行PING
            InetAddress address = InetAddress.getByName(host);
            long startTime = System.currentTimeMillis();
            boolean isReachable = address.isReachable(task.getTimeout());
            long responseTime = System.currentTimeMillis() - startTime;
            
            if (isReachable) {
                return new PingResult(true, responseTime, 0.0, null);
            } else {
                return new PingResult(false, 0, 100.0, "Host unreachable");
            }
            
        } catch (Exception e) {
            return new PingResult(false, 0, 100.0, e.getMessage());
        }
    }
    
    /**
     * 停止监控
     */
    public void stopMonitoring() {
        running = false;
        this.interrupt();
    }
    
    /**
     * 触发PING失败告警（预留接口）
     */
    private void triggerAlert(PingTask task, String targetIp, PingResult result) {
        // TODO: 集成告警系统
        // 构造告警事件
        // AlertEvent alertEvent = new AlertEvent();
        // alertEvent.setAlertType("PING_FAILURE");
        // alertEvent.setResourceType("PING_TASK");
        // alertEvent.setResourceId(task.getId());
        // alertEvent.setResourceName(task.getTaskName());
        // alertEvent.setAlertContent(String.format("PING失败: %s 无法访问", targetIp));
        // alertEvent.setAlertLevel("WARNING");
        // alertEvent.setCreateTime(new Date());
        
        // 发送到告警系统
        // alertService.processAlert(alertEvent);
        
        logger.warn("PING告警: 任务[{}] 目标IP[{}] 不可达", task.getTaskName(), targetIp);
    }
    
    // getter方法
    public boolean isRunning() { 
        return running; 
    }
    
    public Date getLastPingTime() { 
        return lastPingTime; 
    }
    
    public long getTotalPingCount() { 
        return totalPingCount; 
    }
    
    public long getFailureCount() { 
        return failureCount; 
    }
    
    public PingTask getTask() { 
        return task; 
    }
    
    /**
     * 获取成功率
     */
    public double getSuccessRate() {
        if (totalPingCount == 0) {
            return 0.0;
        }
        return (double) (totalPingCount - failureCount) / totalPingCount * 100;
    }
    
    /**
     * 重置统计信息
     */
    public void resetStatistics() {
        totalPingCount = 0;
        failureCount = 0;
        lastPingTime = null;
    }
    
    @Override
    public String toString() {
        return "PingMonitorThread{" +
                "taskName='" + task.getTaskName() + '\'' +
                ", running=" + running +
                ", totalPingCount=" + totalPingCount +
                ", failureCount=" + failureCount +
                ", successRate=" + getSuccessRate() + "%" +
                ", lastPingTime=" + lastPingTime +
                '}';
    }
} 