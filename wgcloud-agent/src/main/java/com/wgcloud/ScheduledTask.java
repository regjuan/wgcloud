package com.wgcloud;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wgcloud.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * @version V2.3
 * @ClassName:ScheduledTask.java
 * @author: wgcloud
 * @date: 2019年11月16日
 * @Description: ScheduledTask.java
 * @Copyright: 2017-2024 www.wgstart.com. All rights reserved.
 */
@Component
public class ScheduledTask {

    private Logger logger = LoggerFactory.getLogger(ScheduledTask.class);
    public static List<AppInfo> appInfoList = Collections.synchronizedList(new ArrayList<AppInfo>());
    @Autowired
    private RestUtil restUtil;
    @Autowired
    private CommonConfig commonConfig;

    private SystemInfo systemInfo = null;


    /**
     * 线程池
     */
    static ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 10, TimeUnit.MINUTES, new LinkedBlockingDeque<>());

    /**
     * 60秒后执行，每隔120秒执行, 单位：ms。
     */
    @Scheduled(initialDelay = 59 * 1000L, fixedRate = 120 * 1000)
    public void minTask() {
        List<AppInfo> APP_INFO_LIST_CP = new ArrayList<AppInfo>();
        APP_INFO_LIST_CP.addAll(appInfoList);
        JSONObject jsonObject = new JSONObject();
        LogInfo logInfo = new LogInfo();
        Timestamp t = FormatUtil.getNowTime();
        logInfo.setHostname(commonConfig.getBindIp() + "：Agent错误");
        logInfo.setCreateTime(t);
        try {
            oshi.SystemInfo si = new oshi.SystemInfo();

            HardwareAbstractionLayer hal = si.getHardware();
            OperatingSystem os = si.getOperatingSystem();

            // 操作系统信息
            systemInfo = OshiUtil.os(hal.getProcessor(), os);
            systemInfo.setCreateTime(t);
            // 文件系统信息
            List<DeskState> deskStateList = OshiUtil.file(t, os.getFileSystem());
            // cpu信息
            CpuState cpuState = OshiUtil.cpu(hal.getProcessor());
            cpuState.setCreateTime(t);
            // 内存信息
            MemState memState = OshiUtil.memory(hal.getMemory());
            memState.setCreateTime(t);
            // 网络流量信息
            NetIoState netIoState = OshiUtil.net(hal);
            netIoState.setCreateTime(t);
            // 系统负载信息
            SysLoadState sysLoadState = OshiUtil.getLoadState(systemInfo, hal.getProcessor());
            if (sysLoadState != null) {
                sysLoadState.setCreateTime(t);
            }
            if (cpuState != null) {
                jsonObject.put("cpuState", cpuState);
            }
            if (memState != null) {
                jsonObject.put("memState", memState);
            }
            if (netIoState != null) {
                jsonObject.put("netIoState", netIoState);
            }
            if (sysLoadState != null) {
                jsonObject.put("sysLoadState", sysLoadState);
            }
            if (systemInfo != null) {
                if (memState != null) {
                    systemInfo.setVersionDetail(systemInfo.getVersion() + "，总内存：" + oshi.util.FormatUtil.formatBytes(hal.getMemory().getTotal()));
                    systemInfo.setMemPer(memState.getUsePer());
                } else {
                    systemInfo.setMemPer(0d);
                }
                if (cpuState != null) {
                    systemInfo.setCpuPer(cpuState.getSys());
                } else {
                    systemInfo.setCpuPer(0d);
                }
                jsonObject.put("systemInfo", systemInfo);
            }
            if (deskStateList != null) {
                jsonObject.put("deskStateList", deskStateList);
            }
            //进程信息
            if (APP_INFO_LIST_CP.size() > 0) {
                List<AppInfo> appInfoResList = new ArrayList<>();
                List<AppState> appStateResList = new ArrayList<>();
                for (AppInfo appInfo : APP_INFO_LIST_CP) {
                    appInfo.setHostname(commonConfig.getBindIp());
                    appInfo.setCreateTime(t);
                    appInfo.setState("1");
                    String pid = FormatUtil.getPidByFile(appInfo);
                    if (StringUtils.isEmpty(pid)) {
                        continue;
                    }
                    AppState appState = OshiUtil.getLoadPid(pid, os, hal.getMemory());
                    if (appState != null) {
                        appState.setCreateTime(t);
                        appState.setAppInfoId(appInfo.getId());
                        appInfo.setMemPer(appState.getMemPer());
                        appInfo.setCpuPer(appState.getCpuPer());
                        appInfoResList.add(appInfo);
                        appStateResList.add(appState);
                    }
                }
                jsonObject.put("appInfoList", appInfoResList);
                jsonObject.put("appStateList", appStateResList);
            }

            logger.debug("---------------" + jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
            logInfo.setInfoContent(e.toString());
        } finally {
            if (!StringUtils.isEmpty(logInfo.getInfoContent())) {
                jsonObject.put("logInfo", logInfo);
            }
            restUtil.post(commonConfig.getServerUrl() + "/wgcloud/agent/minTask", jsonObject);
        }

    }


    /**
     * 30秒后执行，每隔5分钟执行, 单位：ms。
     * 获取监控进程
     */
    @Scheduled(initialDelay = 28 * 1000L, fixedRate = 300 * 1000)
    public void appInfoListTask() {
        JSONObject jsonObject = new JSONObject();
        LogInfo logInfo = new LogInfo();
        Timestamp t = FormatUtil.getNowTime();
        logInfo.setHostname(commonConfig.getBindIp() + "：Agent获取进程列表错误");
        logInfo.setCreateTime(t);
        try {
            JSONObject paramsJson = new JSONObject();
            paramsJson.put("hostname", commonConfig.getBindIp());
            String resultJson = restUtil.post(commonConfig.getServerUrl() + "/wgcloud/appInfo/agentList", paramsJson);
            if (resultJson != null) {
                JSONArray resultArray = JSONUtil.parseArray(resultJson);
                appInfoList.clear();
                if (resultArray.size() > 0) {
                    appInfoList = JSONUtil.toList(resultArray, AppInfo.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logInfo.setInfoContent(e.toString());
        } finally {
            if (!StringUtils.isEmpty(logInfo.getInfoContent())) {
                jsonObject.put("logInfo", logInfo);
            }
            restUtil.post(commonConfig.getServerUrl() + "/wgcloud/agent/minTask", jsonObject);
        }
    }


    /**
     * 20秒后执行，每隔1分钟执行, 单位：ms。
     * 获取指令任务并执行
     */
    @Scheduled(initialDelay = 20 * 1000L, fixedRate = 60 * 1000)
    public void commandTaskExecutor() {
        JSONObject resultJson = null;
        try {
            //1. 获取任务列表
            JSONObject paramsJson = new JSONObject();
            paramsJson.put("hostname", commonConfig.getBindIp());
            String resultStr = restUtil.post(commonConfig.getServerUrl() + "/wgcloud/agent/getCommand", paramsJson);
            if (StringUtils.isEmpty(resultStr)) {
                return;
            }

            JSONArray commandList = JSONUtil.parseArray(resultStr);
            if (commandList == null || commandList.isEmpty()) {
                return;
            }
            logger.info("Fetched {} command tasks from server.", commandList.size());

            for (Object commandJsonObj : commandList) {
                final JSONObject commandJson = (JSONObject) commandJsonObj;
                executor.execute(() -> {
                    CommandResult commandResult = null;
                    Command command = null;
                    try {
                        // 2. 执行前判断超时
                        String commandResultStr = commandJson.getStr("commandResult");
                        String commandStr = commandJson.getStr("command");
                        commandResult = cn.hutool.json.JSONUtil.toBean(commandResultStr, CommandResult.class);
                        command = cn.hutool.json.JSONUtil.toBean(commandStr, Command.class);
                        if (commandResult.getExpireTime() == null) {
                            logger.error("Command task [{}] has a NULL expireTime after parsing. Raw JSON: {}", commandResult.getId(), commandResultStr);
                            commandResult.setStatus("FAILED");
                            commandResult.setStderr("Agent failed to read task expireTime.");
                        } else {
                            if (System.currentTimeMillis() > commandResult.getExpireTime().getTime()) {
                                logger.warn("Command task [{}] timed out in agent queue before execution.", commandResult.getId());
                                commandResult.setStatus("TIMEOUT");
                                commandResult.setStderr("Task timed out in agent queue before execution.");
                            } else {
                                // 3. 执行任务
                                Process process = null;
                                StringBuilder stdout = new StringBuilder();
                                StringBuilder stderr = new StringBuilder();
                                int exitCode = -1;
                                try {
                                    process = Runtime.getRuntime().exec(command.getCmdContent());
                                    BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                                    String line;
                                    while ((line = stdoutReader.readLine()) != null) {
                                        stdout.append(line).append("\n");
                                    }
                                    BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                                    while ((line = stderrReader.readLine()) != null) {
                                        stderr.append(line).append("\n");
                                    }
                                    boolean finished = process.waitFor(command.getTimeout(), TimeUnit.SECONDS);
                                    if (!finished) {
                                        process.destroy();
                                        commandResult.setStatus("TIMEOUT");
                                        stderr.append("\nTask execution timed out in agent.");
                                    } else {
                                        exitCode = process.exitValue();
                                        commandResult.setStatus(exitCode == 0 ? "SUCCESS" : "FAILED");
                                    }
                                } catch (Exception e) {
                                    logger.error("Execute command error", e);
                                    commandResult.setStatus("FAILED");
                                    stderr.append(e.getMessage());
                                } finally {
                                    if (process != null) {
                                        process.destroy();
                                    }
                                }
                                commandResult.setStdout(stdout.toString());
                                commandResult.setStderr(stderr.toString());
                                commandResult.setExitCode(exitCode);
                            }
                        }
                        commandResult.setEndTime(new Date());
                        JSONObject reportJson = new JSONObject();
                        reportJson.put("commandResult", commandResult);
                        restUtil.post(commonConfig.getServerUrl() + "/wgcloud/agent/updateResult", reportJson);
                    } catch (Exception e) {
                        logger.error("Error processing a command task: {}", commandJson.toString(), e);
                        if (commandResult != null) {
                            commandResult.setStatus("FAILED");
                            commandResult.setStderr("Agent failed to process task: " + e.getMessage());
                            commandResult.setEndTime(new Date());
                            JSONObject reportJson = new JSONObject();
                            reportJson.put("commandResult", commandResult);
                            restUtil.post(commonConfig.getServerUrl() + "/wgcloud/agent/updateResult", reportJson);
                        }
                    }
                });
                }
        } catch (Exception e) {
            logger.error("Command task executor (fetcher) error", e);
        }
    }

}