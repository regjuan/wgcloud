package com.wgcloud.service;

import com.wgcloud.entity.PingResult;
import com.wgcloud.entity.PingTask;
import com.wgcloud.mapper.PingResultMapper;
import com.wgcloud.mapper.PingTaskMapper;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.PageUtil;
import com.wgcloud.util.staticvar.StaticKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Ping监控服务类 - Server端
 * @author WGCLOUD
 */
@Service
public class PingService {

    private static final Logger logger = LoggerFactory.getLogger(PingService.class);

    @Autowired
    private PingTaskMapper pingTaskMapper;

    @Autowired
    private PingResultMapper pingResultMapper;

    @Autowired
    private LogInfoService logInfoService;

    // ================ PingTask相关方法 ================

    /**
     * 查询所有ping任务
     */
    public List<PingTask> selectAll() {
        try {
            return pingTaskMapper.selectAll();
        } catch (Exception e) {
            logger.error("查询所有ping任务失败: {}", e.getMessage());
            logInfoService.save("selectAll", "查询所有ping任务失败：" + e.toString(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 获取所有启用的ping任务
     */
    public List<PingTask> selectAllEnabled() {
        try {
            return pingTaskMapper.selectAllEnabled();
        } catch (Exception e) {
            logger.error("获取启用的ping任务失败: {}", e.getMessage());
            logInfoService.save("selectAllEnabled", "获取启用的ping任务失败：" + e.toString(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 分页查询ping任务
     */
    public PageUtil<PingTask> selectByParams(PageUtil<PingTask> pageUtil) {
        try {
            Map<String, Object> params = pageUtil.getParams();
            int totalCount = pingTaskMapper.countByParams(params);
            pageUtil.setTotalCount(totalCount);
            
            params.put("startIndex", pageUtil.getStartIndex());
            params.put("pageSize", pageUtil.getPageSize());
            
            List<PingTask> list = pingTaskMapper.selectByParams(params);
            pageUtil.setList(list);
            
            return pageUtil;
        } catch (Exception e) {
            logger.error("分页查询ping任务失败: {}", e.getMessage());
            logInfoService.save("selectByParams", "分页查询ping任务失败：" + e.toString(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 根据ID查询ping任务
     */
    public PingTask selectById(String id) {
        try {
            return pingTaskMapper.selectById(id);
        } catch (Exception e) {
            logger.error("根据ID查询ping任务失败: {}", e.getMessage());
            logInfoService.save("selectById", "根据ID查询ping任务失败：" + e.toString(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 根据任务名称查询ping任务
     */
    public PingTask selectByTaskName(String taskName) {
        try {
            return pingTaskMapper.selectByTaskName(taskName);
        } catch (Exception e) {
            logger.error("根据任务名称查询ping任务失败: {}", e.getMessage());
            logInfoService.save("selectByTaskName", "根据任务名称查询ping任务失败：" + e.toString(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 保存ping任务
     */
    @Transactional
    public void save(PingTask pingTask) {
        try {
            if (pingTask.getId() == null || pingTask.getId().isEmpty()) {
                pingTask.setId(UUID.randomUUID().toString());
            }
            pingTask.setCreateTime(DateUtil.getNowTime());
            pingTask.setUpdateTime(DateUtil.getNowTime());
            pingTaskMapper.save(pingTask);
        } catch (Exception e) {
            logger.error("保存ping任务失败: {}", e.getMessage());
            logInfoService.save("save", "保存ping任务失败：" + e.toString(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 更新ping任务
     */
    @Transactional
    public void updateById(PingTask pingTask) {
        try {
            pingTask.setUpdateTime(DateUtil.getNowTime());
            pingTaskMapper.updateById(pingTask);
        } catch (Exception e) {
            logger.error("更新ping任务失败: {}", e.getMessage());
            logInfoService.save("updateById", "更新ping任务失败：" + e.toString(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 启用/禁用ping任务
     */
    @Transactional
    public void updateEnabled(String id, boolean enabled) {
        try {
            pingTaskMapper.updateEnabled(id, enabled);
        } catch (Exception e) {
            logger.error("更新ping任务状态失败: {}", e.getMessage());
            logInfoService.save("updateEnabled", "更新ping任务状态失败：" + e.toString(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 删除ping任务
     */
    @Transactional
    public void deleteById(String id) {
        try {
            pingTaskMapper.deleteById(id);
            // 同时删除相关的ping结果
            pingResultMapper.deleteByTaskId(id);
        } catch (Exception e) {
            logger.error("删除ping任务失败: {}", e.getMessage());
            logInfoService.save("deleteById", "删除ping任务失败：" + e.toString(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 根据ID列表删除ping任务
     */
    @Transactional
    public void deleteByIds(List<String> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return;
            }
            
            for (String id : ids) {
                deleteById(id);
            }
        } catch (Exception e) {
            logger.error("批量删除ping任务失败: {}", e.getMessage());
            logInfoService.save("deleteByIds", "批量删除ping任务失败：" + e.toString(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 获取任务统计信息
     */
    public Map<String, Object> getTaskStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCount", pingTaskMapper.getTotalCount());
            stats.put("enabledCount", pingTaskMapper.getEnabledCount());
            stats.put("disabledCount", pingTaskMapper.getDisabledCount());
            return stats;
        } catch (Exception e) {
            logger.error("获取任务统计信息失败: {}", e.getMessage());
            throw e;
        }
    }

    // ================ PingResult相关方法 ================

    /**
     * 保存ping结果
     */
    @Transactional
    public void savePingResult(PingResult pingResult) {
        try {
            if (pingResult.getId() == null || pingResult.getId().isEmpty()) {
                pingResult.setId(UUID.randomUUID().toString());
            }
            if (pingResult.getPingTime() == null) {
                pingResult.setPingTime(DateUtil.getNowTime());
            }
            pingResultMapper.save(pingResult);
        } catch (Exception e) {
            logger.error("保存ping结果失败: {}", e.getMessage());
            logInfoService.save("savePingResult", "保存ping结果失败：" + e.toString(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 批量保存ping结果
     */
    @Transactional
    public void savePingResults(List<PingResult> pingResults) {
        try {
            if (pingResults == null || pingResults.isEmpty()) {
                return;
            }
            
            for (PingResult result : pingResults) {
                if (result.getId() == null || result.getId().isEmpty()) {
                    result.setId(UUID.randomUUID().toString());
                }
                if (result.getPingTime() == null) {
                    result.setPingTime(DateUtil.getNowTime());
                }
            }
            
            pingResultMapper.insertBatch(pingResults);
        } catch (Exception e) {
            logger.error("批量保存ping结果失败: {}", e.getMessage());
            logInfoService.save("savePingResults", "批量保存ping结果失败：" + e.toString(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 分页查询ping结果
     */
    public PageUtil<PingResult> selectResultsByParams(PageUtil<PingResult> pageUtil) {
        try {
            Map<String, Object> params = pageUtil.getParams();
            int totalCount = pingResultMapper.countByParams(params);
            pageUtil.setTotalCount(totalCount);
            
            params.put("startIndex", pageUtil.getStartIndex());
            params.put("pageSize", pageUtil.getPageSize());
            
            List<PingResult> list = pingResultMapper.selectByParams(params);
            pageUtil.setList(list);
            
            return pageUtil;
        } catch (Exception e) {
            logger.error("分页查询ping结果失败: {}", e.getMessage());
            logInfoService.save("selectResultsByParams", "分页查询ping结果失败：" + e.toString(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }

    /**
     * 根据任务ID分页查询ping结果
     */
    public PageUtil<PingResult> selectResultsByTaskId(String taskId, int page, int size) {
        try {
            PageUtil<PingResult> pageUtil = new PageUtil<>();
            pageUtil.setPage(page);
            pageUtil.setPageSize(size);
            
            int totalCount = pingResultMapper.countByTaskId(taskId);
            pageUtil.setTotalCount(totalCount);
            
            int start = (page - 1) * size;
            List<PingResult> list = pingResultMapper.selectByTaskId(taskId, start, size);
            pageUtil.setList(list);
            
            return pageUtil;
        } catch (Exception e) {
            logger.error("根据任务ID分页查询ping结果失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 获取最近的ping结果
     */
    public List<PingResult> getRecentResults(String taskId, int limit) {
        try {
            return pingResultMapper.selectRecentResults(taskId, limit);
        } catch (Exception e) {
            logger.error("获取最近ping结果失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 获取失败的ping结果
     */
    public List<PingResult> getFailedResults(String taskId, int limit) {
        try {
            return pingResultMapper.selectFailedResults(taskId, limit);
        } catch (Exception e) {
            logger.error("获取失败ping结果失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 获取任务统计信息
     */
    public Map<String, Object> getTaskStatistics(String taskId) {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 获取24小时内的统计
            int failureCount = pingResultMapper.getFailureCount24h(taskId);
            int totalCount = pingResultMapper.getTotalCount24h(taskId);
            
            stats.put("failureCount24h", failureCount);
            stats.put("totalCount24h", totalCount);
            
            if (totalCount > 0) {
                double availability = (double) (totalCount - failureCount) / totalCount * 100;
                stats.put("availability24h", Math.round(availability * 100.0) / 100.0);
            } else {
                stats.put("availability24h", 0.0);
            }
            
            return stats;
        } catch (Exception e) {
            logger.error("获取任务统计信息失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 清理过期的ping结果
     */
    @Transactional
    public void cleanExpiredResults(int days) {
        try {
            pingResultMapper.deleteExpiredResults(days);
        } catch (Exception e) {
            logger.error("清理过期ping结果失败: {}", e.getMessage());
            logInfoService.save("cleanExpiredResults", "清理过期ping结果失败：" + e.toString(), StaticKeys.LOG_ERROR);
            throw e;
        }
    }
} 