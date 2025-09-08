package com.wgcloud.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageInfo;
import com.wgcloud.common.AjaxResult;
import com.wgcloud.entity.*;
import com.wgcloud.service.*;
import com.wgcloud.util.TokenUtils;
import com.wgcloud.util.msg.WarnMailUtil;
import com.wgcloud.util.staticvar.BatchData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/agent")
public class AgentController {


    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);

    ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 40, 2, TimeUnit.MINUTES, new LinkedBlockingDeque<>());


    @Resource
    private LogInfoService logInfoService;
    @Resource
    private AlarmInfoService alarmInfoService;
    @Resource
    private SystemInfoService systemInfoService;
    @Resource
    private TagRelationService tagRelationService;
    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private CommandResultService commandResultService;
    @Autowired
    private CommandService commandService;
    @Resource
    private ContainerInfoService containerInfoService;

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
//            todo threadMon逻辑下的线程告警
//        JSONArray appInfoList = agentJsonObject.getJSONArray("appInfoList");
//        JSONArray appStateList = agentJsonObject.getJSONArray("appStateList");
        JSONArray logInfoList = agentJsonObject.getJSONArray("logInfoList");
        JSONObject systemInfo = agentJsonObject.getJSONObject("systemInfo");
        JSONObject netIoState = agentJsonObject.getJSONObject("netIoState");
        JSONArray deskStateList = agentJsonObject.getJSONArray("deskStateList");
        JSONArray threadStateList = agentJsonObject.getJSONArray("threadStateList");

        try {

            if (threadStateList != null) {
                List<ThreadState> threadStates = JSONUtil.toList(threadStateList, ThreadState.class);
                for (ThreadState threadState : threadStates) {
                    BatchData.THREAD_STATE_LIST.add(threadState);
                }
            }

            if (logInfoList != null) {
                List<LogInfo> logInfos = JSONUtil.toList(logInfoList, LogInfo.class);
                for (LogInfo logInfo : logInfos) {
                    String content = logInfo.getInfoContent();
                    // Check for agent error keywords
                    if (content != null && (content.contains("Agent获取进程列表错误") || content.contains("Agent错误"))) {
                        AlarmInfo alarmInfo = new AlarmInfo();
                        BeanUtil.copyProperties(logInfo, alarmInfo);
                        alarmInfo.setLogTitle("Agent错误");
                        alarmInfo.setSource("wgcloud-agent");
                        alarmInfoService.save(alarmInfo);
                    } else {
                        // If it's not an agent error, add it to the regular log batch
                        BatchData.LOG_INFO_LIST.add(logInfo);
                    }
                }
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
//            todo threadMon逻辑下的线程告警
//            if (appInfoList != null && appStateList != null) {
//                List<AppInfo> appInfoResList = JSONUtil.toList(appInfoList, AppInfo.class);
//                for (AppInfo appInfo : appInfoResList) {
//                    BatchData.APP_INFO_LIST.add(appInfo);
//                }
//                List<AppState> appStateResList = JSONUtil.toList(appStateList, AppState.class);
//                for (AppState appState : appStateResList) {
//                    BatchData.APP_STATE_LIST.add(appState);
//                }
//            }
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
            resultJson.put("result", "success");
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("result", "error：" + e.toString());
        } finally {
            return resultJson;
        }
    }


    @ResponseBody
    @RequestMapping("/tags")
    public AjaxResult tags(@RequestBody String paramBean) {
        JSONObject agentJsonObject = (JSONObject) JSONUtil.parse(paramBean);
        if (!tokenUtils.checkAgentToken(agentJsonObject)) {
            logger.error("token is invalidate");
            return AjaxResult.error("token is invalidate");
        }
        String hostname = agentJsonObject.getStr("hostname");
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("hostname", hostname);
            PageInfo<SystemInfo> pageInfo = systemInfoService.selectByParams(params, 1, 1);
            if (pageInfo.getList().size() > 0) {
                SystemInfo systemInfo = pageInfo.getList().get(0);
                List<String> tags = tagRelationService.selectTagNamesByHostId(systemInfo.getId());
                return AjaxResult.success(tags);
            }
            return AjaxResult.success(new JSONArray());
        } catch (Exception e) {
            logger.error("Agent获取标签列表错误", e);
            return AjaxResult.error("Agent获取标签列表错误");
        }
    }

    @ResponseBody
    @RequestMapping("/getCommand")
    public JSONArray getCommand(@RequestBody String paramBean) {
        JSONObject agentJsonObject = (JSONObject) JSONUtil.parse(paramBean);
//        if (!tokenUtils.checkAgentToken(agentJsonObject)) {
//            logger.error("token is invalidate");
//            return null;
//        }
        String hostname = agentJsonObject.get("hostname").toString();
        if (StringUtils.isEmpty(hostname)) {
            return null;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("hostname", hostname);
            params.put("status", "PENDING");
            // 获取所有待执行任务,每次最多100条
            PageInfo<CommandResult> pageInfo = commandResultService.selectByParams(params, 1, 100);
            if (pageInfo.getList().isEmpty()) {
                return new JSONArray();
            }

            List<CommandResult> commandResults = pageInfo.getList();
            JSONArray resultArray = new JSONArray();

            for (CommandResult commandResult : commandResults) {
                Command command = commandService.selectById(commandResult.getCommandId());
                if (command == null) {
                    commandResult.setStatus("CANCELLED");
                    commandResultService.updateById(commandResult);
                    continue;
                }

                commandResult.setStatus("RUNNING");
                commandResult.setStartTime(new Date());
                commandResultService.updateById(commandResult);

                JSONObject resultJson = new JSONObject();

                JSONConfig config = JSONConfig.create().setDateFormat("yyyy-MM-dd HH:mm:ss");
                resultJson.put("commandResult", JSONUtil.toJsonStr(commandResult, config));
                resultJson.put("command", JSONUtil.toJsonStr(command, config));
                resultArray.add(resultJson);
            }
            return resultArray;
        } catch (Exception e) {
            logger.error("Agent获取指令任务错误", e);
            return null;
        }
    }

    @ResponseBody
    @RequestMapping("/updateResult")
    public JSONObject updateResult(@RequestBody String paramBean) {
        JSONObject agentJsonObject = (JSONObject) JSONUtil.parse(paramBean);
        JSONObject resultJson = new JSONObject();
        if (!tokenUtils.checkAgentToken(agentJsonObject)) {
            logger.error("token is invalidate");
            resultJson.put("result", "error：token is invalidate");
            return resultJson;
        }
        try {
            CommandResult commandResult = agentJsonObject.get("commandResult", CommandResult.class);
            commandResult.setEndTime(new Date());
            commandResultService.updateById(commandResult);
            resultJson.put("result", "success");
        } catch (Exception e) {
            logger.error("Agent上报指令执行结果错误", e);
            resultJson.put("result", "error：" + e.toString());
        }
        return resultJson;
    }


    @ResponseBody
    @RequestMapping("/agentContainerInfo")
    public JSONObject agentContainerInfo(@RequestBody String paramBean) {
        JSONObject agentJsonObject = (JSONObject) JSONUtil.parse(paramBean);
        JSONObject resultJson = new JSONObject();
        if (!tokenUtils.checkAgentToken(agentJsonObject)) {
            logger.error("token is invalidate");
            resultJson.put("result", "error：token is invalidate");
            return resultJson;
        }
        try {
            String hostname = agentJsonObject.getStr("hostname");
            if (StringUtils.isEmpty(hostname)) {
                resultJson.put("result", "error：hostname is empty");
                return resultJson;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("hostname", hostname);
            SystemInfo systemInfo = null;
            PageInfo<SystemInfo> pageInfo = systemInfoService.selectByParams(params, 1, 1);
            if (pageInfo.getList().size() > 0) {
                systemInfo = pageInfo.getList().get(0);
            }
            if (systemInfo == null) {
                resultJson.put("result", "error：can not find host by hostname " + hostname);
                return resultJson;
            }

            JSONArray containerInfoList = agentJsonObject.getJSONArray("containerInfoList");
            if (containerInfoList != null) {
                List<ContainerInfo> containerInfos = JSONUtil.toList(containerInfoList, ContainerInfo.class);
                for (ContainerInfo containerInfo : containerInfos) {
                    containerInfo.setHostId(systemInfo.getId());
                    containerInfo.setHostName(systemInfo.getHostname());
                }
                containerInfoService.saveRecord(containerInfos);
            }
            resultJson.put("result", "success");
        } catch (Exception e) {
            logger.error("Agent上报容器信息错误", e);
            resultJson.put("result", "error：" + e.toString());
        }
        return resultJson;
    }

}
