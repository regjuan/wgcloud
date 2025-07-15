package com.wgcloud.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wgcloud.entity.CommandTask;
import com.wgcloud.entity.CommandExecutionRecord;
import com.wgcloud.entity.SystemInfo;
import com.wgcloud.mapper.CommandTaskMapper;
import com.wgcloud.mapper.CommandExecutionRecordMapper;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.PageUtil;
import com.wgcloud.util.staticvar.StaticKeys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 指令下发服务类 - Server端
 * @author WGCLOUD
 */
@Service
public class CommandService {

    private static final Logger logger = LoggerFactory.getLogger(CommandService.class);

    @Autowired
    private CommandTaskMapper commandTaskMapper;

    @Autowired
    private CommandExecutionRecordMapper commandExecutionRecordMapper;

    @Autowired
    private LogInfoService logInfoService;

    @Autowired
    private SystemInfoService systemInfoService;

    @Autowired
    private RestTemplate restTemplate;

    // ================ CommandTask相关方法 ================

    /**
     * 查询所有指令任务
     */
    public List<CommandTask> selectAll() {
        try {
            return commandTaskMapper.selectAll();
        } catch (Exception e) {
            logger.error("查询所有指令任务失败: {}", e.getMessage());
            logInfoService.save("selectAll", "查询所有指令任务失败：" + e.toString(), StaticKeys.LOG_ERROR);
            return new ArrayList<>();
        }
    }

    /**
     * 分页查询指令任务
     */
    public PageUtil<CommandTask> selectByParams(PageUtil<CommandTask> pageUtil) {
        try {
            Map<String, Object> params = pageUtil.getParams();
            
            // 计算总数
            int totalCount = commandTaskMapper.countByParams(params);
            pageUtil.setTotalCount(totalCount);
            
            // 分页查询
            if (totalCount > 0) {
                params.put("offset", pageUtil.getStartRow());
                params.put("limit", pageUtil.getPageSize());
                List<CommandTask> list = commandTaskMapper.selectByParams(params);
                pageUtil.setList(list);
            }
            
            return pageUtil;
        } catch (Exception e) {
            logger.error("分页查询指令任务失败: {}", e.getMessage());
            logInfoService.save("selectByParams", "分页查询指令任务失败：" + e.toString(), StaticKeys.LOG_ERROR);
            return pageUtil;
        }
    }

    /**
     * 根据ID查询指令任务
     */
    public CommandTask selectById(String id) {
        try {
            return commandTaskMapper.selectById(id);
        } catch (Exception e) {
            logger.error("根据ID查询指令任务失败: {}", e.getMessage());
            logInfoService.save("selectById", "根据ID查询指令任务失败：" + e.toString(), StaticKeys.LOG_ERROR);
            return null;
        }
    }

    /**
     * 保存指令任务
     */
    @Transactional
    public String save(CommandTask commandTask) {
        try {
            if (StringUtils.isEmpty(commandTask.getId())) {
                commandTask.setId(IdUtil.simpleUUID());
            }
            commandTask.setCreateTime(new Date());
            commandTask.setUpdateTime(new Date());
            
            commandTaskMapper.save(commandTask);
            logInfoService.save("save", "保存指令任务成功：" + commandTask.getCommandName(), StaticKeys.LOG_INFO);
            return "success";
        } catch (Exception e) {
            logger.error("保存指令任务失败: {}", e.getMessage());
            logInfoService.save("save", "保存指令任务失败：" + e.toString(), StaticKeys.LOG_ERROR);
            return "error: " + e.getMessage();
        }
    }

    /**
     * 更新指令任务
     */
    @Transactional
    public String updateById(CommandTask commandTask) {
        try {
            commandTask.setUpdateTime(new Date());
            commandTaskMapper.updateById(commandTask);
            logInfoService.save("updateById", "更新指令任务成功：" + commandTask.getCommandName(), StaticKeys.LOG_INFO);
            return "success";
        } catch (Exception e) {
            logger.error("更新指令任务失败: {}", e.getMessage());
            logInfoService.save("updateById", "更新指令任务失败：" + e.toString(), StaticKeys.LOG_ERROR);
            return "error: " + e.getMessage();
        }
    }

