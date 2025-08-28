package com.wgcloud.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.LogInfo;
import com.wgcloud.mapper.LogInfoMapper;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class LogInfoService {

    private static final Logger logger = LoggerFactory.getLogger(LogInfoService.class);

    @Autowired
    private LogInfoMapper logInfoMapper;

    public PageInfo selectByParams(Map<String, Object> params, int currPage, int pageSize) throws Exception {
        PageHelper.startPage(currPage, pageSize);
        List<LogInfo> list = logInfoMapper.selectByParams(params);
        PageInfo<LogInfo> pageInfo = new PageInfo<LogInfo>(list);
        return pageInfo;
    }

    public void saveRecord(List<LogInfo> recordList) throws Exception {
        if (recordList.size() < 1) {
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        for (LogInfo as : recordList) {
            as.setId(UUIDUtil.getUUID());
            as.setInfoTitle("日志监控");
        }
        logInfoMapper.insertList(recordList);
    }

    public void save(LogInfo logInfo) {
        try {
            logInfoMapper.save(logInfo);
        } catch (Exception e) {
            logger.error("保存日志信息异常：", e);
        }
    }

    public void save(String hostname, String infoContent, String state) {
        this.save(hostname, infoContent, state, null);
    }

    public void save(String hostname, String infoContent, String state, String logMonId) {
        LogInfo logInfo = new LogInfo();
        logInfo.setHostname(hostname);
        logInfo.setInfoContent(infoContent);
        logInfo.setState(state);
        logInfo.setLogMonId(logMonId);
        logInfo.setId(UUIDUtil.getUUID());
        logInfo.setCreateTime(DateUtil.getNowTime());
        this.save(logInfo);
    }

    public int countByParams(Map<String, Object> params) throws Exception {
        return logInfoMapper.countByParams(params);
    }

    public int deleteById(String[] id) throws Exception {
        return logInfoMapper.deleteById(id);
    }

    public LogInfo selectById(String id) throws Exception {
        return logInfoMapper.selectById(id);
    }

    public List<LogInfo> selectAllByParams(Map<String, Object> params) throws Exception {
        return logInfoMapper.selectAllByParams(params);
    }


}
