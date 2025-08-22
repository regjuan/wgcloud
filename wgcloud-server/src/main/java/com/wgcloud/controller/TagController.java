package com.wgcloud.controller;

import com.github.pagehelper.PageInfo;
import com.wgcloud.common.AjaxResult;
import com.wgcloud.entity.Tag;
import com.wgcloud.service.TagService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/tag")
public class TagController {

    private static final Logger logger = LoggerFactory.getLogger(TagController.class);

    @Resource
    private TagService tagService;

    @PostMapping("/list")
    @ResponseBody
    public AjaxResult list(@RequestBody Tag tag) {
        Map<String, Object> params = new HashMap<>();
        try {
            if (!StringUtils.isEmpty(tag.getTagName())) {
                params.put("tagName", tag.getTagName());
            }
            PageInfo<Tag> pageInfo = tagService.selectByParams(params, tag.getPage(), tag.getPageSize());
            return AjaxResult.success(pageInfo);
        } catch (Exception e) {
            logger.error("查询标签列表错误", e);
            return AjaxResult.error("查询标签列表错误");
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public AjaxResult info(@PathVariable("id") String id) {
        try {
            Tag tag = tagService.selectById(id);
            return AjaxResult.success(tag);
        } catch (Exception e) {
            logger.error("获取标签信息错误", e);
            return AjaxResult.error("获取标签信息错误");
        }
    }

    @PostMapping
    @ResponseBody
    public AjaxResult create(@RequestBody Tag tag) {
        try {
            tagService.save(tag);
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("保存标签错误", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public AjaxResult update(@PathVariable("id") String id, @RequestBody Tag tag) {
        try {
            tag.setId(id);
            tagService.updateById(tag);
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("更新标签错误", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public AjaxResult delete(@PathVariable("id") String id) {
        try {
            if (!StringUtils.isEmpty(id)) {
                tagService.deleteById(id.split(","));
            }
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("删除标签错误", e);
            return AjaxResult.error(e.getMessage());
        }
    }
}
