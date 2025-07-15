package com.wgcloud;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @version V2.3
 * @ClassName: CommandAgentController
 * @author: wgcloud
 * @date: 2024年01月16日
 * @Description: Agent端指令执行控制器
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Controller
@RequestMapping("/agent")
public class CommandAgentController {

    private static final Logger logger = LoggerFactory.getLogger(CommandAgentController.class);
    
    @Autowired
    private CommonConfig commonConfig;
    
    @Autowired
    private RestUtil restUtil;
    
    // 指令执行线程池
    private static final Executor COMMAND_EXECUTOR = Executors.newFixedThreadPool(5);
    
    // 正在执行的任务缓存
    private static final ConcurrentHashMap<String, CommandExecution> runningTasks = new ConcurrentHashMap<>();
    
    /**
     * 接收并执行指令
     */
    @ResponseBody
    @RequestMapping("/executeCommand")
    public JSONObject executeCommand(@RequestBody String paramBean) {
        JSONObject result = new JSONObject();
        
        try {
            JSONObject requestJson = JSONUtil.parseObj(paramBean);
            
            // 验证token
            if (!validateToken(requestJson)) {
                result.put("status", "failed");
                result.put("errorMessage", "Token验证失败");
                return result;
            }
            
            String command = requestJson.getStr("command");
            String taskId = requestJson.getStr("taskId");
            String recordId = requestJson.getStr("recordId");
            Integer timeout = requestJson.getInt("timeout", 300);
            
            if (StringUtils.isEmpty(command)) {
                result.put("status", "failed");
                result.put("errorMessage", "指令内容不能为空");
                return result;
            }
            
            // 安全检查
            if (!CommandExecutor.isCommandSafe(command)) {
                result.put("status", "failed");
                result.put("errorMessage", "检测到危险指令，执行被拒绝");
                logger.warn("拒绝执行危险指令: {}", command);
                return result;
            }
            
            // 检查是否已经在执行相同的任务
            if (StringUtils.isNotEmpty(recordId) && runningTasks.containsKey(recordId)) {
                result.put("status", "running");
                result.put("message", "指令正在执行中");
                return result;
            }
            
            logger.info("接收到指令执行请求，任务ID: {}, 记录ID: {}, 指令: {}", taskId, recordId, command);
            
            // 异步执行指令
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                executeCommandAsync(command, taskId, recordId, timeout);
            }, COMMAND_EXECUTOR);
            
            // 将任务加入缓存
            if (StringUtils.isNotEmpty(recordId)) {
                CommandExecution execution = new CommandExecution(recordId, taskId, command, future);
                runningTasks.put(recordId, execution);
            }
            
