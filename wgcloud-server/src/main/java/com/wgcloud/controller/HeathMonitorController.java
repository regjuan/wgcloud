package com.wgcloud.controller;

import com.github.pagehelper.PageInfo;
import com.wgcloud.common.AjaxResult;
import com.wgcloud.entity.HeathMonitor;
import com.wgcloud.service.HeathMonitorService;
import com.wgcloud.service.LogInfoService;
import com.wgcloud.util.staticvar.StaticKeys;
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

/**
 * @version v2.3
 * @ClassName:HeathMonitorController.java
 * @author: http://www.wgstart.com
 * @date: 2019年11月16日
 * @Description: HeathMonitorController.java
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Controller
@RequestMapping("/heathMonitor")
public class HeathMonitorController {


    private static final Logger logger = LoggerFactory.getLogger(HeathMonitorController.class);

    @Resource
    private HeathMonitorService heathMonitorService;
    @Resource
    private LogInfoService logInfoService;


    /**
     * 根据条件查询心跳监控列表
     *
     * @return
     */
    @RequestMapping(value = "list")
    @ResponseBody
    public AjaxResult heathMonitorList(HeathMonitor HeathMonitor) {
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            PageInfo pageInfo = heathMonitorService.selectByParams(params, HeathMonitor.getPage(), HeathMonitor.getPageSize());
            return AjaxResult.success(pageInfo);
        } catch (Exception e) {
            logger.error("查询服务心跳监控错误", e);

            return AjaxResult.error("查询服务心跳监控错误");
        }
    }


    /**
     * 保存心跳监控信息
     *
     * @param HeathMonitor
     * @return
     */
    @RequestMapping(value = "save")
    @ResponseBody
    public AjaxResult saveHeathMonitor(@RequestBody HeathMonitor HeathMonitor) {
        try {
            if (StringUtils.isEmpty(HeathMonitor.getId())) {
                heathMonitorService.save(HeathMonitor);
            } else {
                heathMonitorService.updateById(HeathMonitor);
            }
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("保存服务心跳监控错误：", e);
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 查看该心跳监控
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "edit")
    @ResponseBody
    public AjaxResult edit(HttpServletRequest request) {
        String errorMsg = "编辑服务心跳监控：";
        String id = request.getParameter("id");
        HeathMonitor heathMonitor = new HeathMonitor();
        try {
            if (!StringUtils.isEmpty(id)) {
                heathMonitor = heathMonitorService.selectById(id);
            }
            return AjaxResult.success(heathMonitor);
        } catch (Exception e) {
            logger.error(errorMsg, e);
            return AjaxResult.error("获取服务心跳监控错误");
        }
    }

    /**
     * 查看该心跳监控
     * @param request
     * @return
     */
    @RequestMapping(value = "view")
    @ResponseBody
    public AjaxResult view(HttpServletRequest request) {
        String errorMsg = "查看服务心跳监控：";
        String id = request.getParameter("id");
        try {
            HeathMonitor heathMonitor = heathMonitorService.selectById(id);
            return AjaxResult.success(heathMonitor);
        } catch (Exception e) {
            logger.error(errorMsg, e);
            logInfoService.save(errorMsg, e.toString(), StaticKeys.LOG_ERROR);
            return AjaxResult.error("查看服务心跳监控错误");
        }
    }


    /**
     * 删除心跳监控
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "del")
    @ResponseBody
    public AjaxResult delete(HttpServletRequest request) {
        String errorMsg = "删除服务心跳监控错误：";
        try {
            String id = request.getParameter("id");
            if (!StringUtils.isEmpty(id)) {
                //批量删除时，日志仅记录第一个
                HeathMonitor HeathMonitor = heathMonitorService.selectById(id.split(",")[0]);

                heathMonitorService.deleteById(id.split(","));
            }
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error(errorMsg, e);
            logInfoService.save(errorMsg, e.toString(), StaticKeys.LOG_ERROR);
            return AjaxResult.error(e.getMessage());
        }
    }


}