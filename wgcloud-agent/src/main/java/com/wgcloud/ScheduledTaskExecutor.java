package com.wgcloud;

import com.wgcloud.entity.ScheduledTask;
import com.wgcloud.entity.ScheduledTaskRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @version v2.3
 * @ClassName:ScheduledTaskExecutor.java
 * @author: http://www.wgstart.com
 * @date: 2024年01月16日
 * @Description: 计划任务执行器 - Agent端
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Component
@RestController
public class ScheduledTaskExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskExecutor.class);

    /**
     * 立即执行计划任务
     */
    @PostMapping("/scheduledTask/executeImmediately")
    public ScheduledTaskResult executeImmediately(@RequestBody ScheduledTask task) {
        logger.info("开始执行计划任务：{}", task.getTaskName());
        return executeTask(task, true);
    }

    /**
     * 执行计划任务
     */
    @PostMapping("/scheduledTask/execute")
    public ScheduledTaskResult executeTask(@RequestBody ScheduledTask task) {
        logger.info("执行计划任务：{}", task.getTaskName());
        return executeTask(task, false);
    }

    /**
     * 执行任务的核心方法
     */
    public ScheduledTaskResult executeTask(ScheduledTask task, boolean isManualTrigger) {
        long startTime = System.currentTimeMillis();
        ScheduledTaskResult result = new ScheduledTaskResult();
        
        try {
            // 设置基本信息
            result.setTaskId(task.getId());
            result.setTaskName(task.getTaskName());
            result.setHostname(task.getHostname());
            result.setCommand(task.getCommand());
            result.setStartTime(new Date(startTime));
            result.setManualTrigger(isManualTrigger);
            result.setStatus("running");
            
            logger.info("执行任务 [{}] 开始，命令：{}", task.getTaskName(), task.getCommand());
            
            // 构建进程
            ProcessBuilder processBuilder = createProcessBuilder(task.getCommand());
            Process process = processBuilder.start();
            
            // 设置超时时间
            int timeoutSeconds = task.getTimeoutSeconds() != null ? task.getTimeoutSeconds() : 300;
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            
            if (!finished) {
                // 超时处理
                process.destroyForcibly();
                result.setStatus("timeout");
                result.setErrorMessage("任务执行超时（" + timeoutSeconds + "秒）");
                logger.warn("任务 [{}] 执行超时", task.getTaskName());
            } else {
                // 正常完成
                int exitCode = process.exitValue();
                result.setExitCode(exitCode);
                
                // 读取输出结果
                String output = readProcessOutput(process);
                result.setResult(output);
                
                if (exitCode == 0) {
                    result.setStatus("success");
                    logger.info("任务 [{}] 执行成功", task.getTaskName());
                } else {
                    result.setStatus("failed");
                    result.setErrorMessage("进程退出码: " + exitCode);
                    logger.warn("任务 [{}] 执行失败，退出码：{}", task.getTaskName(), exitCode);
                }
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            result.setStatus("failed");
            result.setErrorMessage("任务执行被中断: " + e.getMessage());
            logger.error("任务 [{}] 执行被中断", task.getTaskName(), e);
        } catch (IOException e) {
            result.setStatus("failed");
            result.setErrorMessage("IO异常: " + e.getMessage());
            logger.error("任务 [{}] 执行IO异常", task.getTaskName(), e);
        } catch (Exception e) {
            result.setStatus("failed");
            result.setErrorMessage("执行异常: " + e.getMessage());
            logger.error("任务 [{}] 执行异常", task.getTaskName(), e);
        }
        
        // 计算执行时间
        long endTime = System.currentTimeMillis();
        result.setDuration(endTime - startTime);
        result.setCompleteTime(new Date(endTime));
        
        logger.info("任务 [{}] 执行完成，状态：{}，耗时：{}ms", 
            task.getTaskName(), result.getStatus(), result.getDuration());
        
        return result;
    }

    /**
     * 创建进程构建器
     */
    private ProcessBuilder createProcessBuilder(String command) {
        ProcessBuilder processBuilder;
        String os = System.getProperty("os.name").toLowerCase();
        
        if (os.contains("win")) {
            // Windows系统
            processBuilder = new ProcessBuilder("cmd", "/c", command);
        } else {
            // Linux/Unix系统
            processBuilder = new ProcessBuilder("bash", "-c", command);
        }
        
        // 合并标准输出和错误输出
        processBuilder.redirectErrorStream(true);
        
        return processBuilder;
    }

    /**
     * 读取进程输出
     */
    private String readProcessOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), "UTF-8"))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        
        return output.toString();
    }

    /**
     * 检测任务执行能力
     */
    @PostMapping("/scheduledTask/checkCapability")
    public boolean checkCapability() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;
            
            if (os.contains("win")) {
                processBuilder = new ProcessBuilder("cmd", "/c", "echo test");
            } else {
                processBuilder = new ProcessBuilder("bash", "-c", "echo test");
            }
            
            Process process = processBuilder.start();
            boolean finished = process.waitFor(5, TimeUnit.SECONDS);
            
            return finished && process.exitValue() == 0;
        } catch (Exception e) {
            logger.error("检测任务执行能力失败", e);
            return false;
        }
    }

    /**
     * 计划任务执行结果
     */
    public static class ScheduledTaskResult {
        private String taskId;
        private String taskName;
        private String hostname;
        private String command;
        private Date startTime;
        private Date completeTime;
        private String status;
        private String result;
        private String errorMessage;
        private Long duration;
        private Integer exitCode;
        private Boolean manualTrigger;

        // 构造方法
        public ScheduledTaskResult() {
            this.manualTrigger = false;
        }

        public ScheduledTaskResult(String status, String result, String errorMessage) {
            this();
            this.status = status;
            this.result = result;
            this.errorMessage = errorMessage;
        }

        // Getter和Setter方法
        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public Date getCompleteTime() {
            return completeTime;
        }

        public void setCompleteTime(Date completeTime) {
            this.completeTime = completeTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public Long getDuration() {
            return duration;
        }

        public void setDuration(Long duration) {
            this.duration = duration;
        }

        public Integer getExitCode() {
            return exitCode;
        }

        public void setExitCode(Integer exitCode) {
            this.exitCode = exitCode;
        }

        public Boolean getManualTrigger() {
            return manualTrigger;
        }

        public void setManualTrigger(Boolean manualTrigger) {
            this.manualTrigger = manualTrigger;
        }
    }
} 