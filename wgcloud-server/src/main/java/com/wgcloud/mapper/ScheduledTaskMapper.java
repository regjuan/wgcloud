package com.wgcloud.mapper;

import com.wgcloud.entity.ScheduledTask;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:ScheduledTaskMapper.java
 * @author: http://www.wgstart.com
 * @date: 2024年01月16日
 * @Description: 计划任务数据访问接口
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Repository
public interface ScheduledTaskMapper {

    /**
     * 根据参数查询计划任务列表
     */
    List<ScheduledTask> selectByParams(Map<String, Object> params) throws Exception;

    /**
     * 根据参数统计计划任务数量
     */
    int countByParams(Map<String, Object> params) throws Exception;

    /**
     * 根据ID查询计划任务
     */
    ScheduledTask selectById(@Param("id") String id) throws Exception;

    /**
     * 查询所有计划任务
     */
    List<ScheduledTask> selectAll() throws Exception;

    /**
     * 查询所有启用的计划任务
     */
    List<ScheduledTask> selectAllEnabled() throws Exception;

    /**
     * 保存计划任务
     */
    void save(ScheduledTask scheduledTask) throws Exception;

    /**
     * 批量保存计划任务
     */
    void insertBatch(@Param("list") List<ScheduledTask> list) throws Exception;

    /**
     * 更新计划任务
     */
    void updateById(ScheduledTask scheduledTask) throws Exception;

    /**
     * 根据ID删除计划任务
     */
    int deleteById(@Param("ids") String[] ids) throws Exception;

    /**
     * 根据主机名删除计划任务
     */
    int deleteByHostname(@Param("hostname") String hostname) throws Exception;

    /**
     * 根据任务名称查询计划任务
     */
    ScheduledTask selectByTaskName(@Param("taskName") String taskName) throws Exception;

    /**
     * 根据主机名查询计划任务
     */
    List<ScheduledTask> selectByHostname(@Param("hostname") String hostname) throws Exception;

    /**
     * 启用/禁用计划任务
     */
    void updateEnabled(@Param("id") String id, @Param("enabled") Boolean enabled) throws Exception;

    /**
     * 更新任务状态
     */
    void updateStatus(@Param("id") String id, @Param("status") String status) throws Exception;

    /**
     * 更新任务执行信息
     */
    void updateExecutionInfo(@Param("id") String id, 
                            @Param("lastExecuteTime") java.util.Date lastExecuteTime,
                            @Param("status") String status,
                            @Param("lastResult") String lastResult,
                            @Param("lastErrorMessage") String lastErrorMessage,
                            @Param("lastExecutionTime") Long lastExecutionTime,
                            @Param("nextExecuteTime") java.util.Date nextExecuteTime) throws Exception;

    /**
     * 查询需要执行的计划任务（到期时间）
     */
    List<ScheduledTask> selectTasksToExecute(@Param("currentTime") java.util.Date currentTime) throws Exception;

    /**
     * 根据创建人查询计划任务
     */
    List<ScheduledTask> selectByCreator(@Param("creator") String creator) throws Exception;

    /**
     * 根据标签查询计划任务
     */
    List<ScheduledTask> selectByTags(@Param("tags") String tags) throws Exception;

    /**
     * 获取任务统计信息
     */
    Map<String, Object> getTaskStatistics() throws Exception;

    /**
     * 根据状态查询计划任务
     */
    List<ScheduledTask> selectByStatus(@Param("status") String status) throws Exception;

    /**
     * 增加重试次数
     */
    void incrementRetryCount(@Param("id") String id) throws Exception;

    /**
     * 重置重试次数
     */
    void resetRetryCount(@Param("id") String id) throws Exception;
} 