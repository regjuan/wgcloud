package com.wgcloud.controller;

import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.CommandResult;
import com.wgcloud.service.CommandResultService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/commandResult")
public class CommandResultController {

    private static final Logger logger = LoggerFactory.getLogger(CommandResultController.class);

    @Resource
    private CommandResultService commandResultService;

    @RequestMapping(value = "list")
    @ResponseBody
    public cn.hutool.json.JSONObject list(CommandResult commandResult) {
        cn.hutool.json.JSONObject resultJson = new cn.hutool.json.JSONObject();
        Map<String, Object> params = new HashMap<>();
        try {
            if (!StringUtils.isEmpty(commandResult.getHostname())) {
                params.put("hostname", commandResult.getHostname());
            }
            if (!StringUtils.isEmpty(commandResult.getStatus())) {
                params.put("status", commandResult.getStatus());
            }
            PageInfo<CommandResult> pageInfo = commandResultService.selectByParams(params, commandResult.getPage(), commandResult.getPageSize());
            resultJson.put("page", pageInfo);
            resultJson.put("commandResult", commandResult);
        } catch (Exception e) {
            logger.error("查询指令执行结果列表错误", e);
            resultJson.put("error", e.getMessage());
        }
        return resultJson;
    }

    @RequestMapping(value = "del")
    @ResponseBody
    public cn.hutool.json.JSONObject delete(HttpServletRequest request) {
        cn.hutool.json.JSONObject resultJson = new cn.hutool.json.JSONObject();
        try {
            String id = request.getParameter("id");
            if (!StringUtils.isEmpty(id)) {
                commandResultService.deleteById(id.split(","));
            }
            resultJson.put("result", "success");
        } catch (Exception e) {
            logger.error("删除指令执行结果错误", e);
            resultJson.put("result", "error");
            resultJson.put("msg", e.getMessage());
        }
        return resultJson;
    }
}