    /**
     * 删除指令任务
     */
    @Transactional
    public String deleteById(String id) {
        try {
            // 同时删除相关的执行记录
            commandExecutionRecordMapper.deleteByTaskId(id);
            commandTaskMapper.deleteById(id);
            logInfoService.save("deleteById", "删除指令任务成功，ID：" + id, StaticKeys.LOG_INFO);
            return "success";
        } catch (Exception e) {
            logger.error("删除指令任务失败: {}", e.getMessage());
            logInfoService.save("deleteById", "删除指令任务失败：" + e.toString(), StaticKeys.LOG_ERROR);
            return "error: " + e.getMessage();
        }
    }

    /**
     * 下发指令任务
     */
    @Transactional
    public String deployCommand(CommandTask commandTask) {
        try {
            // 1. 保存任务
            if (StringUtils.isEmpty(commandTask.getId())) {
                commandTask.setId(IdUtil.simpleUUID());
            }
            commandTask.setCreateTime(new Date());
            commandTask.setUpdateTime(new Date());
            commandTask.setDeployTime(new Date());
            commandTask.setStatus("deploying");
            
            // 2. 获取目标主机列表
            List<String> targetHosts = getTargetHosts(commandTask);
            commandTask.setTotalCount(targetHosts.size());
            commandTask.setExecutedCount(0);
            
            // 保存任务
            commandTaskMapper.save(commandTask);
            
            // 3. 创建执行记录
            List<CommandExecutionRecord> records = new ArrayList<>();
            for (String hostname : targetHosts) {
                CommandExecutionRecord record = new CommandExecutionRecord();
                record.setId(IdUtil.simpleUUID());
                record.setCommandTaskId(commandTask.getId());
                record.setHostname(hostname);
                record.setCommandContent(commandTask.getCommandContent());
                record.setStatus("pending");
                record.setExecuteTime(new Date());
                records.add(record);
            }
            
            if (!records.isEmpty()) {
                commandExecutionRecordMapper.insertBatch(records);
            }
            
            // 4. 根据任务类型执行
            if ("immediate".equals(commandTask.getTaskType())) {
                executeCommandImmediately(commandTask, records);
            }
            
            logInfoService.save("deployCommand", "下发指令任务成功：" + commandTask.getCommandName() + "，目标主机数：" + targetHosts.size(), StaticKeys.LOG_INFO);
            return "success";
        } catch (Exception e) {
            logger.error("下发指令任务失败: {}", e.getMessage());
            logInfoService.save("deployCommand", "下发指令任务失败：" + e.toString(), StaticKeys.LOG_ERROR);
            return "error: " + e.getMessage();
        }
    }

    /**
     * 立即执行指令任务
     */
    private void executeCommandImmediately(CommandTask task, List<CommandExecutionRecord> records) {
        // 异步执行指令
        new Thread(() -> {
            try {
                int successCount = 0;
                int failedCount = 0;
                
                for (CommandExecutionRecord record : records) {
                    try {
                        // 更新记录状态为执行中
                        record.setStatus("running");
                        record.setExecuteTime(new Date());
                        commandExecutionRecordMapper.updateById(record);
                        
                        // 发送指令到Agent
                        boolean success = sendCommandToAgent(record);
                        
                        if (success) {
                            successCount++;
                        } else {
                            failedCount++;
                        }
                        
                        // 短暂延迟避免并发过高
                        Thread.sleep(100);
                        
                    } catch (Exception e) {
                        logger.error("执行指令失败，主机：{}，错误：{}", record.getHostname(), e.getMessage());
                        
                        // 更新记录状态
                        commandExecutionRecordMapper.updateStatus(
                            record.getId(), "failed", "", e.getMessage(), -1, 0L
                        );
                        failedCount++;
                    }
                }
                
                // 更新任务统计
                String taskStatus = (failedCount == 0) ? "completed" : (successCount == 0) ? "failed" : "partial";
                String resultSummary = String.format("成功：%d，失败：%d，总计：%d", successCount, failedCount, records.size());
                
                commandTaskMapper.updateExecutionStats(task.getId(), successCount + failedCount, taskStatus, resultSummary);
                
            } catch (Exception e) {
                logger.error("执行指令任务异常: {}", e.getMessage());
                commandTaskMapper.updateExecutionStats(task.getId(), 0, "failed", "执行异常：" + e.getMessage());
            }
        }).start();
    }

