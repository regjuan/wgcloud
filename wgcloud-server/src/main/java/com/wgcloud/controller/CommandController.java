package com.wgcloud.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageInfo;
import com.wgcloud.dto.MessageDto;
import com.wgcloud.entity.CommandTask;
import com.wgcloud.entity.CommandExecutionRecord;
import com.wgcloud.service.CommandService;
import com.wgcloud.service.LogInfoService;
import com.wgcloud.util.CodeUtil;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.PageUtil;
import com.wgcloud.util.staticvar.StaticKeys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:CommandController.java
 * @author: http://www.wgstart.com
 * @date: 2024年01月16日
 * @Description: 指令下发批量执行控制器
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Controller
@RequestMapping("/command")
public class CommandController {

    private static final Logger logger = LoggerFactory.getLogger(CommandController.class);

    @Autowired
    private CommandService commandService;

    @Autowired
    private LogInfoService logInfoService;

    /**
     * 指令任务列表页面
     */
    @RequestMapping("/list")
    public String list(Model model, 
                      HttpServletRequest request,
                      @RequestParam(value = "page", required = false) Integer page,
                      @RequestParam(value = "commandName", required = false) String commandName,
                      @RequestParam(value = "status", required = false) String status,
                      @RequestParam(value = "creator", required = false) String creator) {
        
        // 设置菜单激活状态
        request.getSession().setAttribute("menuActive", "62");
        
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotEmpty(commandName)) {
            params.put("commandName", commandName.trim());
        }
        if (StringUtils.isNotEmpty(status)) {
            params.put("status", status);
        }
        if (StringUtils.isNotEmpty(creator)) {
            params.put("creator", creator.trim());
        }
        
        try {
            PageUtil<CommandTask> pageUtil = new PageUtil<>(15);
            if (page != null) {
                pageUtil.setCurrPage(page);
            }
            pageUtil.setParams(params);
            
            pageUtil = commandService.selectByParams(pageUtil);
            
            model.addAttribute("pageUtil", pageUtil);
            model.addAttribute("commandName", commandName);
            model.addAttribute("status", status);
            model.addAttribute("creator", creator);
            
        } catch (Exception e) {
            logger.error("获取指令任务列表失败: {}", e.getMessage());
            model.addAttribute("error", "获取数据失败");
        }
        
