package com.wgcloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @version V2.3
 * @ClassName: CommandExecutor
 * @author: wgcloud
 * @date: 2024年01月16日
 * @Description: 指令执行工具类
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
public class CommandExecutor {

    private static final Logger logger = LoggerFactory.getLogger(CommandExecutor.class);
    
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    private static final boolean IS_WINDOWS = OS_NAME.contains("win");

    /**
     * 执行指令结果
     */
    public static class ExecutionResult {
        private boolean success;
        private int exitCode;
        private String output;
        private String errorOutput;
        private long executionTime;
        private String errorMessage;

        public ExecutionResult() {}

        public ExecutionResult(boolean success, int exitCode, String output, String errorOutput, long executionTime) {
            this.success = success;
            this.exitCode = exitCode;
            this.output = output;
            this.errorOutput = errorOutput;
            this.executionTime = executionTime;
        }

        // Getters and Setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public int getExitCode() {
            return exitCode;
        }

        public void setExitCode(int exitCode) {
            this.exitCode = exitCode;
        }

        public String getOutput() {
            return output;
        }

        public void setOutput(String output) {
            this.output = output;
        }

        public String getErrorOutput() {
            return errorOutput;
        }

        public void setErrorOutput(String errorOutput) {
            this.errorOutput = errorOutput;
        }

        public long getExecutionTime() {
            return executionTime;
        }

