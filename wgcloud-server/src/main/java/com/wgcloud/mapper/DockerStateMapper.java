package com.wgcloud.mapper;

import com.wgcloud.entity.DockerState;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:DockerStateMapper.java
 * @author: http://www.wgstart.com
 * @date: 2024年01月16日
 * @Description: Docker容器状态数据访问接口
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Repository
public interface DockerStateMapper {

    /**
     * 根据参数查询所有Docker状态
     *
     * @param params
     * @return
     * @throws Exception
     */
    public List<DockerState> selectAllByParams(Map<String, Object> params) throws Exception;

    /**
     * 根据参数查询Docker状态（分页）
     *
     * @param params
     * @return
     * @throws Exception
     */
    public List<DockerState> selectByParams(Map<String, Object> params) throws Exception;

    /**
     * 根据ID查询Docker状态
     *
     * @param id
     * @return
     * @throws Exception
     */
    public DockerState selectById(String id) throws Exception;

    /**
     * 保存Docker状态
     *
     * @param dockerState
     * @throws Exception
     */
    public void save(DockerState dockerState) throws Exception;

    /**
     * 批量插入Docker状态
     *
     * @param recordList
     * @throws Exception
     */
    public void insertList(List<DockerState> recordList) throws Exception;

    /**
     * 根据ID删除Docker状态
     *
     * @param id
     * @return
     * @throws Exception
     */
    public int deleteById(String[] id) throws Exception;

    /**
     * 根据容器ID删除Docker状态
     *
     * @param containerId
     * @return
     * @throws Exception
     */
    public int deleteByContainerId(String containerId) throws Exception;

    /**
     * 根据主机名删除Docker状态
     *
     * @param params
     * @return
     * @throws Exception
     */
    public int deleteByHostname(Map<String, Object> params) throws Exception;

    /**
     * 根据日期删除Docker状态
     *
     * @param params
     * @return
     * @throws Exception
     */
    public int deleteByDate(Map<String, Object> params) throws Exception;

    /**
     * 根据参数统计Docker状态数量
     *
     * @param params
     * @return
     * @throws Exception
     */
    public int countByParams(Map<String, Object> params) throws Exception;
} 