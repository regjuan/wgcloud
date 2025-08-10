package com.wgcloud.controller;

import cn.hutool.json.JSONObject;
import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.LogInfo;
import com.wgcloud.service.LogInfoService;
import com.wgcloud.util.CodeUtil;
import com.wgcloud.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:LogInfoController.java
 * @author: http://www.wgstart.com
 * @date: 2019年11月16日
 * @Description: LogInfoController.java
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Controller
@RequestMapping("/log")
public class LogInfoController {


    private static final Logger logger = LoggerFactory.getLogger(LogInfoController.class);

    @Resource
    private LogInfoService logInfoService;

    /**
     * 根据条件查询日志信息列表
     *
     * @return
     */
    @RequestMapping(value = "list")
    @ResponseBody
    public JSONObject LogInfoList(LogInfo logInfo) {
        JSONObject resultJson = new JSONObject();
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            String hostname = null;
            if (!StringUtils.isEmpty(logInfo.getHostname())) {
                hostname = CodeUtil.unescape(logInfo.getHostname());
                params.put("hostname", hostname.trim());
            }
            PageInfo pageInfo = logInfoService.selectByParams(params, logInfo.getPage(), logInfo.getPageSize());
            resultJson.put("page", pageInfo);
            resultJson.put("logInfo", logInfo);
        } catch (Exception e) {
            logger.error("查询日志错误", e);
            resultJson.put("error", e.getMessage());
        }
        return resultJson;
    }

    /**
     * 查看日志信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "view")
    @ResponseBody
    public JSONObject viewLogInfo(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        String id = request.getParameter("id");
        LogInfo logInfo;
        try {
            logInfo = logInfoService.selectById(id);
            resultJson.put("logInfo", logInfo);
        } catch (Exception e) {
            logger.error("查看日志信息：", e);
            resultJson.put("error", e.getMessage());
        }
        return resultJson;
    }

}