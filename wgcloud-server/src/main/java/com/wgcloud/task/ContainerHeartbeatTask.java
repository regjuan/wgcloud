package com.wgcloud.task;

import com.wgcloud.entity.AlarmInfo;
import com.wgcloud.entity.ContainerInfo;
import com.wgcloud.service.AlarmInfoService;
import com.wgcloud.service.ContainerInfoService;
import com.wgcloud.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version v3.3
 * @ClassName:ContainerHeartbeatTask.java
 * @author: http://www.wgstart.com
 * @date: 2025年9月8日
 * @Description: 容器心跳检测定时任务
 * @Copyright: 2017-2025 wgcloud. All rights reserved.
 */
@Component
public class ContainerHeartbeatTask {

    private static final Logger logger = LoggerFactory.getLogger(ContainerHeartbeatTask.class);

    @Autowired
    private ContainerInfoService containerInfoService;
    @Autowired
    private AlarmInfoService alarmInfoService;

    /**
     * 容器心跳检测，每5分钟执行一次
     */
    public void checkContainerHeartbeat() {
        try {
            Map<String, Object> params = new HashMap<>();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -15);
            params.put("lastHeartbeat", DateUtil.getDateTimeString(calendar.getTime()));
            List<ContainerInfo> staleContainers = containerInfoService.selectStaleByParams(params);

            if (staleContainers.isEmpty()) {
                return;
            }

            for (ContainerInfo container : staleContainers) {
                AlarmInfo alarmInfo = new AlarmInfo();
                alarmInfo.setHostname(container.getHostName());
                alarmInfo.setLogTitle("容器心跳丢失");
                alarmInfo.setInfoContent("容器 '" + container.getNames() + "' (ID: " + container.getContainerId() + ") 在主机 '" + container.getHostName() + "' 上已超过15分钟未上报数据");
                alarmInfo.setSource("container_heartbeat");
                alarmInfo.setState("1"); // 1 for active alarm
                alarmInfoService.save(alarmInfo);
            }

            // 批量更新已告警的容器状态，防止重复告警
            containerInfoService.updateAlertState(staleContainers);

        } catch (Exception e) {
            logger.error("执行容器心跳检测任务失败", e);
        }
    }
}
