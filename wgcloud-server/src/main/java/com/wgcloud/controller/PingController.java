package com.wgcloud.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import com.wgcloud.entity.PingResult;
import com.wgcloud.entity.PingTask;
import com.wgcloud.service.PingService;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.PageUtil;
import com.wgcloud.util.PingMonitorManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Ping监控控制器 - Server端
 * @author WGCLOUD
 */
@Controller
@RequestMapping("/ping")
public class PingController {

    private static final Logger logger = LoggerFactory.getLogger(PingController.class);

    @Autowired
    private PingService pingService;

    @Autowired
    private PingMonitorManager pingMonitorManager;

    /**
     * PING任务列表页面
     */
    @RequestMapping("/list")
    public String list(Model model, 
                      HttpServletRequest request,
                      @RequestParam(value = "page", required = false) Integer page,
                      @RequestParam(value = "taskName", required = false) String taskName,
                      @RequestParam(value = "enabled", required = false) Boolean enabled) {
        
        // 设置菜单激活状态
        request.getSession().setAttribute("menuActive", "61");
        
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotEmpty(taskName)) {
            params.put("taskName", taskName.trim());
        }
        if (enabled != null) {
            params.put("enabled", enabled);
        }
        
        try {
            PageUtil<PingTask> pageUtil = new PageUtil<>(15);
            if (page != null) {
                pageUtil.setCurrPage(page);
            }
            pageUtil.setParams(params);
            
            pageUtil = pingService.selectByParams(pageUtil);
            
            // 获取任务运行状态
            Map<String, PingMonitorManager.PingTaskStatus> statusMap = pingMonitorManager.getRunningTasksStatus();
            
            model.addAttribute("pageUtil", pageUtil);
            model.addAttribute("statusMap", statusMap);
            model.addAttribute("taskName", taskName);
            model.addAttribute("enabled", enabled);
            
        } catch (Exception e) {
            logger.error("获取ping任务列表失败: {}", e.getMessage());
            model.addAttribute("error", "获取数据失败");
        }
        
