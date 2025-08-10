package com.wgcloud.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.AppInfo;
import com.wgcloud.entity.AppState;
import com.wgcloud.entity.SystemInfo;
import com.wgcloud.service.*;
import com.wgcloud.util.CodeUtil;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.PageUtil;
import com.wgcloud.util.TokenUtils;
import com.wgcloud.util.staticvar.StaticKeys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:AppInfoController.java
 * @author: http://www.wgstart.com
 * @date: 2019年11月16日
 * @Description: AppInfoController.java
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Controller
@RequestMapping("/appInfo")
public class AppInfoController {


    private static final Logger logger = LoggerFactory.getLogger(AppInfoController.class);

    @Resource
    private AppInfoService appInfoService;

    @Resource
    private AppStateService appStateService;

    @Resource
    private LogInfoService logInfoService;

    @Resource
    private DashboardService dashBoardService;

    @Resource
    private SystemInfoService systemInfoService;

    @Resource
    private HostInfoService hostInfoService;

    @Resource
    private DashboardService dashboardService;

    @Autowired
    private TokenUtils tokenUtils;


    /**
     * agent查询进程列表
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "agentList")
    public String agentList(@RequestBody String paramBean) {
        JSONObject agentJsonObject = (JSONObject) JSONUtil.parse(paramBean);
        if (!tokenUtils.checkAgentToken(agentJsonObject)) {
            logger.error("token is invalidate");
            return "error：token is invalidate";
        }
        Map<String, Object> params = new HashMap<String, Object>();
        if (null == agentJsonObject.get("hostname") || StringUtils.isEmpty(agentJsonObject.get("hostname").toString())) {
            return "";
        }
        params.put("hostname", agentJsonObject.get("hostname").toString());
        try {
            List<AppInfo> appInfoList = appInfoService.selectAllByParams(params);
            return JSONUtil.toJsonStr(appInfoList);
        } catch (Exception e) {
            logger.error("agent获取进程信息错误", e);
            logInfoService.save("agent获取进程信息错误", e.toString(), StaticKeys.LOG_ERROR);

        }
        return "";
    }

    /**
     * 根据条件查询进程列表
     *
     * @return
     */
    @RequestMapping(value = "list")
    @ResponseBody
    public JSONObject AppInfoList(AppInfo appInfo) {
        JSONObject resultJson = new JSONObject();
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            String hostname = null;
            if (!StringUtils.isEmpty(appInfo.getHostname())) {
                hostname = CodeUtil.unescape(appInfo.getHostname());
                params.put("hostname", hostname.trim());
            }
            PageInfo pageInfo = appInfoService.selectByParams(params, appInfo.getPage(), appInfo.getPageSize());
            resultJson.put("page", pageInfo);
            resultJson.put("appInfo", appInfo);
        } catch (Exception e) {
            logger.error("查询进程信息错误", e);
            logInfoService.save("查询进程信息错误", e.toString(), StaticKeys.LOG_ERROR);
            resultJson.put("error",e.getMessage());
        }
        return resultJson;
    }


    /**
     * 保存应用监控信息
     *
     * @param AppInfo
     * @return
     */
    @RequestMapping(value = "save")
    @ResponseBody
    public JSONObject saveAppInfo(@RequestBody AppInfo AppInfo) {
        JSONObject resultJson = new JSONObject();
        try {
            if (StringUtils.isEmpty(AppInfo.getId())) {
                appInfoService.save(AppInfo);
            } else {
                appInfoService.updateById(AppInfo);
            }
            resultJson.put("result","success");
        } catch (Exception e) {
            logger.error("保存进程错误：", e);
            logInfoService.save(AppInfo.getHostname(), "保存进程错误：" + e.toString(), StaticKeys.LOG_ERROR);
            resultJson.put("result","error");
            resultJson.put("msg",e.getMessage());
        }
        return resultJson;
    }

    /**
     * 添加
     * @param request
     * @return
     */
    @RequestMapping(value = "edit")
    @ResponseBody
    public JSONObject edit(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        String errorMsg = "编辑进程信息：";
        String id = request.getParameter("id");
        AppInfo appInfo = new AppInfo();
        try {
            List<SystemInfo> systemInfoList = systemInfoService.selectAllByParams(new HashMap<>());
            resultJson.put("systemInfoList", systemInfoList);
            if (!StringUtils.isEmpty(id)) {
                appInfo = appInfoService.selectById(id);
            }
            resultJson.put("appInfo", appInfo);
        } catch (Exception e) {
            logger.error(errorMsg, e);
            logInfoService.save(appInfo.getAppPid(), errorMsg + e.toString(), StaticKeys.LOG_ERROR);
            resultJson.put("error",e.getMessage());
        }
        return resultJson;
    }


    /**
     * 查看该应用统计图
     * @param request
     * @return
     */
    @RequestMapping(value = "view")
    @ResponseBody
    public JSONObject viewChart(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        String errorMsg = "查看进程统计图错误：";
        String id = request.getParameter("id");
        String date = request.getParameter("date");
        AppInfo appInfo = new AppInfo();
        try {
            appInfo = appInfoService.selectById(id);
            resultJson.put("appInfo", appInfo);

            if(appInfo != null) {
                Map<String, Object> params = new HashMap<String, Object>();
                if (StringUtils.isEmpty(date)) {
                    date = DateUtil.getCurrentDate();
                }
                dashboardService.setDateParam(date, params);
                resultJson.put("datenow", date);
                resultJson.put("dateList", dashboardService.getDateList());
                params.put("appInfoId", appInfo.getId());
                List<AppState> appStateList = appStateService.selectAllByParams(params);
                resultJson.put("appStateList", JSONUtil.parseArray(appStateList));
            }
        } catch (Exception e) {
            logger.error(errorMsg, e);
            if(appInfo != null){
                logInfoService.save(appInfo.getHostname() + ":" + appInfo.getAppPid(), errorMsg + e.toString(), StaticKeys.LOG_ERROR);
            }
            resultJson.put("error",e.getMessage());
        }
        return resultJson;
    }


    /**
     * 删除进程
     * @param request
     * @return
     */
    @RequestMapping(value = "del")
    @ResponseBody
    public JSONObject delete(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        String errorMsg = "删除进程信息错误：";
        AppInfo appInfo = new AppInfo();
        try {
            String id = request.getParameter("id");
            if (!StringUtils.isEmpty(id)) {
                //批量删除只取第一个记录日志
                appInfo = appInfoService.selectById(id.split(",")[0]);
                if(appInfo != null){
                    logInfoService.save("删除进程：" + appInfo.getHostname(), "删除进程：" + appInfo.getHostname() + "：" + appInfo.getAppPid(), StaticKeys.LOG_ERROR);
                }
                appInfoService.deleteById(id.split(","));
            }
            resultJson.put("result","success");
        } catch (Exception e) {
            logger.error(errorMsg, e);
            if(appInfo != null) {
                logInfoService.save(appInfo.getHostname() + ":" + appInfo.getAppPid(), errorMsg + e.toString(), StaticKeys.LOG_ERROR);
            }
            resultJson.put("result","error");
            resultJson.put("msg",e.getMessage());
        }
        return resultJson;
    }


}
