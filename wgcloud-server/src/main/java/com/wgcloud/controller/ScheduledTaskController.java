package com.wgcloud.controller;

import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.ScheduledTask;
import com.wgcloud.entity.ScheduledTaskRecord;
import com.wgcloud.service.ScheduledTaskService;
import com.wgcloud.service.ScheduledTaskRecordService;
import com.wgcloud.util.PageUtil;
import com.wgcloud.util.staticvar.StaticKeys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:ScheduledTaskController.java
 * @author: http://www.wgstart.com
 * @date: 2024年01月16日
 * @Description: 计划任务控制器
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Controller
@RequestMapping("/scheduledTask")
public class ScheduledTaskController {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskController.class);

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @Autowired
    private ScheduledTaskRecordService scheduledTaskRecordService;

    /**
     * 计划任务列表页面
     */
    @RequestMapping(value = "list")
    public String list(Model model, @RequestParam(value = "page", required = false) Integer page,
                       @RequestParam(value = "taskName", required = false) String taskName,
                       @RequestParam(value = "hostname", required = false) String hostname,
                       @RequestParam(value = "status", required = false) String status,
                       @RequestParam(value = "enabled", required = false) Boolean enabled) {
        try {
            Map<String, Object> params = new HashMap<>();
            if (StringUtils.isNotEmpty(taskName)) {
                params.put("taskName", taskName.trim());
            }
            if (StringUtils.isNotEmpty(hostname)) {
                params.put("hostname", hostname.trim());
            }
            if (StringUtils.isNotEmpty(status)) {
                params.put("status", status);
            }
            if (enabled != null) {
                params.put("enabled", enabled);
            }

            PageInfo<ScheduledTask> pageInfo = scheduledTaskService.selectByParams(params, 
                page == null ? 1 : page, StaticKeys.PAGE_SIZE);
            
            // 获取任务统计信息
            Map<String, Object> statistics = scheduledTaskService.getTaskStatistics();
            
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("statistics", statistics);
            model.addAttribute("taskName", taskName);
            model.addAttribute("hostname", hostname);
            model.addAttribute("status", status);
            model.addAttribute("enabled", enabled);
            
        } catch (Exception e) {
            logger.error("查询计划任务列表异常", e);
            model.addAttribute("error", "查询计划任务列表失败：" + e.getMessage());
        }
        return "scheduledTask/list";
    }

    /**
     * 计划任务添加页面
     */
    @RequestMapping(value = "add")
    public String add(Model model) {
        return "scheduledTask/add";
    }

    /**
     * 计划任务编辑页面
     */
    @RequestMapping(value = "edit")
    public String edit(Model model, @RequestParam("id") String id) {
        try {
            ScheduledTask scheduledTask = scheduledTaskService.selectById(id);
            model.addAttribute("scheduledTask", scheduledTask);
        } catch (Exception e) {
            logger.error("查询计划任务信息异常", e);
            model.addAttribute("error", "查询计划任务信息失败：" + e.getMessage());
        }
        return "scheduledTask/edit";
    }

    /**
     * 计划任务详情页面
     */
    @RequestMapping(value = "view")
    public String view(Model model, @RequestParam("id") String id) {
        try {
            ScheduledTask scheduledTask = scheduledTaskService.selectById(id);
            
            // 查询最近的执行记录
            List<ScheduledTaskRecord> recentRecords = scheduledTaskRecordService.selectByTaskId(id);
            if (recentRecords.size() > 10) {
                recentRecords = recentRecords.subList(0, 10);
            }
            
            // 获取执行统计信息
            Map<String, Object> statistics = scheduledTaskRecordService.getExecutionStatistics(id);
            
            model.addAttribute("scheduledTask", scheduledTask);
            model.addAttribute("recentRecords", recentRecords);
            model.addAttribute("statistics", statistics);
            
        } catch (Exception e) {
            logger.error("查询计划任务详情异常", e);
            model.addAttribute("error", "查询计划任务详情失败：" + e.getMessage());
        }
        return "scheduledTask/view";
    }

    /**
     * 保存计划任务
     */
    @ResponseBody
    @PostMapping(value = "save")
    public String save(@RequestBody ScheduledTask scheduledTask) {
        try {
            // 校验Cron表达式
            if (StringUtils.isNotEmpty(scheduledTask.getCronExpression())) {
                if (!scheduledTaskService.validateCronExpression(scheduledTask.getCronExpression())) {
                    return "error: Cron表达式格式不正确";
                }
            }
            
            if (StringUtils.isEmpty(scheduledTask.getId())) {
                scheduledTaskService.save(scheduledTask);
                return "success: 添加计划任务成功";
            } else {
                scheduledTaskService.updateById(scheduledTask);
                return "success: 更新计划任务成功";
            }
        } catch (Exception e) {
            logger.error("保存计划任务失败", e);
            return "error: " + e.getMessage();
        }
    }

    /**
     * 删除计划任务
     */
    @ResponseBody
    @PostMapping(value = "del")
    public String delete(@RequestParam("id") String ids) {
        try {
            String[] idArray = ids.split(",");
            int result = scheduledTaskService.deleteById(idArray);
            return "success: 删除成功，共删除" + result + "条记录";
        } catch (Exception e) {
            logger.error("删除计划任务失败", e);
            return "error: " + e.getMessage();
        }
    }

    /**
     * 启用/禁用计划任务
     */
    @ResponseBody
    @PostMapping(value = "updateEnabled")
    public String updateEnabled(@RequestParam("id") String id, @RequestParam("enabled") Boolean enabled) {
        try {
            scheduledTaskService.updateEnabled(id, enabled);
            String action = enabled ? "启用" : "禁用";
            return "success: " + action + "任务成功";
        } catch (Exception e) {
            logger.error("更新计划任务状态失败", e);
            return "error: " + e.getMessage();
        }
    }

    /**
     * 立即执行计划任务
     */
    @ResponseBody
    @PostMapping(value = "executeImmediately")
    public String executeImmediately(@RequestParam("id") String id, @RequestParam("triggerBy") String triggerBy) {
        try {
            ScheduledTaskRecord record = scheduledTaskService.executeImmediately(id, triggerBy);
            return "success: 任务已提交执行，执行记录ID：" + record.getId();
        } catch (Exception e) {
            logger.error("立即执行计划任务失败", e);
            return "error: " + e.getMessage();
        }
    }

    /**
     * 校验Cron表达式
     */
    @ResponseBody
    @PostMapping(value = "validateCron")
    public String validateCron(@RequestParam("cronExpression") String cronExpression) {
        try {
            boolean isValid = scheduledTaskService.validateCronExpression(cronExpression);
            if (isValid) {
                return "success: Cron表达式格式正确";
            } else {
                return "error: Cron表达式格式不正确";
            }
        } catch (Exception e) {
            logger.error("校验Cron表达式失败", e);
            return "error: " + e.getMessage();
        }
    }

    /**
     * 获取任务统计信息
     */
    @ResponseBody
    @GetMapping(value = "statistics")
    public Map<String, Object> getStatistics() {
        try {
            return scheduledTaskService.getTaskStatistics();
        } catch (Exception e) {
            logger.error("获取任务统计信息失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("error", e.getMessage());
            return result;
        }
    }

    /**
     * 查询任务执行记录
     */
    @RequestMapping(value = "records")
    public String records(Model model, @RequestParam("taskId") String taskId,
                          @RequestParam(value = "page", required = false) Integer page) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("taskId", taskId);
            
            PageInfo<ScheduledTaskRecord> pageInfo = scheduledTaskRecordService.selectByParams(params,
                page == null ? 1 : page, StaticKeys.PAGE_SIZE);
            
            // 获取任务信息
            ScheduledTask task = scheduledTaskService.selectById(taskId);
            
            // 获取执行统计信息
            Map<String, Object> statistics = scheduledTaskRecordService.getExecutionStatistics(taskId);
            
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("task", task);
            model.addAttribute("statistics", statistics);
            model.addAttribute("taskId", taskId);
            
        } catch (Exception e) {
            logger.error("查询任务执行记录异常", e);
            model.addAttribute("error", "查询任务执行记录失败：" + e.getMessage());
        }
        return "scheduledTask/records";
    }

    /**
     * 执行记录详情
     */
    @RequestMapping(value = "recordDetail")
    public String recordDetail(Model model, @RequestParam("id") String id) {
        try {
            ScheduledTaskRecord record = scheduledTaskRecordService.selectById(id);
            model.addAttribute("record", record);
        } catch (Exception e) {
            logger.error("查询执行记录详情异常", e);
            model.addAttribute("error", "查询执行记录详情失败：" + e.getMessage());
        }
        return "scheduledTask/recordDetail";
    }

    /**
     * 获取任务执行图表数据
     */
    @ResponseBody
    @GetMapping(value = "chartData")
    public List<Map<String, Object>> getChartData(@RequestParam("taskId") String taskId, 
                                                   @RequestParam(value = "days", defaultValue = "7") Integer days) {
        try {
            return scheduledTaskRecordService.getChartData(taskId, days);
        } catch (Exception e) {
            logger.error("获取任务执行图表数据失败", e);
            return null;
        }
    }

    /**
     * 复制计划任务
     */
    @ResponseBody
    @PostMapping(value = "copy")
    public String copy(@RequestParam("id") String id) {
        try {
            ScheduledTask originalTask = scheduledTaskService.selectById(id);
            if (originalTask == null) {
                return "error: 原始任务不存在";
            }
            
            // 创建副本
            ScheduledTask copyTask = new ScheduledTask();
            copyTask.setHostname(originalTask.getHostname());
            copyTask.setTaskName(originalTask.getTaskName() + "_副本");
            copyTask.setCronExpression(originalTask.getCronExpression());
            copyTask.setCommand(originalTask.getCommand());
            copyTask.setEnabled(false); // 副本默认禁用
            copyTask.setTags(originalTask.getTags());
            copyTask.setDescription(originalTask.getDescription());
            copyTask.setTimeoutSeconds(originalTask.getTimeoutSeconds());
            copyTask.setMaxRetryCount(originalTask.getMaxRetryCount());
            copyTask.setCreator(originalTask.getCreator());
            
            scheduledTaskService.save(copyTask);
            return "success: 复制任务成功";
        } catch (Exception e) {
            logger.error("复制计划任务失败", e);
            return "error: " + e.getMessage();
        }
    }
} 