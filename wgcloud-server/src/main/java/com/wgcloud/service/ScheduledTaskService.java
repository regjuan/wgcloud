package com.wgcloud.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.ScheduledTask;
import com.wgcloud.entity.ScheduledTaskRecord;
import com.wgcloud.mapper.ScheduledTaskMapper;
import com.wgcloud.mapper.ScheduledTaskRecordMapper;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.UUIDUtil;
import com.wgcloud.util.staticvar.StaticKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:ScheduledTaskService.java
 * @author: http://www.wgstart.com
 * @date: 2024年01月16日
 * @Description: 计划任务业务逻辑服务
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Service
public class ScheduledTaskService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskService.class);

    @Autowired
    private ScheduledTaskMapper scheduledTaskMapper;

    @Autowired
    private ScheduledTaskRecordMapper scheduledTaskRecordMapper;

    @Autowired
    private LogInfoService logInfoService;

    /**
     * 分页查询计划任务
     */
    public PageInfo<ScheduledTask> selectByParams(Map<String, Object> params, int currPage, int pageSize) throws Exception {
        PageHelper.startPage(currPage, pageSize);
        List<ScheduledTask> list = scheduledTaskMapper.selectByParams(params);
        PageInfo<ScheduledTask> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    /**
     * 保存计划任务
     */
    @Transactional
    public void save(ScheduledTask scheduledTask) throws Exception {
        try {
            scheduledTask.setId(UUIDUtil.getUUID());
            scheduledTask.setCreateTime(DateUtil.getNowTime());
            scheduledTask.setUpdateTime(DateUtil.getNowTime());
            
            // 计算下次执行时间
            if (scheduledTask.getCronExpression() != null && !scheduledTask.getCronExpression().isEmpty()) {
                Date nextExecuteTime = calculateNextExecuteTime(scheduledTask.getCronExpression());
                scheduledTask.setNextExecuteTime(nextExecuteTime);
            }
            
            scheduledTaskMapper.save(scheduledTask);
            logInfoService.save("ScheduledTask", "创建计划任务成功：" + scheduledTask.getTaskName(), StaticKeys.LOG_INFO);
        } catch (Exception e) {
            logger.error("保存计划任务失败", e);
            logInfoService.save("ScheduledTask", "创建计划任务失败：" + e.getMessage(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 更新计划任务
     */
    @Transactional
    public void updateById(ScheduledTask scheduledTask) throws Exception {
        try {
            scheduledTask.setUpdateTime(DateUtil.getNowTime());
            
            // 重新计算下次执行时间
            if (scheduledTask.getCronExpression() != null && !scheduledTask.getCronExpression().isEmpty()) {
                Date nextExecuteTime = calculateNextExecuteTime(scheduledTask.getCronExpression());
                scheduledTask.setNextExecuteTime(nextExecuteTime);
            }
            
            scheduledTaskMapper.updateById(scheduledTask);
            logInfoService.save("ScheduledTask", "更新计划任务成功：" + scheduledTask.getTaskName(), StaticKeys.LOG_INFO);
        } catch (Exception e) {
            logger.error("更新计划任务失败", e);
            logInfoService.save("ScheduledTask", "更新计划任务失败：" + e.getMessage(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 根据ID删除计划任务
     */
    @Transactional
    public int deleteById(String[] ids) throws Exception {
        try {
            // 同时删除相关的执行记录
            for (String id : ids) {
                scheduledTaskRecordMapper.deleteByTaskId(id);
            }
            int result = scheduledTaskMapper.deleteById(ids);
            logInfoService.save("ScheduledTask", "删除计划任务成功，数量：" + result, StaticKeys.LOG_INFO);
            return result;
        } catch (Exception e) {
            logger.error("删除计划任务失败", e);
            logInfoService.save("ScheduledTask", "删除计划任务失败：" + e.getMessage(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 根据ID查询计划任务
     */
    public ScheduledTask selectById(String id) throws Exception {
        return scheduledTaskMapper.selectById(id);
    }

    /**
     * 查询所有启用的计划任务
     */
    public List<ScheduledTask> selectAllEnabled() throws Exception {
        return scheduledTaskMapper.selectAllEnabled();
    }

    /**
     * 启用/禁用计划任务
     */
    @Transactional
    public void updateEnabled(String id, Boolean enabled) throws Exception {
        try {
            scheduledTaskMapper.updateEnabled(id, enabled);
            String action = enabled ? "启用" : "禁用";
            logInfoService.save("ScheduledTask", action + "计划任务成功，ID：" + id, StaticKeys.LOG_INFO);
        } catch (Exception e) {
            logger.error("更新计划任务状态失败", e);
            logInfoService.save("ScheduledTask", "更新计划任务状态失败：" + e.getMessage(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 立即执行计划任务
     */
    @Transactional
    public ScheduledTaskRecord executeImmediately(String taskId, String triggerBy) throws Exception {
        try {
            ScheduledTask task = scheduledTaskMapper.selectById(taskId);
            if (task == null) {
                throw new Exception("计划任务不存在");
            }

            // 创建执行记录
            ScheduledTaskRecord record = new ScheduledTaskRecord();
            record.setId(UUIDUtil.getUUID());
            record.setTaskId(taskId);
            record.setTaskName(task.getTaskName());
            record.setHostname(task.getHostname());
            record.setCommand(task.getCommand());
            record.setExecuteTime(new Date());
            record.setStatus("pending");
            record.setManualTrigger(true);
            record.setTriggerBy(triggerBy);
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());

            scheduledTaskRecordMapper.save(record);
            
            // 更新任务状态
            scheduledTaskMapper.updateStatus(taskId, "pending");
            
            logInfoService.save("ScheduledTask", "立即执行计划任务：" + task.getTaskName() + "，触发人：" + triggerBy, StaticKeys.LOG_INFO);
            return record;
        } catch (Exception e) {
            logger.error("立即执行计划任务失败", e);
            logInfoService.save("ScheduledTask", "立即执行计划任务失败：" + e.getMessage(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 查询需要执行的计划任务
     */
    public List<ScheduledTask> selectTasksToExecute() throws Exception {
        Date currentTime = new Date();
        return scheduledTaskMapper.selectTasksToExecute(currentTime);
    }

    /**
     * 更新任务执行信息
     */
    @Transactional
    public void updateExecutionInfo(String taskId, String status, String result, String errorMessage, Long executionTime) throws Exception {
        try {
            Date now = new Date();
            Date nextExecuteTime = null;
            
            // 如果执行成功，计算下次执行时间
            if ("success".equals(status)) {
                ScheduledTask task = scheduledTaskMapper.selectById(taskId);
                if (task != null && task.getCronExpression() != null) {
                    nextExecuteTime = calculateNextExecuteTime(task.getCronExpression());
                }
                // 重置重试次数
                scheduledTaskMapper.resetRetryCount(taskId);
            } else if ("failed".equals(status)) {
                // 增加重试次数
                scheduledTaskMapper.incrementRetryCount(taskId);
            }
            
            scheduledTaskMapper.updateExecutionInfo(taskId, now, status, result, errorMessage, executionTime, nextExecuteTime);
            
        } catch (Exception e) {
            logger.error("更新任务执行信息失败", e);
            throw e;
        }
    }

    /**
     * 根据主机名查询计划任务
     */
    public List<ScheduledTask> selectByHostname(String hostname) throws Exception {
        return scheduledTaskMapper.selectByHostname(hostname);
    }

    /**
     * 获取任务统计信息
     */
    public Map<String, Object> getTaskStatistics() throws Exception {
        return scheduledTaskMapper.getTaskStatistics();
    }

    /**
     * 校验Cron表达式
     */
    public boolean validateCronExpression(String cronExpression) {
        try {
            CronExpression.parse(cronExpression);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 计算下次执行时间
     */
    private Date calculateNextExecuteTime(String cronExpression) {
        try {
            CronExpression cron = CronExpression.parse(cronExpression);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime next = cron.next(now);
            if (next != null) {
                return Date.from(next.atZone(ZoneId.systemDefault()).toInstant());
            }
        } catch (Exception e) {
            logger.error("计算下次执行时间失败", e);
        }
        return null;
    }

    /**
     * 根据任务名称查询计划任务
     */
    public ScheduledTask selectByTaskName(String taskName) throws Exception {
        return scheduledTaskMapper.selectByTaskName(taskName);
    }

    /**
     * 统计任务数量
     */
    public int countByParams(Map<String, Object> params) throws Exception {
        return scheduledTaskMapper.countByParams(params);
    }

    /**
     * 查询所有计划任务
     */
    public List<ScheduledTask> selectAll() throws Exception {
        return scheduledTaskMapper.selectAll();
    }

    /**
     * 根据创建人查询计划任务
     */
    public List<ScheduledTask> selectByCreator(String creator) throws Exception {
        return scheduledTaskMapper.selectByCreator(creator);
    }

    /**
     * 根据状态查询计划任务
     */
    public List<ScheduledTask> selectByStatus(String status) throws Exception {
        return scheduledTaskMapper.selectByStatus(status);
    }

    /**
     * 根据标签查询计划任务
     */
    public List<ScheduledTask> selectByTags(String tags) throws Exception {
        return scheduledTaskMapper.selectByTags(tags);
    }
} 