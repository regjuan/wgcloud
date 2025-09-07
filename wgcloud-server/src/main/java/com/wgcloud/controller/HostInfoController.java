package com.wgcloud.controller;

import com.wgcloud.common.AjaxResult;
import com.wgcloud.entity.SystemInfo;
import com.wgcloud.service.LogInfoService;
import com.wgcloud.service.SystemInfoService;
import com.wgcloud.util.staticvar.StaticKeys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @version v2.3
 * @ClassName:HostInfoController.java
 * @author: http://www.wgstart.com
 * @date: 2019年11月16日
 * @Description: 主机备注信息
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Controller
@RequestMapping("/host")
public class HostInfoController {


    private static final Logger logger = LoggerFactory.getLogger(HostInfoController.class);


    @Resource
    private SystemInfoService systemInfoService;
    @Resource
    private LogInfoService logInfoService;


    /**
     * 保存主机备注信息
     *
     * @return
     */
    @RequestMapping(value = "save")
    @ResponseBody
    public AjaxResult saveHostInfo(@RequestBody SystemInfo systemInfo) {
        try {
            //备注信息是更新，不是新增
            if (!StringUtils.isEmpty(systemInfo.getId())) {
                SystemInfo ho = systemInfoService.selectById(systemInfo.getId());
                ho.setRemark(systemInfo.getRemark());
                systemInfoService.updateById(ho);
            }
             return AjaxResult.success();
        } catch (Exception e) {
            logger.error("保存主机备注信息错误：", e);

            return AjaxResult.error(e.getMessage());
        }
    }


}