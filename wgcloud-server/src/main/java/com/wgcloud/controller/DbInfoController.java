package com.wgcloud.controller;

import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONObject;
import com.github.pagehelper.PageInfo;
import com.wgcloud.dto.MessageDto;
import com.wgcloud.entity.DbInfo;
import com.wgcloud.service.DbInfoService;
import com.wgcloud.service.DbTableService;
import com.wgcloud.service.LogInfoService;
import com.wgcloud.util.PageUtil;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String valdateDbInfo(DbInfo DbInfo, Model model, HttpServletRequest request) {
        MessageDto messageDto = new MessageDto();
        try {
            JdbcTemplate JdbcTemplate = connectionUtil.getJdbcTemplate(DbInfo);
            if (JdbcTemplate == null) {
                messageDto.setCode("1");
                messageDto.setMsg("连接数据库错误，请检查参数是否正确。请在系统信息里查看日志");
            } else {
                messageDto.setCode("0");
                messageDto.setMsg("连接数据库成功");
            }
        } catch (Exception e) {
            logger.error("测试数据源信息错误", e);
            logInfoService.save("测试数据源信息错误", e.toString(), StaticKeys.LOG_ERROR);

        }
        return JSONUtil.toJsonStr(messageDto);
    }


    /**
     * 根据条件查询列表
     *
     * @return
     */
    @RequestMapping(value = "list")
    @ResponseBody
    public JSONObject DbInfoList(DbInfo DbInfo) {
        JSONObject resultJson = new JSONObject();
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            PageInfo pageInfo = dbInfoService.selectByParams(params, DbInfo.getPage(), DbInfo.getPageSize());
            resultJson.put("page", pageInfo);
        } catch (Exception e) {
            logger.error("查询数据源信息错误", e);
            logInfoService.save("查询数据源信息错误", e.toString(), StaticKeys.LOG_ERROR);
            resultJson.put("error", e.getMessage());
        }
        return resultJson;
    }


    /**
     * 添加
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "edit")
    @ResponseBody
    public JSONObject edit(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        String errorMsg = "编辑数据源：";
        String id = request.getParameter("id");
        DbInfo dbInfo = new DbInfo();
        try {
            if (!StringUtils.isEmpty(id)) {
                dbInfo = dbInfoService.selectById(id);
            }
            resultJson.put("dbInfo", dbInfo);
        } catch (Exception e) {
            logger.error(errorMsg, e);
            if(dbInfo != null) {
                logInfoService.save(dbInfo.getDbName(), errorMsg + e.toString(), StaticKeys.LOG_ERROR);
            }
            resultJson.put("error", e.getMessage());
        }
        return resultJson;
    }


    /**
     * 保存数据源信息
     *
     * @param DbInfo
     * @return
     */
    @RequestMapping(value = "save")
    @ResponseBody
    public JSONObject saveDbInfo(@RequestBody DbInfo DbInfo) {
        JSONObject resultJson = new JSONObject();
        try {
            if (StringUtils.isEmpty(DbInfo.getId())) {
                dbInfoService.save(DbInfo);
            } else {
                dbInfoService.updateById(DbInfo);
            }
            resultJson.put("result","success");
        } catch (Exception e) {
            logger.error("保存数据源错误：", e);
            logInfoService.save("保存数据源错误", e.toString(), StaticKeys.LOG_ERROR);
            resultJson.put("result","error");
            resultJson.put("msg",e.getMessage());
        }
        return resultJson;
    }


    /**
     * 删除数据源
     * @param request
     * @return
     */
    @RequestMapping(value = "del")
    @ResponseBody
    public JSONObject delete(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        String errorMsg = "删除数据源信息错误：";
        try {
            String id = request.getParameter("id");
            if (!StringUtils.isEmpty(id)) {
                //批量删除时，日志仅记录第一个
                DbInfo DbInfo = dbInfoService.selectById(id.split(",")[0]);
                if(DbInfo != null) {
                    logInfoService.save("删除数据源：" + DbInfo.getAliasName(), "删除数据源：" + DbInfo.getIp() + "：" + DbInfo.getPort() +
                            "，数据库别名" + DbInfo.getAliasName(), StaticKeys.LOG_ERROR);
                    dbTableService.deleteByDbInfoId(DbInfo.getId());
                }
                dbInfoService.deleteById(id.split(","));
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