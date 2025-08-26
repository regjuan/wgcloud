package com.wgcloud.controller;

import com.github.pagehelper.PageInfo;
import com.wgcloud.common.AjaxResult;
import com.wgcloud.dto.PlaybookHisDto;
import com.wgcloud.entity.CommandResult;
import com.wgcloud.entity.Playbook;
import com.wgcloud.service.CommandResultService;
import com.wgcloud.service.PlaybookService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
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
    private CommandResultService commandResultService;

    @PostMapping("/list")
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

    @GetMapping("/{id}")
    @ResponseBody
    public AjaxResult info(@PathVariable("id") String id) {
        Playbook playbook = new Playbook();
        try {
            if (!StringUtils.isEmpty(id)) {
                playbook = playbookService.selectById(id);
            }
            Map<String, Object> data = new HashMap<>();
            data.put("playbook", playbook);
            return AjaxResult.success(data);
        } catch (Exception e) {
            logger.error("获取预案信息错误", e);
            return AjaxResult.error("获取预案信息错误");
        }
    }

    @PostMapping
    @ResponseBody
    public AjaxResult create(@RequestBody Playbook playbook) {
        try {
            playbookService.save(playbook);
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("保存预案错误", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public AjaxResult update(@PathVariable("id") String id, @RequestBody Playbook playbook) {
        try {
            playbook.setId(id);
            playbookService.updateById(playbook);
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("更新预案错误", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public AjaxResult delete(@PathVariable("id") String id) {
        try {
            if (!StringUtils.isEmpty(id)) {
                playbookService.deleteById(id.split(","));
            }
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("删除预案错误", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/history")
    @ResponseBody
    public AjaxResult getHistoryList(@PathVariable("id") String id) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("taskId", id);
            List<PlaybookHisDto> list = commandResultService.selectHisByParams(params);
            return AjaxResult.success(list);
        } catch (Exception e) {
            logger.error("获取预案执行历史列表错误", e);
            return AjaxResult.error("获取预案执行历史列表错误");
        }
    }

    @GetMapping("/history/details")
    @ResponseBody
    public AjaxResult getHistoryDetails(@RequestParam("taskId") String taskId, @RequestParam("startTime")  String startTime) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("taskId", taskId);
            params.put("startTime", startTime);
            PageInfo<CommandResult> pageInfo = commandResultService.selectByParams(params, 1, 9999);
            return AjaxResult.success(pageInfo.getList());
        } catch (Exception e) {
            logger.error("获取预案单次执行详情错误", e);
            return AjaxResult.error("获取预案单次执行详情错误");
        }
    }
}