        return "ping/list";
    }

    /**
     * 添加PING任务页面
     */
    @RequestMapping("/add")
    public String add(Model model, HttpServletRequest request) {
        request.getSession().setAttribute("menuActive", "61");
        model.addAttribute("task", new PingTask());
        return "ping/add";
    }

    /**
     * 编辑PING任务页面
     */
    @RequestMapping("/edit")
    public String edit(Model model, @RequestParam("id") String id, HttpServletRequest request) {
        request.getSession().setAttribute("menuActive", "61");
        try {
            PingTask task = pingService.selectById(id);
            model.addAttribute("task", task);
        } catch (Exception e) {
            logger.error("获取ping任务详情失败: {}", e.getMessage());
            model.addAttribute("error", "获取数据失败");
        }
        return "ping/edit";
    }

    /**
     * 查看PING任务详情页面
     */
    @RequestMapping("/view/{id}")
    public String view(@PathVariable String id, Model model, HttpServletRequest request) {
        request.getSession().setAttribute("menuActive", "61");
        try {
            PingTask task = pingService.selectById(id);
            model.addAttribute("task", task);
        } catch (Exception e) {
            logger.error("获取ping任务详情失败: {}", e.getMessage());
            model.addAttribute("error", "获取数据失败");
        }
        return "ping/view";
    }

    /**
     * 创建PING任务
     */
    @ResponseBody
    @PostMapping("/add")
    public JSONObject addTask(@RequestBody PingTask task) {
        JSONObject result = new JSONObject();
        try {
            // 验证必填字段
            if (StringUtils.isEmpty(task.getTaskName())) {
                result.put("success", false);
                result.put("message", "任务名称不能为空");
                return result;
            }
            if (StringUtils.isEmpty(task.getIpList())) {
                result.put("success", false);
                result.put("message", "IP列表不能为空");
                return result;
            }

            // 检查任务名称是否已存在
            PingTask existingTask = pingService.selectByTaskName(task.getTaskName());
            if (existingTask != null) {
                result.put("success", false);
                result.put("message", "任务名称已存在");
                return result;
            }

            task.setId(UUID.randomUUID().toString());
            task.setCreateTime(DateUtil.getNowTime());
            task.setUpdateTime(DateUtil.getNowTime());
            
            pingService.save(task);
            
            // 如果任务启用，立即启动监控
            if (task.getIsEnabled()) {
                pingMonitorManager.startPingTask(task);
            }
            
            result.put("success", true);
            result.put("message", "添加成功");
            
        } catch (Exception e) {
            logger.error("添加ping任务失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "添加失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新PING任务
     */
    @ResponseBody
    @PostMapping("/update")
    public JSONObject updateTask(@RequestBody PingTask task) {
        JSONObject result = new JSONObject();
        try {
            task.setUpdateTime(DateUtil.getNowTime());
            pingService.updateById(task);
            
            // 重启监控任务以应用新配置
            pingMonitorManager.restartPingTask(task);
            
            result.put("success", true);
            result.put("message", "更新成功");
            
        } catch (Exception e) {
            logger.error("更新ping任务失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "更新失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 启用/禁用PING任务
     */
    @ResponseBody
    @PostMapping("/toggle/{id}")
    public JSONObject toggleTask(@PathVariable String id) {
        JSONObject result = new JSONObject();
        try {
            PingTask task = pingService.selectById(id);
            if (task == null) {
                result.put("success", false);
                result.put("message", "任务不存在");
                return result;
            }

            boolean newEnabled = !task.getIsEnabled();
            task.setIsEnabled(newEnabled);
            task.setUpdateTime(DateUtil.getNowTime());
            pingService.updateById(task);

            if (newEnabled) {
                pingMonitorManager.startPingTask(task);
            } else {
                pingMonitorManager.stopPingTask(task.getId());
            }

            result.put("success", true);
            result.put("enabled", newEnabled);
            result.put("message", newEnabled ? "启用成功" : "禁用成功");
            
        } catch (Exception e) {
            logger.error("切换ping任务状态失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "操作失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 删除PING任务
     */
    @ResponseBody
    @PostMapping("/delete/{id}")
    public JSONObject deleteTask(@PathVariable String id) {
        JSONObject result = new JSONObject();
        try {
            // 先停止监控线程
            pingMonitorManager.stopPingTask(id);
            
            // 删除任务
            pingService.deleteById(id);
            
            result.put("success", true);
            result.put("message", "删除成功");
            
        } catch (Exception e) {
            logger.error("删除ping任务失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "删除失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * PING结果历史查询
     */
    @RequestMapping("/results/{taskId}")
    public String viewResults(@PathVariable String taskId, 
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "50") int size,
                            Model model) {
        try {
            PingTask task = pingService.selectById(taskId);
            PageUtil<PingResult> results = pingService.selectResultsByTaskId(taskId, page, size);
            
            // 获取任务统计信息
            Map<String, Object> statistics = pingService.getTaskStatistics(taskId);
            
            model.addAttribute("task", task);
            model.addAttribute("results", results);
            model.addAttribute("statistics", statistics);
            
        } catch (Exception e) {
            logger.error("查询ping结果失败: {}", e.getMessage());
            model.addAttribute("error", "获取数据失败");
        }
        
        return "ping/results";
    }

    /**
     * 立即执行PING测试
     */
    @ResponseBody
    @PostMapping("/test")
    public JSONObject testPing(@RequestParam String ipList, 
                              @RequestParam(defaultValue = "5000") int timeout) {
        JSONObject result = new JSONObject();
        Map<String, Object> testResults = new HashMap<>();
        
        try {
            String[] ips = ipList.split(",");
            for (String ip : ips) {
                String targetIp = ip.trim();
                if (targetIp.isEmpty()) continue;
                
                Map<String, Object> testResult = new HashMap<>();
                try {
                    InetAddress address = InetAddress.getByName(targetIp);
                    long startTime = System.currentTimeMillis();
                    boolean isReachable = address.isReachable(timeout);
                    long responseTime = System.currentTimeMillis() - startTime;
                    
                    testResult.put("ip", targetIp);
                    testResult.put("reachable", isReachable);
                    testResult.put("responseTime", responseTime);
                    
                } catch (Exception e) {
                    testResult.put("ip", targetIp);
                    testResult.put("reachable", false);
                    testResult.put("error", e.getMessage());
                }
                
                testResults.put(targetIp, testResult);
            }
            
            result.put("success", true);
            result.put("results", testResults);
            
        } catch (Exception e) {
            logger.error("执行ping测试失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "测试失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取任务运行状态
     */
    @ResponseBody
    @GetMapping("/status/{taskId}")
    public JSONObject getTaskStatus(@PathVariable String taskId) {
        JSONObject result = new JSONObject();
        try {
            PingMonitorManager.PingTaskStatus status = pingMonitorManager.getTaskStatus(taskId);
            if (status != null) {
                result.put("success", true);
                result.put("status", status);
            } else {
                result.put("success", false);
                result.put("message", "任务未运行");
            }
        } catch (Exception e) {
            logger.error("获取任务状态失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "获取状态失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取所有任务运行状态
     */
    @ResponseBody
    @GetMapping("/status")
    public JSONObject getAllTaskStatus() {
        JSONObject result = new JSONObject();
        try {
            Map<String, PingMonitorManager.PingTaskStatus> statusMap = pingMonitorManager.getRunningTasksStatus();
            Map<String, Object> managerStatus = pingMonitorManager.getManagerStatus();
            
            result.put("success", true);
            result.put("taskStatuses", statusMap);
            result.put("managerStatus", managerStatus);
            
        } catch (Exception e) {
            logger.error("获取所有任务状态失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "获取状态失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 重启任务
     */
    @ResponseBody
    @PostMapping("/restart/{taskId}")
    public JSONObject restartTask(@PathVariable String taskId) {
        JSONObject result = new JSONObject();
        try {
            pingMonitorManager.restartPingTask(taskId);
            result.put("success", true);
            result.put("message", "重启成功");
        } catch (Exception e) {
            logger.error("重启任务失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "重启失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 重置任务统计
     */
    @ResponseBody
    @PostMapping("/resetStats/{taskId}")
    public JSONObject resetTaskStats(@PathVariable String taskId) {
        JSONObject result = new JSONObject();
        try {
            pingMonitorManager.resetTaskStatistics(taskId);
            result.put("success", true);
            result.put("message", "重置成功");
        } catch (Exception e) {
            logger.error("重置统计失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "重置失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取任务统计信息
     */
    @ResponseBody
    @GetMapping("/statistics/{taskId}")
    public JSONObject getTaskStatistics(@PathVariable String taskId) {
        JSONObject result = new JSONObject();
        try {
            Map<String, Object> statistics = pingService.getTaskStatistics(taskId);
            result.put("success", true);
            result.put("statistics", statistics);
        } catch (Exception e) {
            logger.error("获取任务统计失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "获取统计失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取任务详情
     */
    @ResponseBody
    @GetMapping("/get/{id}")
    public JSONObject getTask(@PathVariable String id) {
        JSONObject result = new JSONObject();
        try {
            PingTask task = pingService.selectById(id);
            if (task != null) {
                result.put("success", true);
                result.put("data", task);
            } else {
                result.put("success", false);
                result.put("message", "任务不存在");
            }
        } catch (Exception e) {
            logger.error("获取任务详情失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "获取任务详情失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 创建任务
     */
    @ResponseBody
    @PostMapping("/create")
    public JSONObject createTask(@RequestBody PingTask task) {
        JSONObject result = new JSONObject();
        try {
            // 验证必填字段
            if (StringUtils.isEmpty(task.getTaskName())) {
                result.put("success", false);
                result.put("message", "任务名称不能为空");
                return result;
            }
            if (StringUtils.isEmpty(task.getIpList())) {
                result.put("success", false);
                result.put("message", "IP列表不能为空");
                return result;
            }

            // 检查任务名称是否已存在
            PingTask existingTask = pingService.selectByTaskName(task.getTaskName());
            if (existingTask != null) {
                result.put("success", false);
                result.put("message", "任务名称已存在");
                return result;
            }

            task.setId(UUID.randomUUID().toString());
            task.setCreateTime(DateUtil.getNowTime());
            task.setUpdateTime(DateUtil.getNowTime());
            
            pingService.save(task);
            
            // 如果任务启用，立即启动监控
            if (task.getIsEnabled()) {
                pingMonitorManager.startPingTask(task);
            }
            
            result.put("success", true);
            result.put("message", "任务创建成功");
            
        } catch (Exception e) {
            logger.error("创建任务失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "创建任务失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新任务
     */
    @ResponseBody
    @PostMapping("/update")
    public JSONObject updateTask(@RequestBody PingTask task) {
        JSONObject result = new JSONObject();
        try {
            // 验证必填字段
            if (StringUtils.isEmpty(task.getId())) {
                result.put("success", false);
                result.put("message", "任务ID不能为空");
                return result;
            }
            if (StringUtils.isEmpty(task.getTaskName())) {
                result.put("success", false);
                result.put("message", "任务名称不能为空");
                return result;
            }
            if (StringUtils.isEmpty(task.getIpList())) {
                result.put("success", false);
                result.put("message", "IP列表不能为空");
                return result;
            }

            // 检查任务是否存在
            PingTask existingTask = pingService.selectById(task.getId());
            if (existingTask == null) {
                result.put("success", false);
                result.put("message", "任务不存在");
                return result;
            }

            // 检查任务名称是否被其他任务使用
            PingTask taskWithSameName = pingService.selectByTaskName(task.getTaskName());
            if (taskWithSameName != null && !taskWithSameName.getId().equals(task.getId())) {
                result.put("success", false);
                result.put("message", "任务名称已被其他任务使用");
                return result;
            }

            task.setUpdateTime(DateUtil.getNowTime());
            pingService.updateById(task);
            
            // 重启监控线程以应用新配置
            if (task.getIsEnabled()) {
                pingMonitorManager.restartPingTask(task.getId());
            } else {
                pingMonitorManager.stopPingTask(task.getId());
            }
            
            result.put("success", true);
            result.put("message", "任务更新成功");
            
        } catch (Exception e) {
            logger.error("更新任务失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "更新任务失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 删除任务
     */
    @ResponseBody
    @DeleteMapping("/delete/{id}")
    public JSONObject deleteTask(@PathVariable String id) {
        JSONObject result = new JSONObject();
        try {
            // 先停止监控线程
            pingMonitorManager.stopPingTask(id);
            
            // 删除任务和相关结果
            pingService.deleteById(id);
            
            result.put("success", true);
            result.put("message", "任务删除成功");
            
        } catch (Exception e) {
            logger.error("删除任务失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "删除任务失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 切换任务状态
     */
    @ResponseBody
    @PostMapping("/toggleStatus")
    public JSONObject toggleTaskStatus(@RequestParam String id, @RequestParam Boolean isEnabled) {
        JSONObject result = new JSONObject();
        try {
            PingTask task = pingService.selectById(id);
            if (task == null) {
                result.put("success", false);
                result.put("message", "任务不存在");
                return result;
            }

            task.setIsEnabled(isEnabled);
            task.setUpdateTime(DateUtil.getNowTime());
            pingService.updateById(task);
            
            if (isEnabled) {
                pingMonitorManager.startPingTask(task);
            } else {
                pingMonitorManager.stopPingTask(id);
            }
            
            result.put("success", true);
            result.put("message", isEnabled ? "任务已启用" : "任务已禁用");
            
        } catch (Exception e) {
            logger.error("切换任务状态失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "操作失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 立即测试任务
     */
    @ResponseBody
    @PostMapping("/test/{id}")
    public JSONObject testTask(@PathVariable String id) {
        JSONObject result = new JSONObject();
        try {
            PingTask task = pingService.selectById(id);
            if (task == null) {
                result.put("success", false);
                result.put("message", "任务不存在");
                return result;
            }

            // 执行一次性ping测试
            pingService.performImmediatePing(task);
            
            result.put("success", true);
            result.put("message", "测试完成");
            
        } catch (Exception e) {
            logger.error("测试任务失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "测试失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 测试配置
     */
    @ResponseBody
    @PostMapping("/testConfig")
    public JSONObject testConfig(@RequestBody Map<String, Object> config) {
        JSONObject result = new JSONObject();
        try {
            String ipList = (String) config.get("ipList");
            Integer timeout = (Integer) config.get("timeout");
            
            if (StringUtils.isEmpty(ipList)) {
                result.put("success", false);
                result.put("message", "IP列表不能为空");
                return result;
            }

            List<PingResult> testResults = pingService.performConfigTest(ipList, timeout);
            
            result.put("success", true);
            result.put("data", testResults);
            
        } catch (Exception e) {
            logger.error("测试配置失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "测试失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取任务实时状态
     */
    @ResponseBody
    @GetMapping("/status/{id}")
    public JSONObject getTaskStatus(@PathVariable String id) {
        JSONObject result = new JSONObject();
        try {
            Map<String, Object> status = pingService.getTaskRealTimeStatus(id);
            result.put("success", true);
            result.put("data", status);
        } catch (Exception e) {
            logger.error("获取任务状态失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "获取状态失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取最后失败时间
     */
    @ResponseBody
    @GetMapping("/lastFailure/{id}")
    public JSONObject getLastFailureTime(@PathVariable String id) {
        JSONObject result = new JSONObject();
        try {
            String lastFailureTime = pingService.getLastFailureTime(id);
            result.put("success", true);
            result.put("data", lastFailureTime);
        } catch (Exception e) {
            logger.error("获取最后失败时间失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "获取失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取图表数据
     */
    @ResponseBody
    @GetMapping("/chartData/{id}")
    public JSONObject getChartData(@PathVariable String id, @RequestParam(defaultValue = "24") int hours) {
        JSONObject result = new JSONObject();
        try {
            List<Map<String, Object>> chartData = pingService.getChartData(id, hours);
            result.put("success", true);
            result.put("data", chartData);
        } catch (Exception e) {
            logger.error("获取图表数据失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "获取数据失败：" + e.getMessage());
        }
        return result;
    }
} 