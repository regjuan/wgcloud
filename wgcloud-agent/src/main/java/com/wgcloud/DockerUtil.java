package com.wgcloud;

import com.wgcloud.entity.DockerContainer;
import com.wgcloud.entity.DockerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @version V2.3
 * @ClassName: DockerUtil
 * @author: wgcloud
 * @date: 2024年01月16日
 * @Description: Docker工具类
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
public class DockerUtil {

    private static Logger logger = LoggerFactory.getLogger(DockerUtil.class);
    private static CommonConfig commonConfig = (CommonConfig) ApplicationContextHelper.getBean(CommonConfig.class);

    /**
     * 检查Docker是否可用
     */
    public static boolean isDockerAvailable() {
        try {
            ProcessBuilder pb = new ProcessBuilder("docker", "version");
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            logger.error("Docker不可用: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取Docker容器列表
     */
    public static List<DockerContainer> getDockerContainers() {
        List<DockerContainer> containers = new ArrayList<>();
        
        if (!isDockerAvailable()) {
            logger.warn("Docker服务不可用，跳过容器监控");
            return containers;
        }

        try {
            // 获取所有容器（包括停止的）
            ProcessBuilder pb = new ProcessBuilder("docker", "ps", "-a", 
                "--format", "table {{.ID}}\t{{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}\t{{.Command}}\t{{.CreatedAt}}\t{{.Size}}");
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // 跳过表头
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                DockerContainer container = parseContainerLine(line);
                if (container != null) {
                    containers.add(container);
                }
            }
            
            process.waitFor();
            
        } catch (Exception e) {
            logger.error("获取Docker容器列表失败: {}", e.getMessage());
        }
        
        return containers;
    }

    /**
     * 解析容器信息行
     */
    private static DockerContainer parseContainerLine(String line) {
        try {
            String[] parts = line.split("\t");
            if (parts.length < 6) {
                return null;
            }
            
            DockerContainer container = new DockerContainer();
            container.setHostname(commonConfig.getBindIp());
            container.setContainerId(parts[0].trim());
            container.setContainerName(parts[1].trim());
            container.setImage(parts[2].trim());
            container.setStatus(parts[3].trim());
            container.setPorts(parts[4].trim());
            container.setCommand(parts[5].trim());
            
            // 解析创建时间
            if (parts.length > 6) {
                try {
                    String createdAt = parts[6].trim();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    container.setContainerCreateTime(sdf.parse(createdAt));
                } catch (Exception e) {
                    logger.warn("解析容器创建时间失败: {}", e.getMessage());
                }
            }
            
            // 解析容器大小
            if (parts.length > 7) {
                container.setSize(parts[7].trim());
            }
            
            container.setCreateTime(new Date());
            
            return container;
            
        } catch (Exception e) {
            logger.error("解析容器信息失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取Docker容器状态信息
     */
    public static List<DockerState> getDockerStates() {
        List<DockerState> states = new ArrayList<>();
        
        if (!isDockerAvailable()) {
            return states;
        }

        try {
            // 获取运行中的容器统计信息
            ProcessBuilder pb = new ProcessBuilder("docker", "stats", "--no-stream", "--format", 
                "table {{.Container}}\t{{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}\t{{.NetIO}}\t{{.BlockIO}}\t{{.PIDs}}");
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // 跳过表头
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                DockerState state = parseStateLine(line);
                if (state != null) {
                    states.add(state);
                }
            }
            
            process.waitFor();
            
        } catch (Exception e) {
            logger.error("获取Docker容器状态失败: {}", e.getMessage());
        }
        
        return states;
    }

    /**
     * 解析容器状态行
     */
    private static DockerState parseStateLine(String line) {
        try {
            String[] parts = line.split("\t");
            if (parts.length < 8) {
                return null;
            }
            
            DockerState state = new DockerState();
            state.setHostname(commonConfig.getBindIp());
            state.setContainerId(parts[0].trim());
            state.setContainerName(parts[1].trim());
            
            // CPU使用率
            String cpuPerc = parts[2].trim().replace("%", "");
            if (!cpuPerc.isEmpty() && !cpuPerc.equals("--")) {
                state.setCpuPer(Double.parseDouble(cpuPerc));
            }
            
            // 内存使用情况
            String memUsage = parts[3].trim();
            parseMemoryUsage(memUsage, state);
            
            // 内存使用率
            String memPerc = parts[4].trim().replace("%", "");
            if (!memPerc.isEmpty() && !memPerc.equals("--")) {
                state.setMemPer(Double.parseDouble(memPerc));
            }
            
            // 网络IO
            String netIO = parts[5].trim();
            parseNetworkIO(netIO, state);
            
            // 磁盘IO
            String blockIO = parts[6].trim();
            parseBlockIO(blockIO, state);
            
            // 进程数量
            String pids = parts[7].trim();
            if (!pids.isEmpty() && !pids.equals("--")) {
                state.setProcessCount(Integer.parseInt(pids));
            }
            
            state.setStatus("running");
            state.setCreateTime(new Date());
            
            return state;
            
        } catch (Exception e) {
            logger.error("解析容器状态失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解析内存使用情况
     */
    private static void parseMemoryUsage(String memUsage, DockerState state) {
        try {
            if (memUsage.contains("/")) {
                String[] parts = memUsage.split("/");
                if (parts.length == 2) {
                    // 解析使用量
                    double usage = parseMemorySize(parts[0].trim());
                    state.setMemUsage(usage);
                    
                    // 解析限制
                    double limit = parseMemorySize(parts[1].trim());
                    state.setMemLimit(limit);
                }
            }
        } catch (Exception e) {
            logger.warn("解析内存使用情况失败: {}", e.getMessage());
        }
    }

    /**
     * 解析内存大小（支持KB、MB、GB等单位）
     */
    private static double parseMemorySize(String sizeStr) {
        if (sizeStr.isEmpty()) return 0.0;
        
        String upperStr = sizeStr.toUpperCase();
        double value = 0.0;
        
        if (upperStr.contains("GB") || upperStr.contains("GIB")) {
            value = Double.parseDouble(upperStr.replaceAll("[^0-9.]", "")) * 1024;
        } else if (upperStr.contains("MB") || upperStr.contains("MIB")) {
            value = Double.parseDouble(upperStr.replaceAll("[^0-9.]", ""));
        } else if (upperStr.contains("KB") || upperStr.contains("KIB")) {
            value = Double.parseDouble(upperStr.replaceAll("[^0-9.]", "")) / 1024;
        } else {
            // 默认为字节，转换为MB
            value = Double.parseDouble(upperStr.replaceAll("[^0-9.]", "")) / 1024 / 1024;
        }
        
        return value;
    }

    /**
     * 解析网络IO
     */
    private static void parseNetworkIO(String netIO, DockerState state) {
        try {
            if (netIO.contains("/")) {
                String[] parts = netIO.split("/");
                if (parts.length == 2) {
                    // 输入流量
                    long input = parseIOSize(parts[0].trim());
                    state.setNetInput(input);
                    
                    // 输出流量
                    long output = parseIOSize(parts[1].trim());
                    state.setNetOutput(output);
                }
            }
        } catch (Exception e) {
            logger.warn("解析网络IO失败: {}", e.getMessage());
        }
    }

    /**
     * 解析磁盘IO
     */
    private static void parseBlockIO(String blockIO, DockerState state) {
        try {
            if (blockIO.contains("/")) {
                String[] parts = blockIO.split("/");
                if (parts.length == 2) {
                    // 磁盘读取
                    long read = parseIOSize(parts[0].trim());
                    state.setDiskRead(read);
                    
                    // 磁盘写入
                    long write = parseIOSize(parts[1].trim());
                    state.setDiskWrite(write);
                }
            }
        } catch (Exception e) {
            logger.warn("解析磁盘IO失败: {}", e.getMessage());
        }
    }

    /**
     * 解析IO大小（支持KB、MB、GB等单位）
     */
    private static long parseIOSize(String sizeStr) {
        if (sizeStr.isEmpty()) return 0L;
        
        String upperStr = sizeStr.toUpperCase();
        long value = 0L;
        
        if (upperStr.contains("GB") || upperStr.contains("GIB")) {
            value = (long) (Double.parseDouble(upperStr.replaceAll("[^0-9.]", "")) * 1024 * 1024 * 1024);
        } else if (upperStr.contains("MB") || upperStr.contains("MIB")) {
            value = (long) (Double.parseDouble(upperStr.replaceAll("[^0-9.]", "")) * 1024 * 1024);
        } else if (upperStr.contains("KB") || upperStr.contains("KIB")) {
            value = (long) (Double.parseDouble(upperStr.replaceAll("[^0-9.]", "")) * 1024);
        } else {
            // 默认为字节
            value = (long) Double.parseDouble(upperStr.replaceAll("[^0-9.]", ""));
        }
        
        return value;
    }

    /**
     * 启动容器
     */
    public static boolean startContainer(String containerId) {
        return executeDockerCommand("start", containerId);
    }

    /**
     * 停止容器
     */
    public static boolean stopContainer(String containerId) {
        return executeDockerCommand("stop", containerId);
    }

    /**
     * 重启容器
     */
    public static boolean restartContainer(String containerId) {
        return executeDockerCommand("restart", containerId);
    }

    /**
     * 执行Docker命令
     */
    private static boolean executeDockerCommand(String command, String containerId) {
        try {
            ProcessBuilder pb = new ProcessBuilder("docker", command, containerId);
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                logger.info("Docker命令执行成功: {} {}", command, containerId);
                return true;
            } else {
                logger.error("Docker命令执行失败: {} {}, 退出码: {}", command, containerId, exitCode);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("执行Docker命令失败: {} {}, 错误: {}", command, containerId, e.getMessage());
            return false;
        }
    }

    /**
     * 获取容器详细信息
     */
    public static DockerContainer getContainerDetails(String containerId) {
        try {
            ProcessBuilder pb = new ProcessBuilder("docker", "inspect", containerId);
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            
            process.waitFor();
            
            // 这里可以使用JSON解析库来解析详细信息
            // 由于要保持简单，这里返回基本的容器信息
            List<DockerContainer> containers = getDockerContainers();
            for (DockerContainer container : containers) {
                if (container.getContainerId().equals(containerId)) {
                    return container;
                }
            }
            
        } catch (Exception e) {
            logger.error("获取容器详细信息失败: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * 获取容器日志
     */
    public static String getContainerLogs(String containerId, int tailLines) {
        try {
            ProcessBuilder pb = new ProcessBuilder("docker", "logs", "--tail", String.valueOf(tailLines), containerId);
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder logs = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                logs.append(line).append("\n");
            }
            
            process.waitFor();
            return logs.toString();
            
        } catch (Exception e) {
            logger.error("获取容器日志失败: {}", e.getMessage());
            return "获取日志失败: " + e.getMessage();
        }
    }
} 