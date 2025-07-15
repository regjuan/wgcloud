package com.wgcloud.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.DockerContainer;
import com.wgcloud.entity.DockerState;
import com.wgcloud.mapper.DockerContainerMapper;
import com.wgcloud.mapper.DockerStateMapper;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:DockerService.java
 * @author: http://www.wgstart.com
 * @date: 2024年01月16日
 * @Description: Docker监控服务
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Service
public class DockerService {

    @Autowired
    private DockerContainerMapper dockerContainerMapper;
    @Autowired
    private DockerStateMapper dockerStateMapper;

    /**
     * 分页查询Docker容器信息
     *
     * @param params
     * @param currPage
     * @param pageSize
     * @return
     * @throws Exception
     */
    public PageInfo<DockerContainer> selectByParams(Map<String, Object> params, int currPage, int pageSize) throws Exception {
        PageHelper.startPage(currPage, pageSize);
        List<DockerContainer> list = dockerContainerMapper.selectByParams(params);
        PageInfo<DockerContainer> pageInfo = new PageInfo<DockerContainer>(list);
        return pageInfo;
    }

    /**
     * 保存Docker容器信息
     *
     * @param dockerContainer
     * @throws Exception
     */
    public void save(DockerContainer dockerContainer) throws Exception {
        dockerContainer.setId(UUIDUtil.getUUID());
        dockerContainer.setCreateTime(DateUtil.getNowTime());
        dockerContainerMapper.save(dockerContainer);
    }

    /**
     * 批量保存Docker容器信息
     *
     * @param recordList
     * @throws Exception
     */
    @Transactional
    public void saveRecord(List<DockerContainer> recordList) throws Exception {
        if (recordList.size() < 1) {
            return;
        }
        for (DockerContainer container : recordList) {
            container.setId(UUIDUtil.getUUID());
            container.setCreateTime(DateUtil.getNowTime());
        }
        dockerContainerMapper.insertList(recordList);
    }

    /**
     * 批量更新Docker容器信息
     *
     * @param recordList
     * @throws Exception
     */
    @Transactional
    public void updateRecord(List<DockerContainer> recordList) throws Exception {
        if (recordList.size() < 1) {
            return;
        }
        dockerContainerMapper.updateList(recordList);
    }

    /**
     * 根据ID查询Docker容器
     *
     * @param id
     * @return
     * @throws Exception
     */
    public DockerContainer selectById(String id) throws Exception {
        return dockerContainerMapper.selectById(id);
    }

    /**
     * 根据参数查询所有Docker容器
     *
     * @param params
     * @return
     * @throws Exception
     */
    public List<DockerContainer> selectAllByParams(Map<String, Object> params) throws Exception {
        return dockerContainerMapper.selectAllByParams(params);
    }

    /**
     * 根据ID删除Docker容器
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Transactional
    public int deleteById(String[] id) throws Exception {
        return dockerContainerMapper.deleteById(id);
    }

    /**
     * 根据ID更新Docker容器信息
     *
     * @param dockerContainer
     * @throws Exception
     */
    public void updateById(DockerContainer dockerContainer) throws Exception {
        dockerContainerMapper.updateById(dockerContainer);
    }

    /**
     * 根据参数统计Docker容器数量
     *
     * @param params
     * @return
     * @throws Exception
     */
    public int countByParams(Map<String, Object> params) throws Exception {
        return dockerContainerMapper.countByParams(params);
    }

    /**
     * 根据主机名删除Docker容器
     *
     * @param params
     * @return
     * @throws Exception
     */
    public int deleteByHostname(Map<String, Object> params) throws Exception {
        return dockerContainerMapper.deleteByHostname(params);
    }

    // Docker状态相关方法

    /**
     * 保存Docker状态信息
     *
     * @param dockerState
     * @throws Exception
     */
    public void saveDockerState(DockerState dockerState) throws Exception {
        dockerState.setId(UUIDUtil.getUUID());
        dockerState.setCreateTime(DateUtil.getNowTime());
        dockerState.setDateStr(DateUtil.getDateTimeString(dockerState.getCreateTime()));
        dockerStateMapper.save(dockerState);
    }

    /**
     * 批量保存Docker状态信息
     *
     * @param recordList
     * @throws Exception
     */
    @Transactional
    public void saveDockerStateRecord(List<DockerState> recordList) throws Exception {
        if (recordList.size() < 1) {
            return;
        }
        for (DockerState state : recordList) {
            state.setId(UUIDUtil.getUUID());
            state.setDateStr(DateUtil.getDateTimeString(state.getCreateTime()));
        }
        dockerStateMapper.insertList(recordList);
    }

    /**
     * 根据参数查询Docker状态
     *
     * @param params
     * @return
     * @throws Exception
     */
    public List<DockerState> getContainerStates(Map<String, Object> params) throws Exception {
        return dockerStateMapper.selectByParams(params);
    }

    /**
     * 分页查询Docker状态
     *
     * @param params
     * @param currPage
     * @param pageSize
     * @return
     * @throws Exception
     */
    public PageInfo<DockerState> selectDockerStatesByParams(Map<String, Object> params, int currPage, int pageSize) throws Exception {
        PageHelper.startPage(currPage, pageSize);
        List<DockerState> list = dockerStateMapper.selectByParams(params);
        PageInfo<DockerState> pageInfo = new PageInfo<DockerState>(list);
        return pageInfo;
    }

    /**
     * 根据容器ID删除Docker状态
     *
     * @param containerId
     * @return
     * @throws Exception
     */
    @Transactional
    public int deleteDockerStatesByContainerId(String containerId) throws Exception {
        return dockerStateMapper.deleteByContainerId(containerId);
    }

    // Docker容器操作方法

    /**
     * 启动Docker容器
     *
     * @param hostname
     * @param containerId
     * @return
     */
    public boolean startContainer(String hostname, String containerId) {
        // TODO: 实现通过Agent端启动容器的逻辑
        // 这里需要调用Agent端的API或者通过消息队列发送指令
        return false;
    }

    /**
     * 停止Docker容器
     *
     * @param hostname
     * @param containerId
     * @return
     */
    public boolean stopContainer(String hostname, String containerId) {
        // TODO: 实现通过Agent端停止容器的逻辑
        // 这里需要调用Agent端的API或者通过消息队列发送指令
        return false;
    }

    /**
     * 重启Docker容器
     *
     * @param hostname
     * @param containerId
     * @return
     */
    public boolean restartContainer(String hostname, String containerId) {
        // TODO: 实现通过Agent端重启容器的逻辑
        // 这里需要调用Agent端的API或者通过消息队列发送指令
        return false;
    }

    /**
     * 获取容器日志
     *
     * @param hostname
     * @param containerId
     * @param tailLines
     * @return
     */
    public String getContainerLogs(String hostname, String containerId, int tailLines) {
        // TODO: 实现通过Agent端获取容器日志的逻辑
        // 这里需要调用Agent端的API或者通过消息队列发送指令
        return "暂未实现";
    }

    /**
     * 获取容器详细信息
     *
     * @param hostname
     * @param containerId
     * @return
     */
    public DockerContainer getContainerDetails(String hostname, String containerId) {
        // TODO: 实现通过Agent端获取容器详细信息的逻辑
        // 这里需要调用Agent端的API或者通过消息队列发送指令
        return null;
    }
} 