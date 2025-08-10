package com.wgcloud.controller;

import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.Command;
import com.wgcloud.entity.Tag;
import com.wgcloud.service.CommandService;
import com.wgcloud.service.TagService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/command")
public class CommandController {

    private static final Logger logger = LoggerFactory.getLogger(CommandController.class);

    @Resource
    private CommandService commandService;
    @Resource
    private TagService tagService;

    @RequestMapping(value = "list")
    @ResponseBody
    public cn.hutool.json.JSONObject list(Command command) {
        cn.hutool.json.JSONObject resultJson = new cn.hutool.json.JSONObject();
        Map<String, Object> params = new HashMap<>();
        try {
            if (!StringUtils.isEmpty(command.getCmdName())) {
                params.put("cmdName", command.getCmdName());
            }
            PageInfo<Command> pageInfo = commandService.selectByParams(params, command.getPage(), command.getPageSize());
            resultJson.put("page", pageInfo);
            resultJson.put("command", command);
        } catch (Exception e) {
            logger.error("查询指令列表错误", e);
            resultJson.put("error", e.getMessage());
        }
        return resultJson;
    }

    @RequestMapping(value = "edit")
    @ResponseBody
    public cn.hutool.json.JSONObject edit(HttpServletRequest request) {
        cn.hutool.json.JSONObject resultJson = new cn.hutool.json.JSONObject();
        String id = request.getParameter("id");
        Command command = new Command();
        try {
            if (!StringUtils.isEmpty(id)) {
                command = commandService.selectById(id);
            }
            List<Tag> allTags = tagService.selectAllByParams(new HashMap<>());
            resultJson.put("command", command);
            resultJson.put("allTags", allTags);
        } catch (Exception e) {
            logger.error("编辑指令错误", e);
            resultJson.put("error", e.getMessage());
        }
        return resultJson;
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public cn.hutool.json.JSONObject save(@RequestBody Command command) {
        cn.hutool.json.JSONObject resultJson = new cn.hutool.json.JSONObject();
        try {
            if (StringUtils.isEmpty(command.getId())) {
                commandService.save(command);
            } else {
                commandService.updateById(command);
            }
            resultJson.put("result", "success");
        } catch (Exception e) {
            logger.error("保存指令错误", e);
            resultJson.put("result", "error");
            resultJson.put("msg", e.getMessage());
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
                commandService.deleteById(id.split(","));
            }
            resultJson.put("result", "success");
        } catch (Exception e) {
            logger.error("删除指令错误", e);
            resultJson.put("result", "error");
            resultJson.put("msg", e.getMessage());
        }
        return resultJson;
    }
}