    /**
     * 发送指令到Agent
     */
    private boolean sendCommandToAgent(CommandExecutionRecord record) {
        try {
            // 构造发送给Agent的请求
            JSONObject requestJson = new JSONObject();
            requestJson.put("command", record.getCommandContent());
            requestJson.put("taskId", record.getCommandTaskId());
            requestJson.put("recordId", record.getId());
            requestJson.put("timeout", 300); // 5分钟超时
            
            // 获取Agent的URL（这里需要根据hostname获取对应的Agent地址）
            String agentUrl = getAgentUrl(record.getHostname());
            if (StringUtils.isEmpty(agentUrl)) {
                logger.error("未找到主机{}的Agent地址", record.getHostname());
                commandExecutionRecordMapper.updateStatus(
                    record.getId(), "failed", "", "未找到Agent地址", -1, 0L
                );
                return false;
            }
            
            // 发送HTTP请求到Agent
            long startTime = System.currentTimeMillis();
            String response = restTemplate.postForObject(agentUrl + "/agent/executeCommand", requestJson.toString(), String.class);
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (StringUtils.isNotEmpty(response)) {
                JSONObject responseJson = JSONUtil.parseObj(response);
                String status = responseJson.getStr("status", "failed");
                String result = responseJson.getStr("result", "");
                String errorMessage = responseJson.getStr("errorMessage", "");
                Integer exitCode = responseJson.getInt("exitCode", -1);
                
                // 更新执行记录
                commandExecutionRecordMapper.updateStatus(
                    record.getId(), status, result, errorMessage, exitCode, executionTime
                );
                
                return "success".equals(status);
            } else {
                commandExecutionRecordMapper.updateStatus(
                    record.getId(), "failed", "", "Agent无响应", -1, executionTime
                );
                return false;
            }
            
        } catch (Exception e) {
            logger.error("发送指令到Agent失败: {}", e.getMessage());
            commandExecutionRecordMapper.updateStatus(
                record.getId(), "failed", "", "发送失败：" + e.getMessage(), -1, 0L
            );
            return false;
        }
    }

    /**
     * 获取目标主机列表
     */
    private List<String> getTargetHosts(CommandTask commandTask) {
        List<String> hosts = new ArrayList<>();
        
        try {
            // 如果指定了具体主机
            if (StringUtils.isNotEmpty(commandTask.getTargetHosts())) {
                JSONArray hostArray = JSONUtil.parseArray(commandTask.getTargetHosts());
                for (int i = 0; i < hostArray.size(); i++) {
                    hosts.add(hostArray.getStr(i));
                }
            }
            
            // 如果通过标签选择主机
            if (StringUtils.isNotEmpty(commandTask.getTargetTags()) && hosts.isEmpty()) {
                List<SystemInfo> systemInfos = systemInfoService.selectAll();
                JSONArray tagArray = JSONUtil.parseArray(commandTask.getTargetTags());
                Set<String> targetTags = new HashSet<>();
                for (int i = 0; i < tagArray.size(); i++) {
                    targetTags.add(tagArray.getStr(i));
                }
                
                // 根据标签筛选主机（这里简化处理，实际需要根据业务逻辑）
                for (SystemInfo systemInfo : systemInfos) {
                    if (matchesTags(systemInfo, targetTags)) {
                        hosts.add(systemInfo.getHostname());
                    }
                }
            }
            
            // 如果都没有指定，返回所有在线主机
            if (hosts.isEmpty()) {
                List<SystemInfo> systemInfos = systemInfoService.selectAll();
                hosts = systemInfos.stream()
                    .filter(info -> isHostOnline(info))
                    .map(SystemInfo::getHostname)
                    .collect(Collectors.toList());
            }
            
        } catch (Exception e) {
            logger.error("获取目标主机列表失败: {}", e.getMessage());
        }
        
        return hosts;
    }

