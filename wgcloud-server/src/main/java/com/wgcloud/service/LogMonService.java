package com.wgcloud.service;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wgcloud.dto.LogMonitorReportByHostDto;
import com.wgcloud.dto.MonTaskStatusDto;
import com.wgcloud.entity.SystemInfo;
import com.wgcloud.entity.LogInfo;
import com.wgcloud.entity.LogMon;
import com.wgcloud.mapper.LogMonMapper;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LogMonService {

    private static final Logger logger = LoggerFactory.getLogger(LogMonService.class);

    @Resource
    private LogMonMapper logMonMapper;
    @Resource
    private SystemInfoService systemInfoService;
    @Resource
    private TagRelationService tagRelationService;
    @Resource
    private LogInfoService logInfoService;

    public PageInfo<LogMon> selectByParams(Map<String, Object> params, int currPage, int pageSize) throws Exception {
        PageHelper.startPage(currPage, pageSize);
        List<LogMon> list = logMonMapper.selectByParams(params);
        for (LogMon logMon : list) {
            if (!StringUtils.isEmpty(logMon.getTargetTags())) {
                logMon.setTargetTagsList(JSONUtil.toList(logMon.getTargetTags(), String.class));
            }
        }
        return new PageInfo<>(list);
    }

    public LogMon selectById(String id) throws Exception {
        LogMon logMon = logMonMapper.selectById(id);
        if (logMon != null && !StringUtils.isEmpty(logMon.getTargetTags())) {
            logMon.setTargetTagsList(JSONUtil.toList(logMon.getTargetTags(), String.class));
        }
        return logMon;
    }

    public List<LogMon> selectAllByParams(Map<String, Object> params) throws Exception {
        List<LogMon> list = logMonMapper.selectByParams(params);
        for (LogMon logMon : list) {
            if (!StringUtils.isEmpty(logMon.getTargetTags())) {
                logMon.setTargetTagsList(JSONUtil.toList(logMon.getTargetTags(), String.class));
            }
        }
        return list;
    }

    @Transactional
    public void save(LogMon logMon) throws Exception {
        if (logMon.getTargetTagsList() != null) {
            logMon.setTargetTags(JSONUtil.toJsonStr(logMon.getTargetTagsList()));
        }
        logMon.setId(UUIDUtil.getUUID());
        logMon.setCreateTime(DateUtil.getNowTime());
        logMonMapper.save(logMon);
    }

    @Transactional
    public int updateById(LogMon logMon) throws Exception {
        if (logMon.getTargetTagsList() != null) {
            logMon.setTargetTags(JSONUtil.toJsonStr(logMon.getTargetTagsList()));
        }
        return logMonMapper.updateById(logMon);
    }

    public int countByParams(Map<String, Object> params) throws Exception {
        return logMonMapper.countByParams(params);
    }

    public int deleteById(String[] id) throws Exception {
        return logMonMapper.deleteById(id);
    }

    public List<LogMonitorReportByHostDto> getStatusByHost(String startTime, String endTime, String hostname, String tag) throws Exception {
        Map<String, Object> hostParams = new HashMap<>();
        if (!StringUtils.isEmpty(hostname)) {
            hostParams.put("hostname", hostname);
        }
        List<SystemInfo> allHosts = systemInfoService.selectAllByParams(hostParams);
        List<LogMon> allLogMons = selectAllByParams(new HashMap<>());

        Map<String, Object> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        List<LogInfo> recentLogs = logInfoService.selectAllByParams(params);

        Map<String, List<LogInfo>> logsByHostAndMonId = recentLogs.stream()
                .filter(log -> !StringUtils.isEmpty(log.getLogMonId()))
                .collect(Collectors.groupingBy(log -> log.getHostname() + "::" + log.getLogMonId()));

        List<LogMonitorReportByHostDto> resultList = new ArrayList<>();
        for (SystemInfo host : allHosts) {
            LogMonitorReportByHostDto hostStatusDto = new LogMonitorReportByHostDto();
            hostStatusDto.setHostname(host.getHostname());
            List<String> hostTags = tagRelationService.selectTagNamesByHostId(host.getId());
            hostStatusDto.setTags(hostTags);

            if (!StringUtils.isEmpty(tag) && !hostTags.contains(tag)) {
                continue;
            }

            List<MonTaskStatusDto> monTaskStatusDtos = new ArrayList<>();
            String overallStatus = "OK";

            for (LogMon logMon : allLogMons) {
                if (logMon.getTargetTagsList() == null || logMon.getTargetTagsList().isEmpty()) {
                    continue;
                }
                if (hostStatusDto.getTags().stream().anyMatch(hostTag -> logMon.getTargetTagsList().contains(hostTag))) {
                    MonTaskStatusDto taskStatusDto = new MonTaskStatusDto();
                    taskStatusDto.setTaskId(logMon.getId());
                    taskStatusDto.setTaskName(logMon.getAppName());
                    taskStatusDto.setFilePath(logMon.getFilePath());

                    String lookupKey = host.getHostname() + "::" + logMon.getId();
                    List<LogInfo> taskLogs = logsByHostAndMonId.get(lookupKey);

                    if (taskLogs == null || taskLogs.isEmpty()) {
                        taskStatusDto.setStatus("OK");
                        taskStatusDto.setTodayAlerts(0);
                    } else {
                        taskStatusDto.setTodayAlerts(taskLogs.size());
                        LogInfo latestAlert = taskLogs.stream().max(Comparator.comparing(LogInfo::getCreateTime)).get();
                        taskStatusDto.setLastAlertTime(latestAlert.getCreateTime());

                        if (latestAlert.getInfoContent().contains("[WGCLOUD-ALERT]")) {
                            if (latestAlert.getInfoContent().contains("FILE_NOT_FOUND")) {
                                taskStatusDto.setStatus("FILE_NOT_FOUND");
                            } else if (latestAlert.getInfoContent().contains("NO_NEW_MATCH")) {
                                taskStatusDto.setStatus("NO_NEW_MATCH");
                            } else {
                                taskStatusDto.setStatus("ALERT");
                            }
                        } else {
                            taskStatusDto.setStatus("ALERT");
                        }
                    }

                    if (!"OK".equals(taskStatusDto.getStatus())) {
                        overallStatus = "ERROR";
                    }
                    monTaskStatusDtos.add(taskStatusDto);
                }
            }

            hostStatusDto.setOverallStatus(overallStatus);
            hostStatusDto.setMonTasks(monTaskStatusDtos);
            resultList.add(hostStatusDto);
        }

        return resultList;
    }
}
