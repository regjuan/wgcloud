package com.wgcloud.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageInfo;
import com.wgcloud.common.AjaxResult;
import com.wgcloud.entity.ThreadMon;
import com.wgcloud.entity.ThreadMonDetail;
import com.wgcloud.entity.ThreadState;
import com.wgcloud.service.LogInfoService;
import com.wgcloud.service.ThreadMonDetailService;
import com.wgcloud.service.ThreadMonService;
import com.wgcloud.service.ThreadStateService;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.TokenUtils;
import com.wgcloud.util.staticvar.StaticKeys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/threadMon")
public class ThreadMonController {

    private static final Logger logger = LoggerFactory.getLogger(ThreadMonController.class);

    @Autowired
    private ThreadMonService threadMonService;
    @Autowired
    private ThreadStateService threadStateService;
    @Autowired
    private LogInfoService logInfoService;
    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private ThreadMonDetailService threadMonDetailService;

    @ResponseBody
    @RequestMapping(value = "/agentTasksByHost")
    public AjaxResult agentTasksByHost(@RequestBody String paramBean) {
        JSONObject agentJsonObject = (JSONObject) JSONUtil.parse(paramBean);
        if (!tokenUtils.checkAgentToken(agentJsonObject)) {
            logger.error("token is invalidate");
            return AjaxResult.error("token is invalidate");
        }
        Map<String, Object> params = new HashMap<>();
        if (null != agentJsonObject.get("hostname")) {
            params.put("hostname", agentJsonObject.get("hostname"));
        }
        try {
            List<ThreadMonDetail> detailList = threadMonDetailService.selectByParams(params);
            if (detailList.isEmpty()) {
                return AjaxResult.success(new ArrayList<>());
            }
            List<String> ids = detailList.stream().map(ThreadMonDetail::getThreadMonId).collect(java.util.stream.Collectors.toList());
            List<ThreadMon> threadMonList = threadMonService.selectByIds(ids);
            return AjaxResult.success(threadMonList);
        } catch (Exception e) {
            logger.error("agent获取线程监控任务信息错误", e);
            logInfoService.save("agent获取线程监控任务信息错误", e.toString(), StaticKeys.LOG_ERROR);
            return AjaxResult.error("agent获取线程监控任务信息错误");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/agentList")
    public AjaxResult agentList(@RequestBody String paramBean) {
        JSONObject agentJsonObject = (JSONObject) JSONUtil.parse(paramBean);
        if (!tokenUtils.checkAgentToken(agentJsonObject)) {
            logger.error("token is invalidate");
            return AjaxResult.error("token is invalidate");
        }
        Map<String, Object> params = new HashMap<>();
        if (null != agentJsonObject.get("tags")) {
            params.put("tags", agentJsonObject.get("tags"));
        }
        try {
            List<ThreadMon> threadMonList = threadMonService.selectAllByParams(params);
            return AjaxResult.success(threadMonList);
        } catch (Exception e) {
            logger.error("agent获取线程监控任务信息错误", e);
            return AjaxResult.error("agent获取线程监控任务信息错误");
        }
    }

    @ResponseBody
    @RequestMapping("/list")
    public AjaxResult list(ThreadMon threadMon) {
        Map<String, Object> params = new HashMap<>();
        try {
            if (!StringUtils.isEmpty(threadMon.getTaskName())) {
                params.put("taskName", threadMon.getTaskName().trim());
            }
            PageInfo<ThreadMon> pageInfo = threadMonService.selectByParams(params, threadMon.getPage(), threadMon.getPageSize());
            return AjaxResult.success(pageInfo);
        } catch (Exception e) {
            logger.error("查询线程监控任务信息错误", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @ResponseBody
    @PostMapping("/save")
    public AjaxResult save(@RequestBody ThreadMon threadMon) {
        try {
            if (StringUtils.isEmpty(threadMon.getId())) {
                threadMonService.save(threadMon);
            } else {
                threadMonService.updateById(threadMon);
            }
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("保存线程监控任务错误：", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("/edit")
    public AjaxResult edit(HttpServletRequest request) {
        String id = request.getParameter("id");
        try {
            ThreadMon threadMon = new ThreadMon();
            if (!StringUtils.isEmpty(id)) {
                threadMon = threadMonService.selectById(id);
            }
            return AjaxResult.success(threadMon);
        } catch (Exception e) {
            logger.error("编辑线程监控任务信息错误", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @ResponseBody
    @DeleteMapping("/del/{id}")
    public AjaxResult del(@PathVariable("id") String id) {
        try {
            if (!StringUtils.isEmpty(id)) {

                threadMonService.deleteById(id.split(","));
            }
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("删除线程监控任务错误", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("/detail/list")
    public AjaxResult detailList(ThreadMonDetail threadMonDetail, HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();
        try {
            if (!StringUtils.isEmpty(threadMonDetail.getHostname())) {
                params.put("hostname", threadMonDetail.getHostname().trim());
            }
            if (!StringUtils.isEmpty(threadMonDetail.getThreadMonId())) {
                params.put("threadMonId", threadMonDetail.getThreadMonId());
            }
            String tagId = request.getParameter("tagId");
            if (!StringUtils.isEmpty(tagId)) {
                params.put("tagId", tagId);
            }
            PageInfo<ThreadMonDetail> pageInfo = threadMonDetailService.selectByParams(params, threadMonDetail.getPage(), threadMonDetail.getPageSize());
            return AjaxResult.success(pageInfo);
        } catch (Exception e) {
            logger.error("查询线程监控任务详情错误", e);
            logInfoService.save("查询线程监控任务详情错误", e.toString(), StaticKeys.LOG_ERROR);
            return AjaxResult.error(e.getMessage());
        }
    }

}
