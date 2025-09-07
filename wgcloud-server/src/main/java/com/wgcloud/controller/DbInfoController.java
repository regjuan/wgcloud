package com.wgcloud.controller;

import com.github.pagehelper.PageInfo;
import com.wgcloud.common.AjaxResult;
import com.wgcloud.entity.DbInfo;
import com.wgcloud.service.DbInfoService;
import com.wgcloud.service.DbTableService;
import com.wgcloud.service.LogInfoService;
import com.wgcloud.util.jdbc.ConnectionUtil;
import com.wgcloud.util.staticvar.StaticKeys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:DbInfoController.java
 * @author: http://www.wgstart.com
 * @date: 2019年11月16日
 * @Description: DbInfoController.java
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Controller
@RequestMapping("/dbInfo")
public class DbInfoController {


    private static final Logger logger = LoggerFactory.getLogger(DbInfoController.class);

    @Resource
    private DbInfoService dbInfoService;
    @Resource
    private DbTableService dbTableService;
    @Resource
    private LogInfoService logInfoService;
    @Resource
    private ConnectionUtil connectionUtil;


    /**
     * 测试数据库连接
     *
     * @param model
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "validate")
    public AjaxResult valdateDbInfo(DbInfo DbInfo, Model model, HttpServletRequest request) {
        try {
            JdbcTemplate JdbcTemplate = connectionUtil.getJdbcTemplate(DbInfo);
            if (JdbcTemplate == null) {
                return AjaxResult.error("连接数据库错误，请检查参数是否正确。请在系统信息里查看日志");
            } else {
                return AjaxResult.success("连接数据库成功");
            }
        } catch (Exception e) {
            logger.error("测试数据源信息错误", e);

            return AjaxResult.error("测试数据源连接错误");
        }
    }


    /**
     * 根据条件查询列表
     *
     * @return
     */
    @RequestMapping(value = "list")
    @ResponseBody
    public AjaxResult DbInfoList(DbInfo DbInfo) {
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            PageInfo pageInfo = dbInfoService.selectByParams(params, DbInfo.getPage(), DbInfo.getPageSize());
            return AjaxResult.success(pageInfo);
        } catch (Exception e) {
            logger.error("查询数据源信息错误", e);

            return AjaxResult.error("查询数据源信息错误");
        }
    }


    /**
     * 添加
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "edit")
    @ResponseBody
    public AjaxResult edit(HttpServletRequest request) {
        String errorMsg = "编辑数据源：";
        String id = request.getParameter("id");
        DbInfo dbInfo = new DbInfo();
        try {
            if (!StringUtils.isEmpty(id)) {
                dbInfo = dbInfoService.selectById(id);
            }
            return AjaxResult.success(dbInfo);
        } catch (Exception e) {
            logger.error(errorMsg, e);
            return AjaxResult.error("获取数据源信息错误");
        }
    }


    /**
     * 保存数据源信息
     *
     * @param DbInfo
     * @return
     */
    @RequestMapping(value = "save")
    @ResponseBody
    public AjaxResult saveDbInfo(@RequestBody DbInfo DbInfo) {
        try {
            if (StringUtils.isEmpty(DbInfo.getId())) {
                dbInfoService.save(DbInfo);
            } else {
                dbInfoService.updateById(DbInfo);
            }
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("保存数据源错误：", e);

            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 删除数据源
     * @param request
     * @return
     */
    @RequestMapping(value = "del")
    @ResponseBody
    public AjaxResult delete(HttpServletRequest request) {
        String errorMsg = "删除数据源信息错误：";
        try {
            String id = request.getParameter("id");
            if (!StringUtils.isEmpty(id)) {
                //批量删除时，日志仅记录第一个
                DbInfo DbInfo = dbInfoService.selectById(id.split(",")[0]);
                if(DbInfo != null) {

                    dbTableService.deleteByDbInfoId(DbInfo.getId());
                }
                dbInfoService.deleteById(id.split(","));
            }
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error(errorMsg, e);

            return AjaxResult.error(e.getMessage());
        }
    }


}