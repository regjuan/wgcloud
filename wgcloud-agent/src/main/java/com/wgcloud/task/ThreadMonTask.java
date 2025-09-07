package com.wgcloud.task;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wgcloud.CommonConfig;
import com.wgcloud.RestUtil;
import com.wgcloud.entity.ThreadMon;
import com.wgcloud.entity.ThreadState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreadMonTask {

    private static final Logger logger = LoggerFactory.getLogger(ThreadMonTask.class);

    private final RestUtil restUtil;
    private final CommonConfig commonConfig;

    public ThreadMonTask(RestUtil restUtil, CommonConfig commonConfig) {
        this.restUtil = restUtil;
        this.commonConfig = commonConfig;
    }

    public void syncThreadMonTasks() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("hostname", commonConfig.getBindIp());
            String url = commonConfig.getServerUrl() + "/wgcloud/threadMon/agentTasksByHost";
            String result = restUtil.post(url, JSONUtil.parseObj(params));
            if (result == null) {
                logger.error("Failed to get thread mon tasks from server, result is null");
                return;
            }

            JSONObject ajaxResult = JSONUtil.parseObj(result);
            if (ajaxResult.getInt("code") != 200) {
                logger.error("Failed to get thread mon tasks from server: {}", ajaxResult.getStr("msg"));
                return;
            }

            List<ThreadMon> serverTasks = JSONUtil.toList(JSONUtil.parseArray(ajaxResult.getStr("data")), ThreadMon.class);

            if (serverTasks.isEmpty()) {
                return;
            }

            for (ThreadMon task : serverTasks) {
                if ("1".equals(task.getActive())) {
                    ThreadState threadState = collectThreadState(task);
                    if (threadState != null) {
                        BatchData.THREAD_STATE_LIST.add(threadState);
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Error syncing thread mon tasks", e);
        }
    }

    private ThreadState collectThreadState(ThreadMon task) {
        try {
            oshi.SystemInfo si = new oshi.SystemInfo();
            OperatingSystem os = si.getOperatingSystem();
            String processKeyword = task.getProcessKeyword();

            OSProcess targetProcess = null;
            for (OSProcess p : os.getProcesses()) {
                if (p.getCommandLine().contains(processKeyword)) {
                    targetProcess = p;
                    break;
                }
            }

            if (targetProcess != null) {
                logger.info("Found process '{}' with PID {} for task '{}'", targetProcess.getName(), targetProcess.getProcessID(), task.getTaskName());
                ThreadState threadState = new ThreadState();
                threadState.setHostname(commonConfig.getBindIp());
                threadState.setThreadMonId(task.getId());
                threadState.setCreateTime(new Date());
                threadState.setTotalThreads(targetProcess.getThreadCount());

                // OSHI does not provide per-state thread counts in a cross-platform way.
                // Set them to 0 for now. A future implementation could use jstack for Java processes.
                threadState.setRunnableThreads(0);
                threadState.setBlockedThreads(0);
                threadState.setWaitingThreads(0);
                threadState.setTimedWaitingThreads(0);

                return threadState;
            } else {
                logger.warn("Could not find any process matching keyword '{}' for task '{}'", processKeyword, task.getTaskName());
                return null;
            }
        } catch (Exception e) {
            logger.error("Error collecting thread state for task '{}'", task.getTaskName(), e);
            return null;
        }
    }
}
