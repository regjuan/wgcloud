package com.wgcloud.mapper;

import com.wgcloud.entity.DockerContainer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:DockerContainerMapper.java
 * @author: http://www.wgstart.com
 * @date: 2024年01月16日
 * @Description: Docker容器数据访问接口
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Repository
public interface DockerContainerMapper {

    /**
     * 根据参数查询所有Docker容器
     *
     * @param params
     * @return
     * @throws Exception
     */
    public List<DockerContainer> selectAllByParams(Map<String, Object> params) throws Exception;

    /**
     * 根据参数查询Docker容器（分页）
     *
     * @param params
     * @return
     * @throws Exception
     */
    public List<DockerContainer> selectByParams(Map<String, Object> params) throws Exception;

    /**
     * 根据ID查询Docker容器
     *
     * @param id
     * @return
     * @throws Exception
     */
    public DockerContainer selectById(String id) throws Exception;

    /**
     * 保存Docker容器
     *
     * @param dockerContainer
     * @throws Exception
     */
    public void save(DockerContainer dockerContainer) throws Exception;

    /**
     * 批量插入Docker容器
     *
     * @param recordList
     * @throws Exception
     */
    public void insertList(List<DockerContainer> recordList) throws Exception;

    /**
     * 批量更新Docker容器
     *
     * @param recordList
     * @throws Exception
     */
    public void updateList(List<DockerContainer> recordList) throws Exception;

    /**
     * 根据ID删除Docker容器
     *
     * @param id
     * @return
     * @throws Exception
     */
    public int deleteById(String[] id) throws Exception;

    /**
     * 根据主机名删除Docker容器
     *
     * @param params
     * @return
     * @throws Exception
     */
    public int deleteByHostname(Map<String, Object> params) throws Exception;

    /**
     * 根据容器ID删除Docker容器
     *
     * @param containerId
     * @return
     * @throws Exception
     */
    public int deleteByContainerId(String containerId) throws Exception;

    /**
     * 根据参数统计Docker容器数量
     *
     * @param params
     * @return
     * @throws Exception
     */
    public int countByParams(Map<String, Object> params) throws Exception;

    /**
     * 根据ID更新Docker容器
     *
     * @param dockerContainer
     * @return
     * @throws Exception
     */
    public int updateById(DockerContainer dockerContainer) throws Exception;

    /**
     * 根据日期删除Docker容器
     *
     * @param params
     * @return
     * @throws Exception
     */
    public int deleteByDate(Map<String, Object> params) throws Exception;
} 