        public void setExecutionTime(long executionTime) {
            this.executionTime = executionTime;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    /**
     * 执行单个指令
     * @param command 要执行的指令
     * @param timeoutSeconds 超时时间（秒）
     * @return 执行结果
     */
    public static ExecutionResult executeCommand(String command, int timeoutSeconds) {
        logger.info("开始执行指令: {}, 超时时间: {}秒", command, timeoutSeconds);
        
        long startTime = System.currentTimeMillis();
        ExecutionResult result = new ExecutionResult();
        
        try {
            ProcessBuilder processBuilder = buildProcessBuilder(command);
            
            // 设置工作目录
            processBuilder.directory(new java.io.File(System.getProperty("user.home")));
            
            Process process = processBuilder.start();
            
            // 读取输出流
            StringBuilder output = new StringBuilder();
            StringBuilder errorOutput = new StringBuilder();
            
            // 启动读取线程
            Thread outputReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), "UTF-8"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                } catch (IOException e) {
                    logger.error("读取输出流失败: {}", e.getMessage());
                }
            });
            
            Thread errorReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream(), "UTF-8"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorOutput.append(line).append("\n");
                    }
                } catch (IOException e) {
                    logger.error("读取错误流失败: {}", e.getMessage());
                }
            });
            
            outputReader.start();
            errorReader.start();
            
            // 等待进程完成或超时
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            
            if (!finished) {
                // 超时，强制结束进程
                process.destroyForcibly();
                result.setSuccess(false);
                result.setExitCode(-1);
                result.setErrorMessage("指令执行超时（" + timeoutSeconds + "秒）");
                logger.warn("指令执行超时: {}", command);
            } else {
                // 等待读取线程完成
                outputReader.join(1000);
                errorReader.join(1000);
                
                int exitCode = process.exitValue();
                result.setExitCode(exitCode);
                result.setSuccess(exitCode == 0);
                result.setOutput(output.toString());
                result.setErrorOutput(errorOutput.toString());
                
                if (exitCode != 0) {
                    result.setErrorMessage("指令执行失败，退出码: " + exitCode);
                }
                
                logger.info("指令执行完成，退出码: {}, 耗时: {}ms", 
                           exitCode, System.currentTimeMillis() - startTime);
            }
            
        } catch (Exception e) {
            logger.error("指令执行异常: {}", e.getMessage(), e);
            result.setSuccess(false);
            result.setExitCode(-1);
            result.setErrorMessage("指令执行异常: " + e.getMessage());
        }
        
        result.setExecutionTime(System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * 执行多个指令（按行分割）
     * @param commands 指令内容（多行）
     * @param timeoutSeconds 每个指令的超时时间（秒）
     * @return 执行结果
     */
    public static ExecutionResult executeCommands(String commands, int timeoutSeconds) {
        logger.info("开始执行多行指令，超时时间: {}秒", timeoutSeconds);
        
        long startTime = System.currentTimeMillis();
        StringBuilder allOutput = new StringBuilder();
        StringBuilder allErrorOutput = new StringBuilder();
        boolean allSuccess = true;
        int lastExitCode = 0;
        
        // 按行分割指令
        String[] commandLines = commands.split("\n");
        
        for (int i = 0; i < commandLines.length; i++) {
            String command = commandLines[i].trim();
            
            // 跳过空行和注释行
            if (command.isEmpty() || command.startsWith("#")) {
                continue;
            }
            
            logger.info("执行第{}个指令: {}", i + 1, command);
            
            ExecutionResult result = executeCommand(command, timeoutSeconds);
            
            allOutput.append("=== 指令 ").append(i + 1).append(": ").append(command).append(" ===\n");
            allOutput.append(result.getOutput());
            allOutput.append("\n");
            
            if (!result.getErrorOutput().isEmpty()) {
                allErrorOutput.append("=== 指令 ").append(i + 1).append(" 错误输出 ===\n");
                allErrorOutput.append(result.getErrorOutput());
                allErrorOutput.append("\n");
            }
            
            if (!result.isSuccess()) {
                allSuccess = false;
                lastExitCode = result.getExitCode();
                
                // 如果指令失败，记录错误但继续执行下一个指令
                logger.warn("指令执行失败: {}, 退出码: {}", command, result.getExitCode());
            }
        }
        
        ExecutionResult finalResult = new ExecutionResult();
        finalResult.setSuccess(allSuccess);
        finalResult.setExitCode(lastExitCode);
        finalResult.setOutput(allOutput.toString());
        finalResult.setErrorOutput(allErrorOutput.toString());
        finalResult.setExecutionTime(System.currentTimeMillis() - startTime);
        
        if (!allSuccess) {
            finalResult.setErrorMessage("部分指令执行失败");
        }
        
        logger.info("多行指令执行完成，总耗时: {}ms, 成功: {}", 
                   finalResult.getExecutionTime(), allSuccess);
        
        return finalResult;
    }

    /**
     * 构建ProcessBuilder
     * @param command 要执行的指令
     * @return ProcessBuilder
     */
    private static ProcessBuilder buildProcessBuilder(String command) {
        ProcessBuilder processBuilder;
        
        if (IS_WINDOWS) {
            // Windows系统
            processBuilder = new ProcessBuilder("cmd", "/c", command);
        } else {
            // Unix/Linux系统
            processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
        }
        
        // 合并标准输出和错误输出
        processBuilder.redirectErrorStream(false);
        
        return processBuilder;
    }

    /**
     * 验证指令是否安全（简单的安全检查）
     * @param command 要检查的指令
     * @return 是否安全
     */
    public static boolean isCommandSafe(String command) {
        if (command == null || command.trim().isEmpty()) {
            return false;
        }
        
        String cmd = command.toLowerCase().trim();
        
        // 禁止的危险指令
        String[] dangerousCommands = {
            "rm -rf /",
            "format",
            "fdisk",
            "mkfs",
            "dd if=/dev/zero",
            ":(){ :|:& };:",  // fork bomb
            "shutdown",
            "reboot",
            "halt",
            "poweroff"
        };
        
        for (String dangerous : dangerousCommands) {
            if (cmd.contains(dangerous.toLowerCase())) {
                logger.warn("检测到危险指令: {}", command);
                return false;
            }
        }
        
        return true;
    }

    /**
     * 获取操作系统信息
     * @return 操作系统信息
     */
    public static String getOSInfo() {
        return "OS: " + OS_NAME + ", Windows: " + IS_WINDOWS;
    }
} 