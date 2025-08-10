package com.wgcloud.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONObject;
import com.github.pagehelper.PageInfo;
import com.wgcloud.dto.ChartInfo;
import com.wgcloud.dto.NetIoStateDto;
import com.wgcloud.entity.*;
import com.wgcloud.service.*;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.FormatUtil;
import com.wgcloud.util.PageUtil;
import com.wgcloud.util.staticvar.StaticKeys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:DashboardCotroller.java
 * @author: http://www.wgstart.com
 * @date: 2019年11月16日
 * @Description: DashboardCotroller.java
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Controller
@RequestMapping(value = "/dash")
public class DashboardCotroller {

    private static final Logger logger = LoggerFactory.getLogger(DashboardCotroller.class);
    @Resource
    DashboardService dashboardService;
    @Resource
    CpuStateService cpuStateService;
    @Resource
    DeskStateService deskStateService;
    @Resource
    MemStateService memStateService;
    @Resource
    NetIoStateService netIoStateService;
    @Resource
    SysLoadStateService sysLoadStateService;
    @Resource
    DbTableService dbTableService;
    @Resource
    DbInfoService dbInfoService;
    @Resource
    TcpStateService tcpStateService;
    @Resource
    SystemInfoService systemInfoService;
    @Resource
    MailSetService mailSetService;
    @Resource
    AppInfoService appInfoService;
    @Resource
    private LogInfoService logInfoService;
    @Autowired
    HeathMonitorService heathMonitorService;
    @Autowired
    HostInfoService hostInfoService;

