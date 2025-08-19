package com.wgcloud.controller;

import com.github.pagehelper.PageInfo;
import com.wgcloud.common.AjaxResult;
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
    public AjaxResult list(@RequestBody Command command) {
        Map<String, Object> params = new HashMap<>();
        try {
            if (!StringUtils.isEmpty(command.getCmdName())) {
                params.put("cmdName", command.getCmdName());
            }
            PageInfo<Command> pageInfo = commandService.selectByParams(params, command.getPage(), command.getPageSize());
            return AjaxResult.success(pageInfo);
        } catch (Exception e) {
            logger.error("查询指令列表错误", e);
            return AjaxResult.error("查询指令列表错误");
        }
    }

    @RequestMapping(value = "edit")
    @ResponseBody
    public AjaxResult edit(HttpServletRequest request) {
        String id = request.getParameter("id");
        Command command = new Command();
        try {
            if (!StringUtils.isEmpty(id)) {
                command = commandService.selectById(id);
            }
            List<Tag> allTags = tagService.selectAllByParams(new HashMap<>());
            Map<String, Object> data = new HashMap<>();
            data.put("command", command);
            data.put("allTags", allTags);
            return AjaxResult.success(data);
        } catch (Exception e) {
            logger.error("编辑指令错误", e);
            return AjaxResult.error("编辑指令错误");
        }
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public AjaxResult save(@RequestBody Command command) {
        try {
            if (StringUtils.isEmpty(command.getId())) {
                commandService.save(command);
            } else {
                commandService.updateById(command);
            }
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("保存指令错误", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @RequestMapping(value = "del")
    @ResponseBody
    public AjaxResult delete(HttpServletRequest request) {
        try {
            String id = request.getParameter("id");
            if (!StringUtils.isEmpty(id)) {
                commandService.deleteById(id.split(","));
            }
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("删除指令错误", e);
            return AjaxResult.error(e.getMessage());
        }
    }
}