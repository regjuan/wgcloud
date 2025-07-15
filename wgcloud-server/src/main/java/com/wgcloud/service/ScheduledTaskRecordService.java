package com.wgcloud.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.ScheduledTaskRecord;
import com.wgcloud.mapper.ScheduledTaskRecordMapper;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.UUIDUtil;
import com.wgcloud.util.staticvar.StaticKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:ScheduledTaskRecordService.java
 * @author: http://www.wgstart.com
 * @date: 2024年01月16日
 * @Description: 计划任务执行记录业务逻辑服务
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Service
public class ScheduledTaskRecordService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskRecordService.class);

    @Autowired
    private ScheduledTaskRecordMapper scheduledTaskRecordMapper;

    @Autowired
    private LogInfoService logInfoService;

    /**
     * 分页查询执行记录
     */
    public PageInfo<ScheduledTaskRecord> selectByParams(Map<String, Object> params, int currPage, int pageSize) throws Exception {
        PageHelper.startPage(currPage, pageSize);
        List<ScheduledTaskRecord> list = scheduledTaskRecordMapper.selectByParams(params);
        PageInfo<ScheduledTaskRecord> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    /**
     * 保存执行记录
     */
    @Transactional
    public void save(ScheduledTaskRecord record) throws Exception {
        try {
            record.setId(UUIDUtil.getUUID());
            record.setCreateTime(DateUtil.getNowTime());
            record.setUpdateTime(DateUtil.getNowTime());
            scheduledTaskRecordMapper.save(record);
        } catch (Exception e) {
            logger.error("保存执行记录失败", e);
            throw e;
        }
    }

    /**
     * 更新执行记录
     */
    @Transactional
    public void updateById(ScheduledTaskRecord record) throws Exception {
        try {
            record.setUpdateTime(DateUtil.getNowTime());
            scheduledTaskRecordMapper.updateById(record);
        } catch (Exception e) {
            logger.error("更新执行记录失败", e);
            throw e;
        }
    }

    /**
     * 更新执行状态
     */
    @Transactional
    public void updateStatus(String id, String status, String result, String errorMessage, Long duration, Integer exitCode) throws Exception {
        try {
            Date completeTime = new Date();
            scheduledTaskRecordMapper.updateStatus(id, status, completeTime, result, errorMessage, duration, exitCode);
        } catch (Exception e) {
            logger.error("更新执行状态失败", e);
            throw e;
        }
    }

    /**
     * 根据ID删除执行记录
     */
    @Transactional
    public int deleteById(String[] ids) throws Exception {
        try {
            int result = scheduledTaskRecordMapper.deleteById(ids);
            logInfoService.save("ScheduledTaskRecord", "删除执行记录成功，数量：" + result, StaticKeys.LOG_INFO);
            return result;
        } catch (Exception e) {
            logger.error("删除执行记录失败", e);
            logInfoService.save("ScheduledTaskRecord", "删除执行记录失败：" + e.getMessage(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 根据ID查询执行记录
     */
    public ScheduledTaskRecord selectById(String id) throws Exception {
        return scheduledTaskRecordMapper.selectById(id);
    }

    /**
     * 根据任务ID查询执行记录
     */
    public List<ScheduledTaskRecord> selectByTaskId(String taskId) throws Exception {
        return scheduledTaskRecordMapper.selectByTaskId(taskId);
    }

    /**
     * 根据任务ID查询最新的执行记录
     */
    public ScheduledTaskRecord selectLatestByTaskId(String taskId) throws Exception {
        return scheduledTaskRecordMapper.selectLatestByTaskId(taskId);
    }

    /**
     * 查询最近的执行记录
     */
    public List<ScheduledTaskRecord> selectRecentRecords(Integer limit) throws Exception {
        return scheduledTaskRecordMapper.selectRecentRecords(limit);
    }

    /**
     * 根据状态查询执行记录
     */
    public List<ScheduledTaskRecord> selectByStatus(String status) throws Exception {
        return scheduledTaskRecordMapper.selectByStatus(status);
    }

    /**
     * 根据主机名查询执行记录
     */
    public List<ScheduledTaskRecord> selectByHostname(String hostname) throws Exception {
        return scheduledTaskRecordMapper.selectByHostname(hostname);
    }

    /**
     * 根据触发人查询执行记录
     */
    public List<ScheduledTaskRecord> selectByTriggerBy(String triggerBy) throws Exception {
        return scheduledTaskRecordMapper.selectByTriggerBy(triggerBy);
    }

    /**
     * 查询手动触发的执行记录
     */
    public List<ScheduledTaskRecord> selectManualTriggerRecords() throws Exception {
        return scheduledTaskRecordMapper.selectManualTriggerRecords();
    }

    /**
     * 查询失败的执行记录
     */
    public List<ScheduledTaskRecord> selectFailedRecords(Boolean retryable) throws Exception {
        return scheduledTaskRecordMapper.selectFailedRecords(retryable);
    }

    /**
     * 根据时间范围查询执行记录
     */
    public List<ScheduledTaskRecord> selectByTimeRange(Date startTime, Date endTime) throws Exception {
        return scheduledTaskRecordMapper.selectByTimeRange(startTime, endTime);
    }

    /**
     * 获取执行统计信息
     */
    public Map<String, Object> getExecutionStatistics(String taskId) throws Exception {
        return scheduledTaskRecordMapper.getExecutionStatistics(taskId);
    }

    /**
     * 获取任务执行图表数据
     */
    public List<Map<String, Object>> getChartData(String taskId, Integer days) throws Exception {
        return scheduledTaskRecordMapper.getChartData(taskId, days);
    }

    /**
     * 统计执行记录数量
     */
    public int countByParams(Map<String, Object> params) throws Exception {
        return scheduledTaskRecordMapper.countByParams(params);
    }

    /**
     * 根据任务ID统计执行记录数量
     */
    public int countByTaskId(String taskId) throws Exception {
        return scheduledTaskRecordMapper.countByTaskId(taskId);
    }

    /**
     * 删除过期的执行记录
     */
    @Transactional
    public int deleteExpiredRecords(int days) throws Exception {
        try {
            int result = scheduledTaskRecordMapper.deleteExpiredRecords(days);
            logInfoService.save("ScheduledTaskRecord", "删除过期执行记录成功，数量：" + result, StaticKeys.LOG_INFO);
            return result;
        } catch (Exception e) {
            logger.error("删除过期执行记录失败", e);
            logInfoService.save("ScheduledTaskRecord", "删除过期执行记录失败：" + e.getMessage(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 根据任务名称查询执行记录
     */
    public List<ScheduledTaskRecord> selectByTaskName(String taskName) throws Exception {
        return scheduledTaskRecordMapper.selectByTaskName(taskName);
    }

    /**
     * 批量保存执行记录
     */
    @Transactional
    public void saveRecord(List<ScheduledTaskRecord> recordList) throws Exception {
        if (recordList == null || recordList.isEmpty()) {
            return;
        }
        try {
            for (ScheduledTaskRecord record : recordList) {
                record.setId(UUIDUtil.getUUID());
                record.setCreateTime(DateUtil.getNowTime());
                record.setUpdateTime(DateUtil.getNowTime());
            }
            scheduledTaskRecordMapper.insertBatch(recordList);
        } catch (Exception e) {
            logger.error("批量保存执行记录失败", e);
            throw e;
        }
    }

    /**
     * 查询所有执行记录
     */
    public List<ScheduledTaskRecord> selectAll() throws Exception {
        return scheduledTaskRecordMapper.selectAll();
    }
} 