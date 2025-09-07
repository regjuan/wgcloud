package com.wgcloud.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageInfo;
import com.wgcloud.common.AjaxResult;
import com.wgcloud.dto.LogMonitorReportByHostDto;
import com.wgcloud.entity.LogInfo;
import com.wgcloud.entity.LogMon;
import com.wgcloud.service.LogInfoService;
import com.wgcloud.service.LogMonService;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.TokenUtils;
import com.wgcloud.util.staticvar.StaticKeys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/logMon")
public class LogMonController {

    private static final Logger logger = LoggerFactory.getLogger(LogMonController.class);

    @Resource
    private LogMonService logMonService;
    @Resource
    private LogInfoService logInfoService;
    @Resource
    private TokenUtils tokenUtils;

    @RequestMapping(value = "agentList")
    @ResponseBody
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
            List<LogMon> appInfoList = logMonService.selectAllByParams(params);
            return AjaxResult.success(appInfoList);
        } catch (Exception e) {
            logger.error("agent获取日志监控任务信息错误", e);

            return AjaxResult.error("agent获取日志监控任务信息错误");
        }
    }

    @RequestMapping(value = "statusByHost")
    @ResponseBody
    public AjaxResult statusByHost(HttpServletRequest request) {
        try {
            String startTime = request.getParameter("startTime");
            String endTime = request.getParameter("endTime");
            String hostname = request.getParameter("hostname");
            String tag = request.getParameter("tag");

            if (StringUtils.isEmpty(startTime)) {
                startTime = DateUtil.getDateTimeString(DateUtil.getDailyStartTime(new Date())) ;
                endTime = DateUtil.getDateTimeString(DateUtil.getDailyEndTime(new Date()));
            }
            List<LogMonitorReportByHostDto> statusList = logMonService.getStatusByHost(startTime, endTime, hostname, tag);
            return AjaxResult.success(statusList);
        } catch (Exception e) {
            logger.error("获取主机日志监控状态错误", e);

            return AjaxResult.error(e.getMessage());
        }
    }

    @RequestMapping(value = "alertDetails")
    @ResponseBody
    public AjaxResult alertDetails(HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();
        try {
            String logMonId = request.getParameter("logMonId");
            String startTime = request.getParameter("startTime");
            String endTime = request.getParameter("endTime");
            String page = request.getParameter("page");
            String pageSize = request.getParameter("pageSize");

            params.put("logMonId", logMonId);
            params.put("startTime", startTime);
            params.put("endTime", endTime);

            int pageNum = 1;
            if (!StringUtils.isEmpty(page)) {
                pageNum = Integer.parseInt(page);
            }
            int pageSizeNum = 10;
            if (!StringUtils.isEmpty(pageSize)) {
                pageSizeNum = Integer.parseInt(pageSize);
            }

            PageInfo pageInfo = logInfoService.selectByParams(params, pageNum, pageSizeNum);
            return AjaxResult.success(pageInfo);
        } catch (Exception e) {
            logger.error("查询日志监控明细错误", e);

            return AjaxResult.error(e.getMessage());
        }
    }


    @RequestMapping(value = "list")
    @ResponseBody
    public AjaxResult list(LogMon logMon) {
        Map<String, Object> params = new HashMap<>();
        try {
            if (!StringUtils.isEmpty(logMon.getAppName())) {
                params.put("appName", logMon.getAppName().trim());
            }
            PageInfo pageInfo = logMonService.selectByParams(params, logMon.getPage(), logMon.getPageSize());
            Map<String, Object> data = new HashMap<>();
            data.put("page", pageInfo);
//            data.put("logMon", logMon);
            return AjaxResult.success(data);
        } catch (Exception e) {
            logger.error("查询日志监控任务信息错误", e);

            return AjaxResult.error(e.getMessage());
        }
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public AjaxResult save(@RequestBody LogMon logMon) {
        try {
            if (StringUtils.isEmpty(logMon.getId())) {
                logMonService.save(logMon);
            } else {
                logMonService.updateById(logMon);
            }
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("保存日志监控任务错误：", e);

            return AjaxResult.error(e.getMessage());
        }
    }

    @RequestMapping(value = "edit")
    @ResponseBody
    public AjaxResult edit(HttpServletRequest request) {
        String id = request.getParameter("id");
        try {
            LogMon logMon = new LogMon();
            if (!StringUtils.isEmpty(id)) {
                logMon = logMonService.selectById(id);
            }
            return AjaxResult.success(logMon);
        } catch (Exception e) {
            logger.error("编辑日志监控任务信息错误", e);

            return AjaxResult.error(e.getMessage());
        }
    }

    @DeleteMapping("/del/{id}")
    @ResponseBody
    public AjaxResult del(@PathVariable("id") String id) {
        String errorMsg = "删除日志监控任务错误：";
        try {
            if (!StringUtils.isEmpty(id)) {

                logMonService.deleteById(id.split(","));
            }
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error(errorMsg, e);

            return AjaxResult.error(e.getMessage());
        }
    }
}