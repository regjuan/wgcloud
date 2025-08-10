 package com.wgcloud.controller;

import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.Command;
import com.wgcloud.entity.Playbook;
import com.wgcloud.entity.Tag;
import com.wgcloud.service.CommandService;
import com.wgcloud.service.PlaybookService;
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
@RequestMapping("/playbook")
public class PlaybookController {

    private static final Logger logger = LoggerFactory.getLogger(PlaybookController.class);

    @Resource
    private PlaybookService playbookService;
    @Resource
    private CommandService commandService;
    @Resource
    private TagService tagService;

    @RequestMapping(value = "list")
    @ResponseBody
    public cn.hutool.json.JSONObject list(Playbook playbook) {
        cn.hutool.json.JSONObject resultJson = new cn.hutool.json.JSONObject();
        Map<String, Object> params = new HashMap<>();
        try {
            if (!StringUtils.isEmpty(playbook.getPlaybookName())) {
                params.put("playbookName", playbook.getPlaybookName());
            }
            PageInfo<Playbook> pageInfo = playbookService.selectByParams(params, playbook.getPage(), playbook.getPageSize());
            resultJson.put("page", pageInfo);
            resultJson.put("playbook", playbook);
        } catch (Exception e) {
            logger.error("查询预案列表错误", e);
            resultJson.put("error", e.getMessage());
        }
        return resultJson;
    }

    @RequestMapping(value = "edit")
    @ResponseBody
    public cn.hutool.json.JSONObject edit(HttpServletRequest request) {
        cn.hutool.json.JSONObject resultJson = new cn.hutool.json.JSONObject();
        String id = request.getParameter("id");
        Playbook playbook = new Playbook();
        try {
            if (!StringUtils.isEmpty(id)) {
                playbook = playbookService.selectById(id);
            }
            List<Command> allCommands = commandService.selectByParams(new HashMap<>(), 1, 9999).getList();
            List<Tag> allTags = tagService.selectAllByParams(new HashMap<>());
            
            resultJson.put("playbook", playbook);
            resultJson.put("allCommands", allCommands);
            resultJson.put("allTags", allTags);
        } catch (Exception e) {
            logger.error("编辑预案错误", e);
            resultJson.put("error", e.getMessage());
        }
        return resultJson;
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public cn.hutool.json.JSONObject save(@RequestBody Playbook playbook) {
        cn.hutool.json.JSONObject resultJson = new cn.hutool.json.JSONObject();
        try {
            if (StringUtils.isEmpty(playbook.getId())) {
                playbookService.save(playbook);
            } else {
                playbookService.updateById(playbook);
            }
            resultJson.put("result", "success");
        } catch (Exception e) {
            logger.error("保存预案错误", e);
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
                playbookService.deleteById(id.split(","));
            }
            resultJson.put("result", "success");
        } catch (Exception e) {
            logger.error("删除预案错误", e);
            resultJson.put("result", "error");
            resultJson.put("msg", e.getMessage());
        }
        return resultJson;
    }
}