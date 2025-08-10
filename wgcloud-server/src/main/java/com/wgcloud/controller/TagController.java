package com.wgcloud.controller;

import cn.hutool.json.JSONObject;
import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.Tag;
import com.wgcloud.service.TagService;
import com.wgcloud.util.PageUtil;
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
    public JSONObject list(Tag tag) {
        JSONObject resultJson = new JSONObject();
        Map<String, Object> params = new HashMap<>();
        try {
            if (!StringUtils.isEmpty(tag.getTagName())) {
                params.put("tagName", tag.getTagName());
            }
            PageInfo<Tag> pageInfo = tagService.selectByParams(params, tag.getPage(), tag.getPageSize());
            resultJson.put("page", pageInfo);
            resultJson.put("tag", tag);
        } catch (Exception e) {
            logger.error("查询标签列表错误", e);
            resultJson.put("error", e.getMessage());
        }
        return resultJson;
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public JSONObject save(@RequestBody Tag tag) {
        JSONObject resultJson = new JSONObject();
        try {
            if (StringUtils.isEmpty(tag.getId())) {
                tagService.save(tag);
            } else {
                tagService.updateById(tag);
            }
            resultJson.put("result", "success");
        } catch (Exception e) {
            logger.error("保存标签错误", e);
            resultJson.put("result", "error");
            resultJson.put("msg", e.getMessage());
        }
        return resultJson;
    }

    @RequestMapping(value = "del")
    @ResponseBody
    public JSONObject delete(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            String id = request.getParameter("id");
            if (!StringUtils.isEmpty(id)) {
                tagService.deleteById(id.split(","));
            }
            resultJson.put("result", "success");
        } catch (Exception e) {
            logger.error("删除标签错误", e);
            resultJson.put("result", "error");
            resultJson.put("msg", e.getMessage());
        }
        return resultJson;
    }
}