    /**
     * 根据条件查询host列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "main")
    @ResponseBody
    public JSONObject mainList(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        Map<String, Object> params = new HashMap<String, Object>();
        List<ChartInfo> chartInfoList = new ArrayList<ChartInfo>();
        try {
            int totalSystemInfoSize = systemInfoService.countByParams(params);
            resultJson.put("totalSystemInfoSize", totalSystemInfoSize);
            int totalSizeApp = appInfoService.countByParams(params);
            resultJson.put("totalSizeApp", totalSizeApp);

            params.put("memPer", 90);
            int memPerSize_90 = systemInfoService.countByParams(params);
            double a = 0;
            if (totalSystemInfoSize != 0) {
                a = (double) memPerSize_90 / totalSystemInfoSize;
            }
            ChartInfo memPerSize_90_chart = new ChartInfo();
            memPerSize_90_chart.setItem("内存>90%");
            memPerSize_90_chart.setCount(memPerSize_90);
            memPerSize_90_chart.setPercent(FormatUtil.formatDouble(a, 2));
            chartInfoList.add(memPerSize_90_chart);

            params.put("memPer", 50);
            params.put("memPerLe", 90);
            int memPerSize_50_90 = systemInfoService.countByParams(params);
            double b = 0;
            if (totalSystemInfoSize != 0) {
                b = (double) memPerSize_50_90 / totalSystemInfoSize;
            }
            ChartInfo memPerSize_50_90_chart = new ChartInfo();
            memPerSize_50_90_chart.setItem("内存>50%且<90%");
            memPerSize_50_90_chart.setCount(memPerSize_50_90);
            memPerSize_50_90_chart.setPercent(FormatUtil.formatDouble(b, 2));
            chartInfoList.add(memPerSize_50_90_chart);
            params.clear();

            params.put("cpuPer", 90);
            int cpuPerSize_90 = systemInfoService.countByParams(params);
            double c = 0;
            if (totalSystemInfoSize != 0) {
                c = (double) cpuPerSize_90 / totalSystemInfoSize;
            }
            ChartInfo cpuPerSize_90_chart = new ChartInfo();
            cpuPerSize_90_chart.setItem("CPU>90%");
            cpuPerSize_90_chart.setCount(cpuPerSize_90);
            cpuPerSize_90_chart.setPercent(FormatUtil.formatDouble(c, 2));
            chartInfoList.add(cpuPerSize_90_chart);
            params.clear();

            params.put("cpuPer", 90);
            params.put("memPer", 90);
            int perSize_90_90 = systemInfoService.countByParams(params);
            double d = 0;
            if (totalSystemInfoSize != 0) {
                d = (double) perSize_90_90 / totalSystemInfoSize;
            }
            ChartInfo perSize_90_90_chart = new ChartInfo();
            perSize_90_90_chart.setItem("CPU和内存>90%");
            perSize_90_90_chart.setCount(perSize_90_90);
            perSize_90_90_chart.setPercent(FormatUtil.formatDouble(d, 2));
            chartInfoList.add(perSize_90_90_chart);
            params.clear();

            params.put("memPerLe", 50);
            params.put("cpuPerLe", 50);
            int perSize_50_50 = systemInfoService.countByParams(params);
            double e = 0;
            if (totalSystemInfoSize != 0) {
                e = (double) perSize_50_50 / totalSystemInfoSize;
            }
            ChartInfo perSize_50_50_chart = new ChartInfo();
            perSize_50_50_chart.setItem("CPU和内存<50%");
            perSize_50_50_chart.setCount(perSize_50_50);
            perSize_50_50_chart.setPercent(FormatUtil.formatDouble(e, 2));
            chartInfoList.add(perSize_50_50_chart);
            resultJson.put("chartInfoList", JSONUtil.parseArray(chartInfoList));
            params.clear();

            params.put("cpuPer", 90);
            int memPerSizeApp = appInfoService.countByParams(params);
            resultJson.put("memPerSizeApp", memPerSizeApp);
            params.clear();

            int logSize = logInfoService.countByParams(params);
            resultJson.put("logSize", logSize);

            params.clear();
            int dbTableSize = dbTableService.countByParams(params);
            resultJson.put("dbTableSize", dbTableSize);

            Long dbTableSum = dbTableService.sumByParams(params);
            resultJson.put("dbTableSum", dbTableSum == null ? 0 : dbTableSum);

            PageInfo pageInfoDbTableList = dbTableService.selectByParams(params, 1, 10);
            resultJson.put("dbTableList", JSONUtil.parseArray(pageInfoDbTableList.getList()));

            int dbInfoSize = dbInfoService.countByParams(params);
            resultJson.put("dbInfoSize", dbInfoSize);

            int heathSize = heathMonitorService.countByParams(params);
            resultJson.put("heathSize", heathSize);
            params.put("heathStatus", "200");
            int heath200Size = heathMonitorService.countByParams(params);
            resultJson.put("heath200Size", heath200Size);
            resultJson.put("heatherrSize", (heathSize - heath200Size));


        } catch (Exception e) {
            logger.error("主面板信息异常：", e);
            logInfoService.save("dash/main", "主面板信息错误：" + e.toString(), StaticKeys.LOG_ERROR);
        }
        return resultJson;
    }

    /**
     * 根据条件查询host列表
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "systemInfoList")
    @ResponseBody
    public JSONObject systemInfoList(SystemInfo systemInfo, HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            PageInfo<SystemInfo> pageInfo = systemInfoService.selectByParams(params, systemInfo.getPage(), systemInfo.getPageSize());

            //设置磁盘总使用率 begin
            for (SystemInfo systemInfo1 : pageInfo.getList()) {
                params.put("hostname", systemInfo1.getHostname());
                List<DeskState> deskStates = deskStateService.selectAllByParams(params);
                try {
                    Double sumSize = 0d;
                    Double useSize = 0d;
                    for (DeskState deskState : deskStates) {
                        if (!StringUtils.isEmpty(deskState.getSize()) && !StringUtils.isEmpty(deskState.getUsed())) {
                            sumSize += Double.valueOf(deskState.getSize().replace("G", ""));
                            useSize += Double.valueOf(deskState.getUsed().replace("G", ""));
                        }
                    }
                    systemInfo1.setDiskPer(0D);
                    if (sumSize != 0) {
                        systemInfo1.setDiskPer(FormatUtil.formatDouble((useSize / sumSize) * 100, 2));
                    }
                } catch (Exception e){
                    logger.error("设置磁盘总使用率错误", e);
                }
            }
            //设置磁盘总使用率 end
            resultJson.put("page", pageInfo);
        } catch (Exception e) {
            logger.error("查询服务器列表错误：", e);
            logInfoService.save("查询服务器列表错误", e.toString(), StaticKeys.LOG_ERROR);
        }
        return resultJson;
    }


    /**
     * 根据IP查询服务器详情信息
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "detail")
    @ResponseBody
    public JSONObject hostDetail(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        String id = request.getParameter("id");
        if (StringUtils.isEmpty(id)) {
            resultJson.put("error", "id is null");
            return resultJson;
        }
        String hostname = "";
        try {
            SystemInfo systemInfo = systemInfoService.selectById(id);
            resultJson.put("systemInfo", systemInfo);
            if(systemInfo != null){
                hostname = systemInfo.getHostname();
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("hostname", systemInfo.getHostname());
                List<DeskState> deskStateList = deskStateService.selectAllByParams(params);
                resultJson.put("deskStateList", deskStateList);
            }
        } catch (Exception e) {
            logger.error("服务器详细信息错误：", e);
            logInfoService.save(hostname, "查看服务器详细信息错误", e.toString());
            resultJson.put("error", e.getMessage());
        }
        return resultJson;
    }

    /**
     * 删除主机
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "del")
    @ResponseBody
    public JSONObject delete(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        String errorMsg = "删除主机信息错误：";
        try {
            if (!StringUtils.isEmpty(request.getParameter("id"))) {
                String[] ids = request.getParameter("id").split(",");
                for (String id : ids) {
                    SystemInfo sys = systemInfoService.selectById(id);
                    if(sys == null) {
                        continue;
                    }
                    if (!StringUtils.isEmpty(sys.getHostname())) {
                        hostInfoService.deleteByIp(sys.getHostname().split(","));
                    }
                    logInfoService.save("删除主机：" + sys.getHostname(), sys.getHostname(), StaticKeys.LOG_ERROR);
                }
                systemInfoService.deleteById(ids);
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


    /**
     * 根据IP查询服务器图形报表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "chart")
    @ResponseBody
    public JSONObject hostChart(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        //服务器名称
        String id = request.getParameter("id");
        String date = request.getParameter("date");
        if (StringUtils.isEmpty(id)) {
            resultJson.put("error","id is null");
            return resultJson;
        }
        String hostname = "";
        try {
            SystemInfo systemInfo = systemInfoService.selectById(id);
            resultJson.put("systemInfo", systemInfo);

            if(systemInfo != null) {
                hostname = systemInfo.getHostname();
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("hostname", systemInfo.getHostname());
                if (StringUtils.isEmpty(date)) {
                    date = DateUtil.getCurrentDate();
                }
                dashboardService.setDateParam(date, params);
                resultJson.put("datenow", date);
                resultJson.put("dateList", dashboardService.getDateList());
                List<CpuState> cpuStateList = cpuStateService.selectAllByParams(params);
                resultJson.put("cpuStateList", JSONUtil.parseArray(cpuStateList));
                resultJson.put("cpuStateMaxVal", findCpuMaxVal(cpuStateList));
                List<MemState> memStateList = memStateService.selectAllByParams(params);
                resultJson.put("memStateList", JSONUtil.parseArray(memStateList));
                List<SysLoadState> ysLoadSstateList = sysLoadStateService.selectAllByParams(params);
                resultJson.put("ysLoadSstateList", JSONUtil.parseArray(ysLoadSstateList));
                resultJson.put("ysLoadSstateMaxVal", findLoadMaxVal(ysLoadSstateList));
                List<NetIoState> netIoStateList = netIoStateService.selectAllByParams(params);
                List<NetIoStateDto> netIoStateDtoList = toNetIoStateDto(netIoStateList);
                resultJson.put("netIoStateList", JSONUtil.parseArray(netIoStateDtoList));
                resultJson.put("netIoStateBytMaxVal", findNetIoStateBytMaxVal(netIoStateDtoList));
                resultJson.put("netIoStatePckMaxVal", findNetIoStatePckMaxVal(netIoStateDtoList));
            }

        } catch (Exception e) {
            logger.error("服务器图形报表错误：", e);
            logInfoService.save(hostname, "图形报表错误", e.toString());
            resultJson.put("error",e.getMessage());
        }
        return resultJson;
    }


    private double findCpuMaxVal(List<CpuState> cpuStateList) {
        double maxval = 0;
        if (!CollectionUtil.isEmpty(cpuStateList)) {
            for (CpuState cpuState : cpuStateList) {
                if (null != cpuState.getIdle() && cpuState.getIdle() > maxval) {
                    maxval = cpuState.getIdle();
                }
                if (null != cpuState.getSys() && cpuState.getSys() > maxval) {
                    maxval = cpuState.getSys();
                }
                if (null != cpuState.getIowait() && cpuState.getIowait() > maxval) {
                    maxval = cpuState.getIowait();
                }
            }
        }
        if (maxval == 0) {
            maxval = 100;
        }
        return Math.ceil(maxval);
    }

    private double findLoadMaxVal(List<SysLoadState> ysLoadSstateList) {
        double maxval = 0;
        if (!CollectionUtil.isEmpty(ysLoadSstateList)) {
            for (SysLoadState sysLoadState : ysLoadSstateList) {
                if (null != sysLoadState.getOneLoad() && sysLoadState.getOneLoad() > maxval) {
                    maxval = sysLoadState.getOneLoad();
                }
                if (null != sysLoadState.getFiveLoad() && sysLoadState.getFiveLoad() > maxval) {
                    maxval = sysLoadState.getFiveLoad();
                }
                if (null != sysLoadState.getFifteenLoad() && sysLoadState.getFifteenLoad() > maxval) {
                    maxval = sysLoadState.getFifteenLoad();
                }
            }
        }
        if (maxval == 0) {
            maxval = 1;
        }
        return Math.ceil(maxval);
    }

    private List<NetIoStateDto> toNetIoStateDto(List<NetIoState> netIoStateList) {
        List<NetIoStateDto> dtoList = new ArrayList<>();
        for (NetIoState netIoState : netIoStateList) {
            NetIoStateDto dto = new NetIoStateDto();
            dto.setCreateTime(netIoState.getCreateTime());
            dto.setDateStr(netIoState.getDateStr());
            dto.setHostname(netIoState.getHostname());
            dto.setRxbyt(Integer.valueOf(netIoState.getRxbyt()));
            dto.setRxpck(Integer.valueOf(netIoState.getRxpck()));
            dto.setTxbyt(Integer.valueOf(netIoState.getTxbyt()));
            dto.setTxpck(Integer.valueOf(netIoState.getTxpck()));
            dtoList.add(dto);
        }
        return dtoList;
    }

    private double findNetIoStateBytMaxVal(List<NetIoStateDto> netIoStateList) {
        double maxval = 0;
        if (!CollectionUtil.isEmpty(netIoStateList)) {
            for (NetIoStateDto netIoState : netIoStateList) {
                if (null != netIoState.getRxbyt() && netIoState.getRxbyt() > maxval) {
                    maxval = netIoState.getRxbyt();
                }
                if (null != netIoState.getTxbyt() && netIoState.getTxbyt() > maxval) {
                    maxval = netIoState.getTxbyt();
                }

            }
        }
        if (maxval == 0) {
            maxval = 1;
        }
        return Math.ceil(maxval);
    }

    private double findNetIoStatePckMaxVal(List<NetIoStateDto> netIoStateList) {
        double maxval = 0;
        if (!CollectionUtil.isEmpty(netIoStateList)) {
            for (NetIoStateDto netIoState : netIoStateList) {
                if (null != netIoState.getRxpck() && netIoState.getRxpck() > maxval) {
                    maxval = netIoState.getRxpck();
                }
                if (null != netIoState.getTxpck() && netIoState.getTxpck() > maxval) {
                    maxval = netIoState.getTxpck();
                }

            }
        }
        if (maxval == 0) {
            maxval = 1;
        }
        return Math.ceil(maxval);
    }

}
