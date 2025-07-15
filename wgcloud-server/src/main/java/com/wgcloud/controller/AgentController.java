package com.wgcloud.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wgcloud.entity.*;
import com.wgcloud.service.LogInfoService;
import com.wgcloud.service.SystemInfoService;
import com.wgcloud.util.TokenUtils;
import com.wgcloud.util.msg.WarnMailUtil;
import com.wgcloud.util.staticvar.BatchData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @version v2.3
 * @ClassName:AgentController.java
 * @author: http://www.wgstart.com
 * @date: 2019年11月16日
 * @Description: AgentController.java
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Controller
@RequestMapping("/agent")
public class AgentController {


    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);

    ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 40, 2, TimeUnit.MINUTES, new LinkedBlockingDeque<>());


    @Resource
    private LogInfoService logInfoService;
    @Resource
    private SystemInfoService systemInfoService;
    @Autowired
    private TokenUtils tokenUtils;

    @ResponseBody
    @RequestMapping("/minTask")
    public JSONObject minTask(@RequestBody String paramBean) {
        JSONObject agentJsonObject = (JSONObject) JSONUtil.parse(paramBean);
        JSONObject resultJson = new JSONObject();
        if (!tokenUtils.checkAgentToken(agentJsonObject)) {
            logger.error("token is invalidate");
            resultJson.put("result", "error：token is invalidate");
            return resultJson;
        }
        JSONObject cpuState = agentJsonObject.getJSONObject("cpuState");
        JSONObject memState = agentJsonObject.getJSONObject("memState");
        JSONObject sysLoadState = agentJsonObject.getJSONObject("sysLoadState");
        JSONArray appInfoList = agentJsonObject.getJSONArray("appInfoList");
        JSONArray appStateList = agentJsonObject.getJSONArray("appStateList");
        JSONObject logInfo = agentJsonObject.getJSONObject("logInfo");
        JSONObject systemInfo = agentJsonObject.getJSONObject("systemInfo");
        JSONObject netIoState = agentJsonObject.getJSONObject("netIoState");
        JSONArray deskStateList = agentJsonObject.getJSONArray("deskStateList");
        JSONArray dockerContainerList = agentJsonObject.getJSONArray("dockerContainerList");
        JSONArray dockerStateList = agentJsonObject.getJSONArray("dockerStateList");
        JSONArray pingResultList = agentJsonObject.getJSONArray("pingResultList");

        try {

            if (logInfo != null) {
                LogInfo bean = new LogInfo();
                BeanUtil.copyProperties(logInfo, bean);
                BatchData.LOG_INFO_LIST.add(bean);
            }
            if (cpuState != null) {
                CpuState bean = new CpuState();
                BeanUtil.copyProperties(cpuState, bean);
                BatchData.CPU_STATE_LIST.add(bean);
                Runnable runnable = () -> {
                    WarnMailUtil.sendCpuWarnInfo(bean);
                };
                executor.execute(runnable);

            }
            if (memState != null) {
                MemState bean = new MemState();
                BeanUtil.copyProperties(memState, bean);
                BatchData.MEM_STATE_LIST.add(bean);
                Runnable runnable = () -> {
                    WarnMailUtil.sendWarnInfo(bean);
                };
                executor.execute(runnable);
            }
            if (sysLoadState != null) {
                SysLoadState bean = new SysLoadState();
                BeanUtil.copyProperties(sysLoadState, bean);
                BatchData.SYSLOAD_STATE_LIST.add(bean);
            }
            if (netIoState != null) {
                NetIoState bean = new NetIoState();
                BeanUtil.copyProperties(netIoState, bean);
                BatchData.NETIO_STATE_LIST.add(bean);
            }
            if (appInfoList != null && appStateList != null) {
                List<AppInfo> appInfoResList = JSONUtil.toList(appInfoList, AppInfo.class);
                for (AppInfo appInfo : appInfoResList) {
                    BatchData.APP_INFO_LIST.add(appInfo);
                }
                List<AppState> appStateResList = JSONUtil.toList(appStateList, AppState.class);
                for (AppState appState : appStateResList) {
                    BatchData.APP_STATE_LIST.add(appState);
                }
            }
            if (systemInfo != null) {
                SystemInfo bean = new SystemInfo();
                BeanUtil.copyProperties(systemInfo, bean);
                BatchData.SYSTEM_INFO_LIST.add(bean);
            }
            if (deskStateList != null) {
                for (Object jsonObjects : deskStateList) {
                    DeskState bean = new DeskState();
                    BeanUtil.copyProperties(jsonObjects, bean);
                    BatchData.DESK_STATE_LIST.add(bean);
                }
            }
            if (dockerContainerList != null) {
                List<DockerContainer> dockerContainers = JSONUtil.toList(dockerContainerList, DockerContainer.class);
                for (DockerContainer container : dockerContainers) {
                    BatchData.DOCKER_CONTAINER_LIST.add(container);
                }
            }
            if (dockerStateList != null) {
                List<DockerState> dockerStates = JSONUtil.toList(dockerStateList, DockerState.class);
                for (DockerState state : dockerStates) {
                    BatchData.DOCKER_STATE_LIST.add(state);
                }
            }
            if (pingResultList != null) {
                List<PingResult> pingResults = JSONUtil.toList(pingResultList, PingResult.class);
                for (PingResult result : pingResults) {
                    BatchData.PING_RESULT_LIST.add(result);
                }
            }
            resultJson.put("result", "success");
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("result", "error：" + e.toString());
        } finally {
            return resultJson;
        }
    }

    /**
     * 接收Agent端指令执行结果
     */
    @ResponseBody
    @RequestMapping("/commandResult")
    public JSONObject commandResult(@RequestBody String paramBean) {
        JSONObject resultJson = new JSONObject();
        
        try {
            JSONObject agentJsonObject = (JSONObject) JSONUtil.parse(paramBean);
            
            if (!tokenUtils.checkAgentToken(agentJsonObject)) {
                logger.error("token is invalidate");
                resultJson.put("result", "error：token is invalidate");
                return resultJson;
            }
            
            String taskId = agentJsonObject.getStr("taskId");
            String recordId = agentJsonObject.getStr("recordId");
            String hostname = agentJsonObject.getStr("hostname");
            String command = agentJsonObject.getStr("command");
            Boolean success = agentJsonObject.getBool("success");
            Integer exitCode = agentJsonObject.getInt("exitCode");
            String output = agentJsonObject.getStr("output");
            String errorOutput = agentJsonObject.getStr("errorOutput");
            String errorMessage = agentJsonObject.getStr("errorMessage");
            Long executionTime = agentJsonObject.getLong("executionTime");
            
            logger.info("接收到Agent指令执行结果，任务ID: {}, 记录ID: {}, 主机: {}, 成功: {}", 
                       taskId, recordId, hostname, success);
            
            // 这里应该调用CommandService来更新执行记录
            // 由于当前代码结构限制，先记录日志，实际项目中应该注入CommandService
            String logContent = String.format("指令执行结果 - 任务:%s, 记录:%s, 成功:%s, 退出码:%d, 耗时:%dms", 
                                             taskId, recordId, success, exitCode, executionTime);
            
            // 记录到日志表
            logInfoService.save(hostname + "：指令执行结果", logContent, 
                               success ? StaticKeys.LOG_INFO : StaticKeys.LOG_ERROR);
            
            resultJson.put("result", "success");
            
        } catch (Exception e) {
            logger.error("处理指令执行结果失败: {}", e.getMessage(), e);
            resultJson.put("result", "error: " + e.getMessage());
        }
        
        return resultJson;
    }

}
