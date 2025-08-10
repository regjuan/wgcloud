package com.wgcloud.controller;

import cn.hutool.json.JSONObject;
import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.DbTableCount;
import com.wgcloud.service.DbInfoService;
import com.wgcloud.service.DbTableCountService;
import com.wgcloud.service.DbTableService;
import com.wgcloud.service.LogInfoService;
import com.wgcloud.util.PageUtil;
import com.wgcloud.util.staticvar.StaticKeys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:DbTableCountCountController.java
 * @author: http://www.wgstart.com
 * @date: 2019年11月16日
 * @Description: DbTableCountCountController.java
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Controller
@RequestMapping("/dbTableCount")
public class DbTableCountController {


    private static final Logger logger = LoggerFactory.getLogger(DbTableCountController.class);

    @Resource
    private DbInfoService dbInfoService;
    @Resource
    private DbTableService dbTableService;
    @Resource
    private DbTableCountService dbTableCountService;
    @Resource
    private LogInfoService logInfoService;


    /**
     * 根据条件查询列表
     *
     * @return
     */
    @RequestMapping(value = "list")
    @ResponseBody
    public JSONObject dbTableCountList(DbTableCount dbTableCount) {
        JSONObject resultJson = new JSONObject();
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            PageInfo pageInfo = dbTableCountService.selectByParams(params, dbTableCount.getPage(), dbTableCount.getPageSize());
            resultJson.put("page", pageInfo);
        } catch (Exception e) {
            logger.error("查询数据源表统计信息错误", e);
            logInfoService.save("查询数据源表统计信息错误", e.toString(), StaticKeys.LOG_ERROR);
            resultJson.put("error", e.getMessage());
        }
        return resultJson;
    }


    /**
     * 保存数据源表统计信息
     *
     * @param DbTableCount
     * @return
     */
    @RequestMapping(value = "save")
    @ResponseBody
    public JSONObject saveDbTableCount(@RequestBody DbTableCount DbTableCount) {
        JSONObject resultJson = new JSONObject();
        try {
            dbTableCountService.save(DbTableCount);
            resultJson.put("result","success");
        } catch (Exception e) {
            logger.error("保存数据源表统计错误：", e);
            logInfoService.save("保存数据源表统计错误", e.toString(), StaticKeys.LOG_ERROR);
            resultJson.put("result","error");
            resultJson.put("msg",e.getMessage());
        }
        return resultJson;
    }


    /**
     * 删除数据源表统计
     * @param request
     * @return
     */
    @RequestMapping(value = "del")
    @ResponseBody
    public JSONObject delete(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        String errorMsg = "删除数据源表统计信息错误：";
        try {
            String id = request.getParameter("id");
            if (!StringUtils.isEmpty(id)) {
                dbTableCountService.deleteById(id.split(","));
            }
            resultJson.put("result","success");
        } catch (Exception e) {
            logger.error(errorMsg, e);
            logInfoService.save(errorMsg, e.toString(), StaticKeys.LOG_ERROR);
            resultJson.put("result","error");
            resultJson.put("msg",e.getMessage());
        }
        return resultJson;
    }


}