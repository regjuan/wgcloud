package com.wgcloud.util;

import com.wgcloud.entity.ContainerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version v3.3
 * @ClassName:DockerUtil.java
 * @author: http://www.wgstart.com
 * @date: 2025年9月8日
 * @Description: Docker工具类
 * @Copyright: 2017-2025 wgcloud. All rights reserved.
 */
public class DockerUtil {

    private static final Logger logger = LoggerFactory.getLogger(DockerUtil.class);

    private static final String DOCKER_SOCK = "/var/run/docker.sock";

    /**
     * 检查Docker环境是否可用
     * @return true if docker is available
     */
    public static boolean isDockerOk() {
        File dockerSock = new File(DOCKER_SOCK);
        return dockerSock.exists();
    }

    /**
     * 获取所有容器信息
     * @return List of ContainerInfo
     */
    public static List<ContainerInfo> getContainers() {
        List<ContainerInfo> containerList = new ArrayList<>();
        if (!isDockerOk()) {
            logger.debug("Docker is not available on this host.");
            return containerList;
        }

        try {
            // 1. 获取docker stats的性能数据
            Map<String, ContainerInfo> statsMap = getDockerStats();

            // 2. 获取docker ps的元数据并与性能数据合并
            ProcessBuilder psBuilder = new ProcessBuilder("docker", "ps", "-a", "--format", "{{.ID}}\t{{.Names}}\t{{.Image}}\t{{.State}}\t{{.Status}}");
            Process psProcess = psBuilder.start();
            BufferedReader psReader = new BufferedReader(new InputStreamReader(psProcess.getInputStream()));
            String psLine;
            while ((psLine = psReader.readLine()) != null) {
                String[] parts = psLine.split("\t");
                if (parts.length < 5) continue;

                String containerId = parts[0];
                String names = parts[1];
                String image = parts[2];
                String state = parts[3];
                String status = parts[4]; // e.g., "Up 2 hours"

                ContainerInfo containerInfo = statsMap.getOrDefault(names, new ContainerInfo());
                containerInfo.setContainerId(containerId);
                containerInfo.setNames(names);
                containerInfo.setImage(image);
                containerInfo.setState(state);
                containerInfo.setUptime(status);

                containerList.add(containerInfo);
            }
            psProcess.waitFor();

        } catch (Exception e) {
            logger.error("Failed to get container info from docker", e);
        }

        return containerList;
    }

    /**
     * 执行docker stats命令获取性能数据
     * @return Map of container name to ContainerInfo with performance data
     * @throws Exception
     */
    private static Map<String, ContainerInfo> getDockerStats() throws Exception {
        Map<String, ContainerInfo> statsMap = new HashMap<>();
        ProcessBuilder statsBuilder = new ProcessBuilder("docker", "stats", "--no-stream", "--format", "{{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}");
        Process statsProcess = statsBuilder.start();
        BufferedReader statsReader = new BufferedReader(new InputStreamReader(statsProcess.getInputStream()));
        String statsLine;
        while ((statsLine = statsReader.readLine()) != null) {
            String[] parts = statsLine.split("\t");
            if (parts.length < 4) continue;

            String name = parts[0];
            String cpuPercStr = parts[1].replace("%", "");
            String memUsage = parts[2];
            String memPercStr = parts[3].replace("%", "");

            ContainerInfo containerInfo = new ContainerInfo();
            try {
                containerInfo.setCpuPer(Double.parseDouble(cpuPercStr));
                containerInfo.setMemPer(Double.parseDouble(memPercStr));
                containerInfo.setMemCache(memUsage);
                statsMap.put(name, containerInfo);
            } catch (NumberFormatException e) {
                logger.error("Failed to parse docker stats for container: {}", name, e);
            }
        }
        statsProcess.waitFor();
        return statsMap;
    }
}