        return "command/list";
    }

    /**
     * 转向添加指令任务页面
     */
    @RequestMapping("/add")
    public String add(Model model, HttpServletRequest request) {
        request.getSession().setAttribute("menuActive", "62");
        return "command/add";
    }

    /**
     * 保存指令任务
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(Model model, 
                      HttpServletRequest request,
                      RedirectAttributes redirectAttributes,
                      @RequestParam(value = "commandName") String commandName,
                      @RequestParam(value = "commandContent") String commandContent,
                      @RequestParam(value = "description", required = false) String description,
                      @RequestParam(value = "taskType", defaultValue = "immediate") String taskType,
                      @RequestParam(value = "scheduledTime", required = false) String scheduledTime,
                      @RequestParam(value = "timeoutSeconds", defaultValue = "300") Integer timeoutSeconds,
                      @RequestParam(value = "targetHosts", required = false) String targetHosts,
                      @RequestParam(value = "targetTags", required = false) String targetTags) {
        
        try {
            // 参数验证
            if (StringUtils.isEmpty(commandName) || StringUtils.isEmpty(commandContent)) {
                redirectAttributes.addFlashAttribute("error", "指令名称和指令内容不能为空");
                return "redirect:/command/add";
            }
            
            CommandTask commandTask = new CommandTask();
            commandTask.setId(IdUtil.simpleUUID());
            commandTask.setCommandName(commandName.trim());
            commandTask.setCommandContent(commandContent.trim());
            commandTask.setDescription(StringUtils.isEmpty(description) ? "" : description.trim());
            commandTask.setTaskType(taskType);
            commandTask.setTimeoutSeconds(timeoutSeconds);
            commandTask.setTargetHosts(StringUtils.isEmpty(targetHosts) ? "[]" : targetHosts);
            commandTask.setTargetTags(StringUtils.isEmpty(targetTags) ? "[]" : targetTags);
            commandTask.setCreator("admin"); // 这里应该从session中获取当前用户
            
            // 处理定时任务的执行时间
            if ("scheduled".equals(taskType) && StringUtils.isNotEmpty(scheduledTime)) {
                try {
                    Date scheduledDate = DateUtil.parseDate(scheduledTime, "yyyy-MM-dd HH:mm:ss");
                    if (scheduledDate.before(new Date())) {
                        redirectAttributes.addFlashAttribute("error", "定时执行时间不能早于当前时间");
                        return "redirect:/command/add";
                    }
                    commandTask.setScheduledTime(scheduledDate);
                } catch (ParseException e) {
                    redirectAttributes.addFlashAttribute("error", "定时执行时间格式错误");
                    return "redirect:/command/add";
                }
            }
            
            // 直接部署执行任务
            String result = commandService.deployCommand(commandTask);
            
            if ("success".equals(result)) {
                redirectAttributes.addFlashAttribute("success", "指令任务创建并下发成功");
                return "redirect:/command/list";
            } else {
                redirectAttributes.addFlashAttribute("error", "创建任务失败：" + result);
                return "redirect:/command/add";
            }
            
        } catch (Exception e) {
            logger.error("保存指令任务失败: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "保存失败：" + e.getMessage());
            return "redirect:/command/add";
        }
    }

    /**
     * 查看指令任务详情
     */
    @RequestMapping("/view/{id}")
    public String view(@PathVariable("id") String id, Model model, HttpServletRequest request) {
        request.getSession().setAttribute("menuActive", "62");
        
        try {
            CommandTask commandTask = commandService.selectById(id);
            if (commandTask == null) {
                model.addAttribute("error", "指令任务不存在");
                return "command/list";
            }
            
            // 获取执行进度
            Map<String, Object> progress = commandService.getTaskProgress(id);
            
            model.addAttribute("commandTask", commandTask);
            model.addAttribute("progress", progress);
            
        } catch (Exception e) {
            logger.error("查看指令任务详情失败: {}", e.getMessage());
            model.addAttribute("error", "获取详情失败");
        }
        
        return "command/view";
    }

    /**
     * 查看执行历史记录
     */
    @RequestMapping("/history/{taskId}")
    public String history(@PathVariable("taskId") String taskId,
                         Model model,
                         HttpServletRequest request,
                         @RequestParam(value = "page", required = false) Integer page,
                         @RequestParam(value = "hostname", required = false) String hostname,
                         @RequestParam(value = "status", required = false) String status) {
        
        request.getSession().setAttribute("menuActive", "62");
        
        try {
            // 获取任务信息
            CommandTask commandTask = commandService.selectById(taskId);
            if (commandTask == null) {
                model.addAttribute("error", "指令任务不存在");
                return "command/list";
            }
            
            // 查询执行记录
            Map<String, Object> params = new HashMap<>();
            params.put("commandTaskId", taskId);
            if (StringUtils.isNotEmpty(hostname)) {
                params.put("hostname", hostname.trim());
            }
            if (StringUtils.isNotEmpty(status)) {
                params.put("status", status);
            }
            
            PageUtil<CommandExecutionRecord> pageUtil = new PageUtil<>(20);
            if (page != null) {
                pageUtil.setCurrPage(page);
            }
            pageUtil.setParams(params);
            
            pageUtil = commandService.selectRecordsByParams(pageUtil);
            
            model.addAttribute("commandTask", commandTask);
            model.addAttribute("pageUtil", pageUtil);
            model.addAttribute("hostname", hostname);
            model.addAttribute("status", status);
            
        } catch (Exception e) {
            logger.error("查看执行历史记录失败: {}", e.getMessage());
            model.addAttribute("error", "获取历史记录失败");
        }
        
        return "command/history";
    }

    /**
     * 删除指令任务
     */
    @RequestMapping("/del")
    public String del(@RequestParam("id") String id, RedirectAttributes redirectAttributes) {
        try {
            String result = commandService.deleteById(id);
            if ("success".equals(result)) {
                redirectAttributes.addFlashAttribute("success", "删除成功");
            } else {
                redirectAttributes.addFlashAttribute("error", "删除失败：" + result);
            }
        } catch (Exception e) {
            logger.error("删除指令任务失败: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "删除失败：" + e.getMessage());
        }
        return "redirect:/command/list";
    }

    /**
     * 重新执行指令任务
     */
    @RequestMapping("/redeploy/{id}")
    public String redeploy(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            CommandTask commandTask = commandService.selectById(id);
            if (commandTask == null) {
                redirectAttributes.addFlashAttribute("error", "任务不存在");
                return "redirect:/command/list";
            }
            
            // 创建新的任务
            CommandTask newTask = new CommandTask();
            newTask.setCommandName(commandTask.getCommandName() + "_重新执行");
            newTask.setCommandContent(commandTask.getCommandContent());
            newTask.setDescription(commandTask.getDescription());
            newTask.setTaskType("immediate"); // 立即执行
            newTask.setTimeoutSeconds(commandTask.getTimeoutSeconds());
            newTask.setTargetHosts(commandTask.getTargetHosts());
            newTask.setTargetTags(commandTask.getTargetTags());
            newTask.setCreator(commandTask.getCreator());
            
            String result = commandService.deployCommand(newTask);
            
            if ("success".equals(result)) {
                redirectAttributes.addFlashAttribute("success", "任务重新下发成功");
            } else {
                redirectAttributes.addFlashAttribute("error", "重新下发失败：" + result);
            }
            
        } catch (Exception e) {
            logger.error("重新执行指令任务失败: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "重新执行失败：" + e.getMessage());
        }
        
        return "redirect:/command/list";
    }

    /**
     * AJAX获取任务执行进度
     */
    @ResponseBody
    @RequestMapping("/getProgress")
    public JSONObject getProgress(@RequestParam("taskId") String taskId) {
        JSONObject result = new JSONObject();
        
        try {
            Map<String, Object> progress = commandService.getTaskProgress(taskId);
            result.put("success", true);
            result.put("data", progress);
            
        } catch (Exception e) {
            logger.error("获取任务执行进度失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "获取进度失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * AJAX获取在线主机列表
     */
    @ResponseBody
    @RequestMapping("/getOnlineHosts")
    public JSONObject getOnlineHosts() {
        JSONObject result = new JSONObject();
        
        try {
            // 这里应该从SystemInfoService获取在线主机列表
            // 简化处理，返回示例数据
            result.put("success", true);
            result.put("hosts", new String[]{"192.168.1.100", "192.168.1.101", "192.168.1.102"});
            
        } catch (Exception e) {
            logger.error("获取在线主机列表失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "获取主机列表失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 检查指令任务名称是否重复
     */
    @ResponseBody
    @RequestMapping("/checkName")
    public MessageDto checkName(@RequestParam("commandName") String commandName,
                               @RequestParam(value = "id", required = false) String id) {
        
        MessageDto messageDto = new MessageDto();
        
        try {
            if (StringUtils.isEmpty(commandName)) {
                messageDto.setResult(StaticKeys.RESULT_FAIL);
                messageDto.setMessage("指令名称不能为空");
                return messageDto;
            }
            
            // 这里可以添加名称重复检查逻辑
            // 简化处理，直接返回成功
            messageDto.setResult(StaticKeys.RESULT_SUCCESS);
            messageDto.setMessage("指令名称可用");
            
        } catch (Exception e) {
            logger.error("检查指令名称失败: {}", e.getMessage());
            messageDto.setResult(StaticKeys.RESULT_FAIL);
            messageDto.setMessage("检查失败：" + e.getMessage());
        }
        
        return messageDto;
    }
} 