            result.put("status", "accepted");
            result.put("message", "指令已接收，开始执行");
            result.put("executionId", recordId);
            
        } catch (Exception e) {
            logger.error("处理指令执行请求失败: {}", e.getMessage(), e);
            result.put("status", "failed");
            result.put("errorMessage", "处理请求失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 查询指令执行状态
     */
    @ResponseBody
    @RequestMapping("/getCommandStatus")
    public JSONObject getCommandStatus(@RequestBody String paramBean) {
        JSONObject result = new JSONObject();
        
        try {
            JSONObject requestJson = JSONUtil.parseObj(paramBean);
            
            // 验证token
            if (!validateToken(requestJson)) {
                result.put("status", "failed");
                result.put("errorMessage", "Token验证失败");
                return result;
            }
            
            String recordId = requestJson.getStr("recordId");
            
            if (StringUtils.isEmpty(recordId)) {
                result.put("status", "failed");
                result.put("errorMessage", "记录ID不能为空");
                return result;
            }
            
            CommandExecution execution = runningTasks.get(recordId);
            if (execution == null) {
                result.put("status", "unknown");
                result.put("message", "未找到指令执行记录");
            } else if (execution.getFuture().isDone()) {
                result.put("status", "completed");
                result.put("message", "指令执行已完成");
                // 清理已完成的任务
                runningTasks.remove(recordId);
            } else {
                result.put("status", "running");
                result.put("message", "指令正在执行中");
            }
            
        } catch (Exception e) {
            logger.error("查询指令执行状态失败: {}", e.getMessage(), e);
            result.put("status", "failed");
            result.put("errorMessage", "查询状态失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 异步执行指令
     */
    private void executeCommandAsync(String command, String taskId, String recordId, int timeout) {
        try {
            logger.info("开始异步执行指令: {}", command);
            
            // 执行指令
            CommandExecutor.ExecutionResult executionResult = CommandExecutor.executeCommands(command, timeout);
            
            // 构造结果数据
            JSONObject resultData = new JSONObject();
            resultData.put("taskId", taskId);
            resultData.put("recordId", recordId);
            resultData.put("hostname", commonConfig.getBindIp());
            resultData.put("command", command);
            resultData.put("success", executionResult.isSuccess());
            resultData.put("exitCode", executionResult.getExitCode());
            resultData.put("output", executionResult.getOutput());
            resultData.put("errorOutput", executionResult.getErrorOutput());
            resultData.put("errorMessage", executionResult.getErrorMessage());
            resultData.put("executionTime", executionResult.getExecutionTime());
            resultData.put("completeTime", new Date());
            
            // 将结果回传给Server端
            String serverUrl = commonConfig.getServerUrl() + "/wgcloud/agent/commandResult";
            String response = restUtil.post(serverUrl, resultData);
            
            logger.info("指令执行完成并回传结果，任务ID: {}, 记录ID: {}, 成功: {}", 
                       taskId, recordId, executionResult.isSuccess());
            
        } catch (Exception e) {
            logger.error("异步执行指令失败: {}", e.getMessage(), e);
            
            // 即使执行失败也要回传错误信息
            try {
                JSONObject errorData = new JSONObject();
                errorData.put("taskId", taskId);
                errorData.put("recordId", recordId);
                errorData.put("hostname", commonConfig.getBindIp());
                errorData.put("command", command);
                errorData.put("success", false);
                errorData.put("exitCode", -1);
                errorData.put("output", "");
                errorData.put("errorOutput", "");
                errorData.put("errorMessage", "Agent端执行异常: " + e.getMessage());
                errorData.put("executionTime", 0L);
                errorData.put("completeTime", new Date());
                
                String serverUrl = commonConfig.getServerUrl() + "/wgcloud/agent/commandResult";
                restUtil.post(serverUrl, errorData);
                
            } catch (Exception ex) {
                logger.error("回传错误结果失败: {}", ex.getMessage());
            }
        } finally {
            // 清理缓存
            if (StringUtils.isNotEmpty(recordId)) {
                runningTasks.remove(recordId);
            }
        }
    }
    
    /**
     * 验证token
     */
    private boolean validateToken(JSONObject requestJson) {
        String receivedToken = requestJson.getStr("wgToken");
        String expectedToken = MD5Utils.GetMD5Code(commonConfig.getWgToken());
        return expectedToken.equals(receivedToken);
    }
    
    /**
     * 获取Agent状态信息
     */
    @ResponseBody
    @RequestMapping("/getAgentStatus")
    public JSONObject getAgentStatus(@RequestBody String paramBean) {
        JSONObject result = new JSONObject();
        
        try {
            JSONObject requestJson = JSONUtil.parseObj(paramBean);
            
            // 验证token
            if (!validateToken(requestJson)) {
                result.put("status", "failed");
                result.put("errorMessage", "Token验证失败");
                return result;
            }
            
            result.put("status", "success");
            result.put("hostname", commonConfig.getBindIp());
            result.put("osInfo", CommandExecutor.getOSInfo());
            result.put("runningTaskCount", runningTasks.size());
            result.put("agentVersion", "2.3");
            result.put("timestamp", new Date());
            
        } catch (Exception e) {
            logger.error("获取Agent状态失败: {}", e.getMessage(), e);
            result.put("status", "failed");
            result.put("errorMessage", "获取状态失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 指令执行记录
     */
    private static class CommandExecution {
        private String recordId;
        private String taskId;
        private String command;
        private CompletableFuture<Void> future;
        private Date startTime;
        
        public CommandExecution(String recordId, String taskId, String command, CompletableFuture<Void> future) {
            this.recordId = recordId;
            this.taskId = taskId;
            this.command = command;
            this.future = future;
            this.startTime = new Date();
        }
        
        // Getters
        public String getRecordId() {
            return recordId;
        }
        
        public String getTaskId() {
            return taskId;
        }
        
        public String getCommand() {
            return command;
        }
        
        public CompletableFuture<Void> getFuture() {
            return future;
        }
        
        public Date getStartTime() {
            return startTime;
        }
    }
} 