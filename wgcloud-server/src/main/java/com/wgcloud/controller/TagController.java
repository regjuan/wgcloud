package com.wgcloud.controller;

import com.github.pagehelper.PageInfo;
import com.wgcloud.common.AjaxResult;
import com.wgcloud.common.AjaxResult;
import com.wgcloud.entity.Tag;
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
import java.util.Map;

@Controller
@RequestMapping("/tag")
public class TagController {

    private static final Logger logger = LoggerFactory.getLogger(TagController.class);

    @Resource
    private TagService tagService;

    @RequestMapping(value = "list")
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

    @RequestMapping(value = "save")
    @ResponseBody
    public AjaxResult save(@RequestBody Tag tag) {
        try {
            if (StringUtils.isEmpty(tag.getId())) {
                tagService.save(tag);
            } else {
                tagService.updateById(tag);
            }
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("保存标签错误", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @RequestMapping(value = "del")
    @ResponseBody
    public AjaxResult delete(HttpServletRequest request) {
        try {
            String id = request.getParameter("id");
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