package com.wgcloud.controller;

import com.github.pagehelper.PageInfo;
import com.wgcloud.common.AjaxResult;
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
    public AjaxResult list(@RequestBody Playbook playbook) {
        Map<String, Object> params = new HashMap<>();
        try {
            if (!StringUtils.isEmpty(playbook.getPlaybookName())) {
                params.put("playbookName", playbook.getPlaybookName());
            }
            PageInfo<Playbook> pageInfo = playbookService.selectByParams(params, playbook.getPage(), playbook.getPageSize());
            return AjaxResult.success(pageInfo);
        } catch (Exception e) {
            logger.error("查询预案列表错误", e);
            return AjaxResult.error("查询预案列表错误");
        }
    }

    @RequestMapping(value = "edit")
    @ResponseBody
    public AjaxResult edit(HttpServletRequest request) {
        String id = request.getParameter("id");
        Playbook playbook = new Playbook();
        try {
            if (!StringUtils.isEmpty(id)) {
                playbook = playbookService.selectById(id);
            }
            List<Command> allCommands = commandService.selectByParams(new HashMap<>(), 1, 9999).getList();
            List<Tag> allTags = tagService.selectAllByParams(new HashMap<>());
            Map<String, Object> data = new HashMap<>();
            data.put("playbook", playbook);
            data.put("allCommands", allCommands);
            data.put("allTags", allTags);
            return AjaxResult.success(data);
        } catch (Exception e) {
            logger.error("编辑预案错误", e);
            return AjaxResult.error("编辑预案错误");
        }
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public AjaxResult save(@RequestBody Playbook playbook) {
        try {
            if (StringUtils.isEmpty(playbook.getId())) {
                playbookService.save(playbook);
            } else {
                playbookService.updateById(playbook);
            }
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("保存预案错误", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @RequestMapping(value = "del")
    @ResponseBody
    public AjaxResult delete(HttpServletRequest request) {
        try {
            String id = request.getParameter("id");
            if (!StringUtils.isEmpty(id)) {
                playbookService.deleteById(id.split(","));
            }
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("删除预案错误", e);
            return AjaxResult.error(e.getMessage());
        }
    }
}