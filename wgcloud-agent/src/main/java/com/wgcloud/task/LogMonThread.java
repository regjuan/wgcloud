package com.wgcloud.task;

import com.wgcloud.DateUtil;
import com.wgcloud.UUIDUtil;
import com.wgcloud.entity.LogInfo;
import com.wgcloud.entity.LogMon;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Properties;

public class LogMonThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(LogMonThread.class);

    private volatile boolean running = true;
    private long lastModified = 0L;
    private long offset = 0L;
    private String currentState = "OK";

    private final LogMon logMon;
    private final String hostname;
    private final File offsetFile;
    private final Properties offsetProps;

    public LogMonThread(LogMon logMon, String hostname) {
        this.logMon = logMon;
        this.hostname = hostname;
        this.offsetFile = new File("log_offsets.properties");
        this.offsetProps = new Properties();
    }

    @Override
    public void run() {
        loadOffset();
        while (running) {
            try {
                File logFile = new File(logMon.getFilePath());
                logger.info("LogMonThread {}", BatchData.LOG_INFO_LIST.size());
                if (!logFile.exists()) {
                    handleStateChange("FILE_NOT_FOUND", "[WGCLOUD-ALERT] level=WARN, type=FILE_NOT_FOUND, msg=\"Log file " + logMon.getFilePath() + " does not exist.\"");
                    logger.info(" LogMonThread {}", BatchData.LOG_INFO_LIST.size());
                    Thread.sleep(30 * 1000);
                    continue;
                }

                if (logFile.lastModified() > lastModified) {
                    lastModified = logFile.lastModified();
                    RandomAccessFile raf = new RandomAccessFile(logFile, "r");
                    // Handle log rotation
                    if (raf.length() < offset) {
                        offset = 0;
                    }
                    raf.seek(offset);

                    String line;
                    boolean matched = false;
                    while ((line = raf.readLine()) != null) {
                        String utf8Line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                        if (matches(utf8Line)) {
                            matched = true;
                            reportLog(utf8Line);
                        }
                    }

                    offset = raf.getFilePointer();
                    raf.close();
                    saveOffset();

                    if (matched) {
                        handleStateChange("OK", null);
                    } else {
                        handleStateChange("NO_NEW_MATCH", "[WGCLOUD-ALERT] level=WARN, type=NO_NEW_MATCH, msg=\"Log file " + logMon.getFilePath() + " has no new match entries.\"");
                    }
                }

            } catch (Exception e) {
                logger.error("Error monitoring log file: " + logMon.getFilePath(), e);
            }
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                running = false;
            }
        }
        logger.info("LogMonThread for {} stopped.", logMon.getAppName());
    }

    private boolean matches(String line) {
        if (StringUtils.isEmpty(logMon.getMatchKeywords())) {
            return true;
        }
        boolean keywordMatch = false;
        for (String keyword : logMon.getMatchKeywords().split(",")) {
            if (line.contains(keyword)) {
                keywordMatch = true;
                break;
            }
        }
        if (!keywordMatch) {
            return false;
        }
        if (!StringUtils.isEmpty(logMon.getUnMatchKeywords())) {
            for (String unMatchKeyword : logMon.getUnMatchKeywords().split(",")) {
                if (line.contains(unMatchKeyword)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void handleStateChange(String newState, String alertMsg) {
        if (!this.currentState.equals(newState)) {
            this.currentState = newState;
            if (alertMsg != null) {
                reportLog(alertMsg);
            }
        }
    }

    private void reportLog(String content) {
        LogInfo logInfo = new LogInfo();
        logInfo.setHostname(this.hostname);
        logInfo.setLogMonId(logMon.getId());
        logInfo.setInfoContent(content);
        logInfo.setState("1");
        logInfo.setId(UUIDUtil.getUUID());
        logInfo.setCreateTime(DateUtil.getNowTime());
        BatchData.LOG_INFO_LIST.add(logInfo);
        logger.info("{}", BatchData.LOG_INFO_LIST.size());
    }

    private void loadOffset() {
        try {
            if (offsetFile.exists()) {
                FileInputStream fis = new FileInputStream(offsetFile);
                offsetProps.load(fis);
                fis.close();
                this.offset = Long.parseLong(offsetProps.getProperty(logMon.getId(), "0"));
            }
        } catch (Exception e) {
            logger.error("Failed to load offset for " + logMon.getId(), e);
            this.offset = 0;
        }
    }

    private void saveOffset() {
        try {
            offsetProps.setProperty(logMon.getId(), String.valueOf(this.offset));
            FileOutputStream fos = new FileOutputStream(offsetFile);
            offsetProps.store(fos, "Log Monitoring Offsets");
            fos.close();
        } catch (Exception e) {
            logger.error("Failed to save offset for " + logMon.getId(), e);
        }
    }

    public void stop() {
        this.running = false;
    }
}
