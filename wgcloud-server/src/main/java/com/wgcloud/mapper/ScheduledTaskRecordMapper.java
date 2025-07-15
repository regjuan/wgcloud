package com.wgcloud.mapper;

import com.wgcloud.entity.ScheduledTaskRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:ScheduledTaskRecordMapper.java
 * @author: http://www.wgstart.com
 * @date: 2024年01月16日
 * @Description: 计划任务执行记录数据访问接口
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Repository
public interface ScheduledTaskRecordMapper {

    /**
     * 根据参数查询执行记录列表
     */
    List<ScheduledTaskRecord> selectByParams(Map<String, Object> params) throws Exception;

    /**
     * 根据参数统计执行记录数量
     */
    int countByParams(Map<String, Object> params) throws Exception;

    /**
     * 根据ID查询执行记录
     */
    ScheduledTaskRecord selectById(@Param("id") String id) throws Exception;

    /**
     * 查询所有执行记录
     */
    List<ScheduledTaskRecord> selectAll() throws Exception;

    /**
     * 保存执行记录
     */
    void save(ScheduledTaskRecord record) throws Exception;

    /**
     * 批量保存执行记录
     */
    void insertBatch(@Param("list") List<ScheduledTaskRecord> list) throws Exception;

    /**
     * 更新执行记录
     */
    void updateById(ScheduledTaskRecord record) throws Exception;

    /**
     * 根据ID删除执行记录
     */
    int deleteById(@Param("ids") String[] ids) throws Exception;

    /**
     * 根据任务ID查询执行记录
     */
    List<ScheduledTaskRecord> selectByTaskId(@Param("taskId") String taskId) throws Exception;

    /**
     * 根据任务ID删除执行记录
     */
    int deleteByTaskId(@Param("taskId") String taskId) throws Exception;

    /**
     * 根据任务ID统计执行记录数量
     */
    int countByTaskId(@Param("taskId") String taskId) throws Exception;

    /**
     * 根据状态查询执行记录
     */
    List<ScheduledTaskRecord> selectByStatus(@Param("status") String status) throws Exception;

    /**
     * 根据主机名查询执行记录
     */
    List<ScheduledTaskRecord> selectByHostname(@Param("hostname") String hostname) throws Exception;

    /**
     * 更新执行状态
     */
    void updateStatus(@Param("id") String id, 
                     @Param("status") String status,
                     @Param("completeTime") java.util.Date completeTime,
                     @Param("result") String result,
                     @Param("errorMessage") String errorMessage,
                     @Param("duration") Long duration,
                     @Param("exitCode") Integer exitCode) throws Exception;

    /**
     * 查询最近的执行记录
     */
    List<ScheduledTaskRecord> selectRecentRecords(@Param("limit") Integer limit) throws Exception;

    /**
     * 根据任务ID查询最新的执行记录
     */
    ScheduledTaskRecord selectLatestByTaskId(@Param("taskId") String taskId) throws Exception;

    /**
     * 根据时间范围查询执行记录
     */
    List<ScheduledTaskRecord> selectByTimeRange(@Param("startTime") java.util.Date startTime,
                                               @Param("endTime") java.util.Date endTime) throws Exception;

    /**
     * 删除过期的执行记录
     */
    int deleteExpiredRecords(@Param("days") int days) throws Exception;

    /**
     * 获取执行统计信息
     */
    Map<String, Object> getExecutionStatistics(@Param("taskId") String taskId) throws Exception;

    /**
     * 根据触发人查询执行记录
     */
    List<ScheduledTaskRecord> selectByTriggerBy(@Param("triggerBy") String triggerBy) throws Exception;

    /**
     * 查询手动触发的执行记录
     */
    List<ScheduledTaskRecord> selectManualTriggerRecords() throws Exception;

    /**
     * 查询失败的执行记录
     */
    List<ScheduledTaskRecord> selectFailedRecords(@Param("retryable") Boolean retryable) throws Exception;

    /**
     * 根据任务名称查询执行记录
     */
    List<ScheduledTaskRecord> selectByTaskName(@Param("taskName") String taskName) throws Exception;

    /**
     * 获取任务执行图表数据
     */
    List<Map<String, Object>> getChartData(@Param("taskId") String taskId, 
                                          @Param("days") Integer days) throws Exception;
} 