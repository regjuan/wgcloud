package com.wgcloud.mapper;

import com.wgcloud.entity.CommandTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 指令任务数据访问接口 - Server端
 * @author WGCLOUD
 */
public interface CommandTaskMapper {

    /**
     * 根据参数查询指令任务列表
     */
    List<CommandTask> selectByParams(Map<String, Object> params);

    /**
     * 根据参数统计指令任务数量
     */
    int countByParams(Map<String, Object> params);

    /**
     * 根据ID查询指令任务
     */
    CommandTask selectById(@Param("id") String id);

    /**
     * 查询所有指令任务
     */
    List<CommandTask> selectAll();

    /**
     * 保存指令任务
     */
    void save(CommandTask commandTask);

    /**
     * 批量保存指令任务
     */
    void insertBatch(@Param("list") List<CommandTask> list);

    /**
     * 更新指令任务
     */
    void updateById(CommandTask commandTask);

    /**
     * 根据ID删除指令任务
     */
    void deleteById(@Param("id") String id);

    /**
     * 批量删除指令任务
     */
    void deleteBatch(@Param("ids") List<String> ids);

    /**
     * 根据状态查询指令任务
     */
    List<CommandTask> selectByStatus(@Param("status") String status);

    /**
     * 查询待执行的定时任务
     */
    List<CommandTask> selectScheduledTasks();

    /**
     * 更新任务执行统计
     */
    void updateExecutionStats(@Param("id") String id, 
                             @Param("executedCount") Integer executedCount,
                             @Param("status") String status,
                             @Param("resultSummary") String resultSummary);

    /**
     * 根据创建人查询任务
     */
    List<CommandTask> selectByCreator(@Param("creator") String creator);

    /**
     * 查询最近的任务
     */
    List<CommandTask> selectRecentTasks(@Param("limit") Integer limit);

    /**
     * 根据任务名称模糊查询
     */
    List<CommandTask> selectByCommandName(@Param("commandName") String commandName);
} 