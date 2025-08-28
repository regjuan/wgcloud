package com.wgcloud.task;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wgcloud.CommonConfig;
import com.wgcloud.RestUtil;
import com.wgcloud.entity.LogMon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LogMonTask {

    private static final Logger logger = LoggerFactory.getLogger(LogMonTask.class);
    private static final Map<String, LogMonThread> RUNNING_THREADS = new ConcurrentHashMap<>();
    private static final Map<String, Thread> THREAD_INSTANCES = new ConcurrentHashMap<>();

    private final RestUtil restUtil;
    private final List<String> agentTags;
    private final CommonConfig commonConfig;

    public LogMonTask(List<String> agentTags, RestUtil restUtil, CommonConfig commonConfig) {
        this.agentTags = agentTags;
        this.restUtil = restUtil;
        this.commonConfig = commonConfig;
    }

    public void syncLogMonTasks() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("tags", agentTags);
            String url = commonConfig.getServerUrl() + "/wgcloud/logMon/agentList";
            String result = restUtil.post(url, JSONUtil.parseObj(params));
            if (result == null) {
                logger.error("Failed to get log mon tasks from server, result is null");
                return;
            }
            JSONObject ajaxResult = JSONUtil.parseObj(result);
            if (ajaxResult.getInt("code") != 200) {
                logger.error("Failed to get log mon tasks from server: {}", ajaxResult.getStr("msg"));
                return;
            }
            List<LogMon> serverTasks = JSONUtil.toList(JSONUtil.parseArray(ajaxResult.getStr("data")), LogMon.class);
            Map<String, LogMon> serverTaskMap = serverTasks.stream().collect(Collectors.toMap(LogMon::getId, task -> task));

            for (String taskId : RUNNING_THREADS.keySet()) {
                if (!serverTaskMap.containsKey(taskId)) {
                    logger.info("Stopping LogMonThread for task: {}", taskId);
                    RUNNING_THREADS.get(taskId).stop();
                    THREAD_INSTANCES.get(taskId).interrupt();
                    RUNNING_THREADS.remove(taskId);
                    THREAD_INSTANCES.remove(taskId);
                }
            }

            for (LogMon task : serverTasks) {
                if (!RUNNING_THREADS.containsKey(task.getId())) {
                    logger.info("Starting LogMonThread for task: {}", task.getAppName());
                    LogMonThread logMonThread = new LogMonThread(task, commonConfig.getBindIp());
                    Thread thread = new Thread(logMonThread);
                    thread.setDaemon(true);
                    thread.start();
                    RUNNING_THREADS.put(task.getId(), logMonThread);
                    THREAD_INSTANCES.put(task.getId(), thread);
                }
            }

        } catch (Exception e) {
            logger.error("Error syncing log mon tasks", e);
        }
    }
}
