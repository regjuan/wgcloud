package com.wgcloud.mapper;

import com.wgcloud.entity.CommandExecutionRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 指令执行记录数据访问接口 - Server端
 * @author WGCLOUD
 */
public interface CommandExecutionRecordMapper {

    /**
     * 根据参数查询执行记录列表
     */
    List<CommandExecutionRecord> selectByParams(Map<String, Object> params);

    /**
     * 根据参数统计执行记录数量
     */
    int countByParams(Map<String, Object> params);

    /**
     * 根据ID查询执行记录
     */
    CommandExecutionRecord selectById(@Param("id") String id);

    /**
     * 查询所有执行记录
     */
    List<CommandExecutionRecord> selectAll();

    /**
     * 保存执行记录
     */
    void save(CommandExecutionRecord record);

    /**
     * 批量保存执行记录
     */
    void insertBatch(@Param("list") List<CommandExecutionRecord> list);

    /**
     * 更新执行记录
     */
    void updateById(CommandExecutionRecord record);

    /**
     * 根据ID删除执行记录
     */
    void deleteById(@Param("id") String id);

    /**
     * 批量删除执行记录
     */
    void deleteBatch(@Param("ids") List<String> ids);

    /**
     * 根据任务ID查询执行记录
     */
    List<CommandExecutionRecord> selectByTaskId(@Param("commandTaskId") String commandTaskId);

    /**
     * 根据状态查询执行记录
     */
    List<CommandExecutionRecord> selectByStatus(@Param("status") String status);

    /**
     * 根据主机名查询执行记录
     */
    List<CommandExecutionRecord> selectByHostname(@Param("hostname") String hostname);

    /**
     * 更新执行状态
     */
    void updateStatus(@Param("id") String id, 
                     @Param("status") String status,
                     @Param("result") String result,
                     @Param("errorMessage") String errorMessage,
                     @Param("exitCode") Integer exitCode,
                     @Param("executionTime") Long executionTime);

    /**
     * 根据任务ID统计各状态的记录数
     */
    Map<String, Integer> countStatusByTaskId(@Param("commandTaskId") String commandTaskId);

    /**
     * 查询任务的执行进度
     */
    Map<String, Object> selectTaskProgress(@Param("commandTaskId") String commandTaskId);

    /**
     * 根据任务ID删除所有相关记录
     */
    void deleteByTaskId(@Param("commandTaskId") String commandTaskId);

    /**
     * 查询失败的执行记录
     */
    List<CommandExecutionRecord> selectFailedRecords(@Param("commandTaskId") String commandTaskId);

    /**
     * 查询超时的执行记录
     */
    List<CommandExecutionRecord> selectTimeoutRecords();

    /**
     * 查询指定时间段内的执行记录
     */
    List<CommandExecutionRecord> selectByTimeRange(@Param("startTime") String startTime, 
                                                   @Param("endTime") String endTime);
} 