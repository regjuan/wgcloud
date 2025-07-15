package com.wgcloud.controller;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageInfo;
import com.wgcloud.dto.MessageDto;
import com.wgcloud.entity.DockerContainer;
import com.wgcloud.entity.DockerState;
import com.wgcloud.service.DockerService;
import com.wgcloud.service.LogInfoService;
import com.wgcloud.util.CodeUtil;
import com.wgcloud.util.PageUtil;
import com.wgcloud.util.staticvar.StaticKeys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:DockerController.java
 * @author: http://www.wgstart.com
 * @date: 2024年01月16日
 * @Description: Docker容器监控控制器
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Controller
@RequestMapping("/docker")
public class DockerController {

    private static final Logger logger = LoggerFactory.getLogger(DockerController.class);

    @Resource
    private DockerService dockerService;
    @Resource
    private LogInfoService logInfoService;

    /**
     * 根据条件查询Docker容器列表
     *
     * @param dockerContainer
     * @param model
     * @return
     */
    @RequestMapping(value = "list")
    public String dockerList(DockerContainer dockerContainer, Model model) {
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            StringBuffer url = new StringBuffer();
            String hostname = null;
            if (!StringUtils.isEmpty(dockerContainer.getHostname())) {
                hostname = CodeUtil.unescape(dockerContainer.getHostname());
                params.put("hostname", hostname.trim());
                url.append("&hostname=").append(CodeUtil.escape(hostname));
            }
            
            PageInfo<DockerContainer> pageInfo = dockerService.selectByParams(params, dockerContainer.getPage(), dockerContainer.getPageSize());
            PageUtil.initPageNumber(pageInfo, model);
            
            model.addAttribute("pageUrl", "/docker/list?1=1" + url.toString());
            model.addAttribute("page", pageInfo);
            model.addAttribute("dockerContainer", dockerContainer);
        } catch (Exception e) {
            logger.error("查询Docker容器信息错误", e);
            logInfoService.save("查询Docker容器信息错误", e.toString(), StaticKeys.LOG_ERROR);
        }
        return "docker/list";
    }

    /**
     * 查看Docker容器详情
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "view")
    public String viewDockerContainer(Model model, HttpServletRequest request) {
        String id = request.getParameter("id");
        String containerId = request.getParameter("containerId");
        String hostname = request.getParameter("hostname");
        
        try {
            DockerContainer dockerContainer = null;
            if (!StringUtils.isEmpty(id)) {
                dockerContainer = dockerService.selectById(id);
            }
            
            // 获取容器状态历史数据
            Map<String, Object> params = new HashMap<String, Object>();
            if (!StringUtils.isEmpty(containerId)) {
                params.put("containerId", containerId);
            }
            if (!StringUtils.isEmpty(hostname)) {
                params.put("hostname", hostname);
            }
            
            List<DockerState> stateList = dockerService.getContainerStates(params);
            
            model.addAttribute("dockerContainer", dockerContainer);
            model.addAttribute("stateList", stateList);
        } catch (Exception e) {
            logger.error("查看Docker容器详情错误", e);
            logInfoService.save("查看Docker容器详情错误", e.toString(), StaticKeys.LOG_ERROR);
        }
        return "docker/view";
    }

    /**
     * 编辑Docker容器配置
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "edit")
    public String editDockerContainer(Model model, HttpServletRequest request) {
        String id = request.getParameter("id");
        DockerContainer dockerContainer = new DockerContainer();
        
        try {
            if (!StringUtils.isEmpty(id)) {
                dockerContainer = dockerService.selectById(id);
            }
            model.addAttribute("dockerContainer", dockerContainer);
        } catch (Exception e) {
            logger.error("编辑Docker容器配置错误", e);
            logInfoService.save("编辑Docker容器配置错误", e.toString(), StaticKeys.LOG_ERROR);
        }
        return "docker/edit";
    }

    /**
     * 保存Docker容器配置
     *
     * @param dockerContainer
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "save")
    public String saveDockerContainer(DockerContainer dockerContainer, Model model, HttpServletRequest request) {
        try {
            if (StringUtils.isEmpty(dockerContainer.getId())) {
                dockerService.save(dockerContainer);
            } else {
                dockerService.updateById(dockerContainer);
            }
        } catch (Exception e) {
            logger.error("保存Docker容器配置错误", e);
            logInfoService.save("保存Docker容器配置错误", e.toString(), StaticKeys.LOG_ERROR);
        }
        return "redirect:/docker/list";
    }

    /**
     * 删除Docker容器记录
     *
     * @param model
     * @param request
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "del")
    public String deleteDockerContainer(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String errorMsg = "删除Docker容器记录错误：";
        try {
            String id = request.getParameter("id");
            if (!StringUtils.isEmpty(id)) {
                DockerContainer dockerContainer = dockerService.selectById(id);
                logInfoService.save("删除Docker容器记录：" + dockerContainer.getContainerName(), 
                    "删除Docker容器记录：" + dockerContainer.getContainerName() + "，主机：" + dockerContainer.getHostname(), 
                    StaticKeys.LOG_ERROR);
                dockerService.deleteById(id.split(","));
            }
        } catch (Exception e) {
            logger.error(errorMsg, e);
            logInfoService.save(errorMsg, e.toString(), StaticKeys.LOG_ERROR);
        }
        return "redirect:/docker/list?msg=del";
    }

    /**
     * 启动Docker容器
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "start")
    public String startContainer(HttpServletRequest request) {
        MessageDto messageDto = new MessageDto();
        String containerId = request.getParameter("containerId");
        String hostname = request.getParameter("hostname");
        
        try {
            if (StringUtils.isEmpty(containerId) || StringUtils.isEmpty(hostname)) {
                messageDto.setCode("1");
                messageDto.setMsg("参数错误");
            } else {
                boolean result = dockerService.startContainer(hostname, containerId);
                if (result) {
                    messageDto.setCode("0");
                    messageDto.setMsg("容器启动成功");
                    logInfoService.save("启动Docker容器", "启动Docker容器：" + containerId + "，主机：" + hostname, StaticKeys.LOG_INFO);
                } else {
                    messageDto.setCode("1");
                    messageDto.setMsg("容器启动失败");
                }
            }
        } catch (Exception e) {
            logger.error("启动Docker容器错误", e);
            logInfoService.save("启动Docker容器错误", e.toString(), StaticKeys.LOG_ERROR);
            messageDto.setCode("1");
            messageDto.setMsg("容器启动失败：" + e.getMessage());
        }
        return JSONUtil.toJsonStr(messageDto);
    }

    /**
     * 停止Docker容器
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "stop")
    public String stopContainer(HttpServletRequest request) {
        MessageDto messageDto = new MessageDto();
        String containerId = request.getParameter("containerId");
        String hostname = request.getParameter("hostname");
        
        try {
            if (StringUtils.isEmpty(containerId) || StringUtils.isEmpty(hostname)) {
                messageDto.setCode("1");
                messageDto.setMsg("参数错误");
            } else {
                boolean result = dockerService.stopContainer(hostname, containerId);
                if (result) {
                    messageDto.setCode("0");
                    messageDto.setMsg("容器停止成功");
                    logInfoService.save("停止Docker容器", "停止Docker容器：" + containerId + "，主机：" + hostname, StaticKeys.LOG_INFO);
                } else {
                    messageDto.setCode("1");
                    messageDto.setMsg("容器停止失败");
                }
            }
        } catch (Exception e) {
            logger.error("停止Docker容器错误", e);
            logInfoService.save("停止Docker容器错误", e.toString(), StaticKeys.LOG_ERROR);
            messageDto.setCode("1");
            messageDto.setMsg("容器停止失败：" + e.getMessage());
        }
        return JSONUtil.toJsonStr(messageDto);
    }

    /**
     * 重启Docker容器
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "restart")
    public String restartContainer(HttpServletRequest request) {
        MessageDto messageDto = new MessageDto();
        String containerId = request.getParameter("containerId");
        String hostname = request.getParameter("hostname");
        
        try {
            if (StringUtils.isEmpty(containerId) || StringUtils.isEmpty(hostname)) {
                messageDto.setCode("1");
                messageDto.setMsg("参数错误");
            } else {
                boolean result = dockerService.restartContainer(hostname, containerId);
                if (result) {
                    messageDto.setCode("0");
                    messageDto.setMsg("容器重启成功");
                    logInfoService.save("重启Docker容器", "重启Docker容器：" + containerId + "，主机：" + hostname, StaticKeys.LOG_INFO);
                } else {
                    messageDto.setCode("1");
                    messageDto.setMsg("容器重启失败");
                }
            }
        } catch (Exception e) {
            logger.error("重启Docker容器错误", e);
            logInfoService.save("重启Docker容器错误", e.toString(), StaticKeys.LOG_ERROR);
            messageDto.setCode("1");
            messageDto.setMsg("容器重启失败：" + e.getMessage());
        }
        return JSONUtil.toJsonStr(messageDto);
    }

    /**
     * 获取容器日志
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "logs")
    public String getContainerLogs(HttpServletRequest request) {
        String containerId = request.getParameter("containerId");
        String hostname = request.getParameter("hostname");
        String tailLines = request.getParameter("tailLines");
        
        try {
            if (StringUtils.isEmpty(containerId) || StringUtils.isEmpty(hostname)) {
                return "参数错误";
            }
            
            int lines = 100;
            if (!StringUtils.isEmpty(tailLines)) {
                lines = Integer.parseInt(tailLines);
            }
            
            String logs = dockerService.getContainerLogs(hostname, containerId, lines);
            return logs;
        } catch (Exception e) {
            logger.error("获取容器日志错误", e);
            logInfoService.save("获取容器日志错误", e.toString(), StaticKeys.LOG_ERROR);
            return "获取容器日志失败：" + e.getMessage();
        }
    }

    /**
     * 获取容器状态图表数据
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "chartData")
    public String getChartData(HttpServletRequest request) {
        String containerId = request.getParameter("containerId");
        String hostname = request.getParameter("hostname");
        
        try {
            if (StringUtils.isEmpty(containerId) || StringUtils.isEmpty(hostname)) {
                return "[]";
            }
            
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("containerId", containerId);
            params.put("hostname", hostname);
            
            List<DockerState> stateList = dockerService.getContainerStates(params);
            return JSONUtil.toJsonStr(stateList);
        } catch (Exception e) {
            logger.error("获取容器状态图表数据错误", e);
            logInfoService.save("获取容器状态图表数据错误", e.toString(), StaticKeys.LOG_ERROR);
            return "[]";
        }
    }
} 