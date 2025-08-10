package com.wgcloud.controller;

import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONObject;
import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.DbInfo;
import com.wgcloud.entity.DbTable;
import com.wgcloud.entity.DbTableCount;
import com.wgcloud.service.DbInfoService;
import com.wgcloud.service.DbTableCountService;
import com.wgcloud.service.DbTableService;
import com.wgcloud.service.LogInfoService;
import com.wgcloud.util.PageUtil;
import com.wgcloud.util.jdbc.RDSConnection;
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
import java.util.List;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:DbTableController.java
 * @author: http://www.wgstart.com
 * @date: 2019年11月16日
 * @Description: DbTableController.java
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Controller
@RequestMapping("/dbTable")
public class DbTableController {


    private static final Logger logger = LoggerFactory.getLogger(DbTableController.class);

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
     * @return
     */
    @RequestMapping(value = "list")
    @ResponseBody
    public JSONObject DbTableList(DbTable DbTable) {
        JSONObject resultJson = new JSONObject();
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            PageInfo<DbTable> pageInfo = dbTableService.selectByParams(params, DbTable.getPage(), DbTable.getPageSize());
            List<DbInfo> dbInfoList = dbInfoService.selectAllByParams(params);
            for (DbTable dbTable : pageInfo.getList()) {
                for (DbInfo dbInfo : dbInfoList) {
                    if (dbInfo.getId().equals(dbTable.getDbInfoId())) {
                        dbTable.setTableName(dbInfo.getAliasName());
                    }
                }
            }
            resultJson.put("page", pageInfo);
        } catch (Exception e) {
            logger.error("查询数据表信息错误", e);
            logInfoService.save("查询数据表信息错误", e.toString(), StaticKeys.LOG_ERROR);
            resultJson.put("error", e.getMessage());
        }
        return resultJson;
    }


    /**
     * 保存数据源表信息
     *
     * @param DbTable
     * @return
     */
    @RequestMapping(value = "save")
    @ResponseBody
    public JSONObject saveDbTable(@RequestBody DbTable DbTable) {
        JSONObject resultJson = new JSONObject();
        try {
            String whereVal = DbTable.getWhereVal().toLowerCase();
            if (!StringUtils.isEmpty(whereVal)) {
                String[] sqlinkeys = RDSConnection.SQL_INKEYS.split(",");
                for (String sqlinkey : sqlinkeys) {
                    if (whereVal.contains(sqlinkey)) {
                        resultJson.put("result", "error");
                        resultJson.put("msg", "where语句含有sql敏感字符" + sqlinkey + "，请检查");
                        return resultJson;
                    }
                }
            }
            if (StringUtils.isEmpty(DbTable.getId())) {
                dbTableService.save(DbTable);
            } else {
                dbTableService.updateById(DbTable);
            }
            resultJson.put("result","success");
        } catch (Exception e) {
            logger.error("保存数据表错误：", e);
            logInfoService.save("保存数据表错误", e.toString(), StaticKeys.LOG_ERROR);
            resultJson.put("result","error");
            resultJson.put("msg",e.getMessage());
        }
        return resultJson;
    }


    /**
     * 查看数据源表信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "edit")
    @ResponseBody
    public JSONObject editDbTable(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            String id = request.getParameter("id");
            DbTable dbTableInfo = new DbTable();
            if (!StringUtils.isEmpty(id)) {
                dbTableInfo = dbTableService.selectById(id);
            }
            List<DbInfo> dbInfoList = dbInfoService.selectAllByParams(new HashMap<>());
            resultJson.put("dbInfoList", dbInfoList);
            resultJson.put("dbTable", dbTableInfo);
        } catch (Exception e) {
            logger.error("查看数据表错误：", e);
            logInfoService.save("查看数据表错误", e.toString(), StaticKeys.LOG_ERROR);
            resultJson.put("error", e.getMessage());
        }
        return resultJson;
    }

    /**
     * 查看数据源表图表统计信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "viewChart")
    @ResponseBody
    public JSONObject viewChartDbTable(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            String id = request.getParameter("id");
            if (!StringUtils.isEmpty(id)) {
                DbTable dbTableInfo = dbTableService.selectById(id);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("dbTableId", id);
                List<DbTableCount> dbTableCounts = dbTableCountService.selectAllByParams(params);
                resultJson.put("dbTableCounts", JSONUtil.parseArray(dbTableCounts));
                resultJson.put("dbTable", dbTableInfo);
                if(dbTableInfo != null){
                    String sql = RDSConnection.query_table_count.replace("{tableName}", dbTableInfo.getTableName()) + dbTableInfo.getWhereVal();
                    resultJson.put("sqlCount", sql);
                }
            }
        } catch (Exception e) {
            logger.error("查看数据表图表统计错误：", e);
            logInfoService.save("查看数据表图表统计错误", e.toString(), StaticKeys.LOG_ERROR);
            resultJson.put("error", e.getMessage());
        }
        return resultJson;
    }


    /**
     * 删除数据源表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "del")
    @ResponseBody
    public JSONObject delete(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        String errorMsg = "删除数据源表信息错误：";
        try {
            String id = request.getParameter("id");
            if (!StringUtils.isEmpty(id)) {
                //批量删除时，日志仅记录第一个
                DbTable dbTable = dbTableService.selectById(id.split(",")[0]);
                if(dbTable != null) {
                    logInfoService.save("删除数据表：" + dbTable.getTableName(), "删除数据表：" + dbTable.getTableName(), StaticKeys.LOG_ERROR);
                }
                dbTableService.deleteById(id.split(","));
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