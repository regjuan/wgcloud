package com.wgcloud.mapper;

import com.wgcloud.entity.PingResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Ping结果数据访问接口 - Server端
 * @author WGCLOUD
 */
public interface PingResultMapper {

    /**
     * 根据参数查询ping结果列表
     */
    List<PingResult> selectByParams(Map<String, Object> params);

    /**
     * 根据参数统计ping结果数量
     */
    int countByParams(Map<String, Object> params);

    /**
     * 根据ID查询ping结果
     */
    PingResult selectById(@Param("id") String id);

    /**
     * 保存ping结果
     */
    void save(PingResult pingResult);

    /**
     * 批量保存ping结果
     */
    void insertBatch(@Param("list") List<PingResult> list);

    /**
     * 更新ping结果
     */
    void updateById(PingResult pingResult);

    /**
     * 根据ID删除ping结果
     */
    void deleteById(@Param("id") String id);

    /**
     * 根据任务ID删除ping结果
     */
    void deleteByTaskId(@Param("taskId") String taskId);

    /**
     * 删除过期的ping结果
     */
    void deleteExpiredResults(@Param("days") int days);

    /**
     * 根据任务ID查询ping结果（分页）
     */
    List<PingResult> selectByTaskId(@Param("taskId") String taskId, 
                                   @Param("start") Integer start, 
                                   @Param("limit") Integer limit);

    /**
     * 根据任务ID统计ping结果数量
     */
    int countByTaskId(@Param("taskId") String taskId);

    /**
     * 根据目标IP查询ping结果
     */
    List<PingResult> selectByTargetIp(@Param("targetIp") String targetIp);

    /**
     * 获取图表数据
     */
    List<PingResult> selectChartData(@Param("taskId") String taskId, 
                                    @Param("targetIp") String targetIp, 
                                    @Param("dateTime") String dateTime);

    /**
     * 获取最近的ping结果
     */
    List<PingResult> selectRecentResults(@Param("taskId") String taskId, 
                                        @Param("limit") int limit);

    /**
     * 获取失败的ping结果
     */
    List<PingResult> selectFailedResults(@Param("taskId") String taskId, 
                                        @Param("limit") int limit);

    /**
     * 统计任务的成功率
     */
    Map<String, Object> getSuccessRate(@Param("taskId") String taskId, 
                                      @Param("startTime") String startTime, 
                                      @Param("endTime") String endTime);

    /**
     * 统计任务的平均响应时间
     */
    Double getAvgResponseTime(@Param("taskId") String taskId, 
                             @Param("startTime") String startTime, 
                             @Param("endTime") String endTime);

    /**
     * 获取任务的响应时间趋势
     */
    List<Map<String, Object>> getResponseTimeTrend(@Param("taskId") String taskId, 
                                                   @Param("targetIp") String targetIp,
                                                   @Param("startTime") String startTime, 
                                                   @Param("endTime") String endTime);

    /**
     * 获取任务的可用性统计
     */
    Map<String, Object> getAvailabilityStats(@Param("taskId") String taskId, 
                                            @Param("startTime") String startTime, 
                                            @Param("endTime") String endTime);

    /**
     * 获取IP的ping统计
     */
    List<Map<String, Object>> getIpPingStats(@Param("taskId") String taskId, 
                                            @Param("startTime") String startTime, 
                                            @Param("endTime") String endTime);

    /**
     * 获取所有目标IP列表
     */
    List<String> getAllTargetIps(@Param("taskId") String taskId);

    /**
     * 获取结果总数
     */
    int getTotalCount();

    /**
     * 获取成功结果数
     */
    int getSuccessCount();

    /**
     * 获取失败结果数
     */
    int getFailureCount();

    /**
     * 获取最近24小时的失败次数
     */
    int getFailureCount24h(@Param("taskId") String taskId);

    /**
     * 获取最近24小时的总次数
     */
    int getTotalCount24h(@Param("taskId") String taskId);
} 