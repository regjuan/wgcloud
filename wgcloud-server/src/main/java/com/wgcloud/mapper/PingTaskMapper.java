package com.wgcloud.mapper;

import com.wgcloud.entity.PingTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Ping任务数据访问接口 - Server端
 * @author WGCLOUD
 */
public interface PingTaskMapper {

    /**
     * 根据参数查询ping任务列表
     */
    List<PingTask> selectByParams(Map<String, Object> params);

    /**
     * 根据参数统计ping任务数量
     */
    int countByParams(Map<String, Object> params);

    /**
     * 根据ID查询ping任务
     */
    PingTask selectById(@Param("id") String id);

    /**
     * 查询所有ping任务
     */
    List<PingTask> selectAll();

    /**
     * 保存ping任务
     */
    void save(PingTask pingTask);

    /**
     * 批量保存ping任务
     */
    void insertBatch(@Param("list") List<PingTask> list);

    /**
     * 更新ping任务
     */
    void updateById(PingTask pingTask);

    /**
     * 根据ID删除ping任务
     */
    void deleteById(@Param("id") String id);

    /**
     * 根据ID列表删除ping任务
     */
    void deleteByIds(@Param("ids") List<String> ids);

    /**
     * 获取所有启用的ping任务
     */
    List<PingTask> selectAllEnabled();

    /**
     * 根据任务名称查询ping任务
     */
    PingTask selectByTaskName(@Param("taskName") String taskName);

    /**
     * 根据标签查询ping任务
     */
    List<PingTask> selectByTags(@Param("tags") String tags);

    /**
     * 获取任务总数
     */
    int getTotalCount();

    /**
     * 获取启用的任务数
     */
    int getEnabledCount();

    /**
     * 获取禁用的任务数
     */
    int getDisabledCount();

    /**
     * 启用/禁用任务
     */
    void updateEnabled(@Param("id") String id, @Param("enabled") boolean enabled);
} 