    /**
     * 检查主机是否匹配标签
     */
    private boolean matchesTags(SystemInfo systemInfo, Set<String> targetTags) {
        // 这里简化处理，实际需要根据业务逻辑实现标签匹配
        // 可以根据主机的操作系统、环境等信息匹配
        return true;
    }

    /**
     * 检查主机是否在线
     */
    private boolean isHostOnline(SystemInfo systemInfo) {
        // 检查最近更新时间，判断主机是否在线
        if (systemInfo.getCreateTime() != null) {
            long diffMinutes = (System.currentTimeMillis() - systemInfo.getCreateTime().getTime()) / (1000 * 60);
            return diffMinutes <= 10; // 10分钟内有数据更新认为在线
        }
        return false;
    }

    /**
     * 获取Agent的URL
     */
    private String getAgentUrl(String hostname) {
        // 这里需要根据实际部署情况返回Agent的URL
        // 可以从配置文件或数据库中获取
        return "http://" + hostname + ":10001";
    }

    // ================ CommandExecutionRecord相关方法 ================

    /**
     * 分页查询执行记录
     */
    public PageUtil<CommandExecutionRecord> selectRecordsByParams(PageUtil<CommandExecutionRecord> pageUtil) {
        try {
            Map<String, Object> params = pageUtil.getParams();
            
            // 计算总数
            int totalCount = commandExecutionRecordMapper.countByParams(params);
            pageUtil.setTotalCount(totalCount);
            
            // 分页查询
            if (totalCount > 0) {
                params.put("offset", pageUtil.getStartRow());
                params.put("limit", pageUtil.getPageSize());
                List<CommandExecutionRecord> list = commandExecutionRecordMapper.selectByParams(params);
                pageUtil.setList(list);
            }
            
            return pageUtil;
        } catch (Exception e) {
            logger.error("分页查询执行记录失败: {}", e.getMessage());
            logInfoService.save("selectRecordsByParams", "分页查询执行记录失败：" + e.toString(), StaticKeys.LOG_ERROR);
            return pageUtil;
        }
    }

    /**
     * 根据任务ID查询执行记录
     */
    public List<CommandExecutionRecord> selectRecordsByTaskId(String taskId) {
        try {
            return commandExecutionRecordMapper.selectByTaskId(taskId);
        } catch (Exception e) {
            logger.error("根据任务ID查询执行记录失败: {}", e.getMessage());
            logInfoService.save("selectRecordsByTaskId", "根据任务ID查询执行记录失败：" + e.toString(), StaticKeys.LOG_ERROR);
            return new ArrayList<>();
        }
    }

    /**
     * 查询任务执行进度
     */
    public Map<String, Object> getTaskProgress(String taskId) {
        try {
            return commandExecutionRecordMapper.selectTaskProgress(taskId);
        } catch (Exception e) {
            logger.error("查询任务执行进度失败: {}", e.getMessage());
            logInfoService.save("getTaskProgress", "查询任务执行进度失败：" + e.toString(), StaticKeys.LOG_ERROR);
            return new HashMap<>();
        }
    }

    /**
     * 处理定时任务
     */
    public void processScheduledTasks() {
        try {
            List<CommandTask> scheduledTasks = commandTaskMapper.selectScheduledTasks();
            for (CommandTask task : scheduledTasks) {
                logger.info("开始执行定时任务：{}", task.getCommandName());
                deployCommand(task);
            }
        } catch (Exception e) {
            logger.error("处理定时任务失败: {}", e.getMessage());
            logInfoService.save("processScheduledTasks", "处理定时任务失败：" + e.toString(), StaticKeys.LOG_ERROR);
        }
    }
} 