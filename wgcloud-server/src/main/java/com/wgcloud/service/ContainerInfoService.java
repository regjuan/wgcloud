package com.wgcloud.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.ContainerInfo;
import com.wgcloud.mapper.ContainerInfoMapper;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @version v3.3
 * @ClassName:ContainerInfoService.java
 * @author: http://www.wgstart.com
 * @date: 2025年9月8日
 * @Description: 容器信息Service
 * @Copyright: 2017-2025 wgcloud. All rights reserved.
 */
@Service
public class ContainerInfoService {

    @Autowired
    private ContainerInfoMapper containerInfoMapper;

    public PageInfo selectByParams(Map<String, Object> params, int currPage, int pageSize) throws Exception {
        PageHelper.startPage(currPage, pageSize);
        List<ContainerInfo> list = containerInfoMapper.selectByParams(params);
        PageInfo<ContainerInfo> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    public void save(ContainerInfo containerInfo) throws Exception {
        containerInfo.setId(UUIDUtil.getUUID());
        containerInfo.setLastHeartbeat(new Date());
        containerInfoMapper.save(containerInfo);
    }

    @Transactional
    public void saveList(List<ContainerInfo> recordList) throws Exception {
        if(recordList.size() > 0){
            for(ContainerInfo containerInfo : recordList){
                containerInfo.setId(UUIDUtil.getUUID());
            }
            containerInfoMapper.saveList(recordList);
        }
    }
    
    @Transactional
    public void saveRecord(List<ContainerInfo> recordList) throws Exception {
        if(recordList.size()<1){
            return;
        }
        String hostId = recordList.get(0).getHostId();
        //先删除该主机下本次未上报的容器
        String[] names = new String[recordList.size()];
        for(int i=0;i<recordList.size();i++){
            names[i] = recordList.get(i).getNames();
        }
        java.util.Map<String,Object> params = new java.util.HashMap<>();
        params.put("hostId",hostId);
        params.put("namesList",java.util.Arrays.asList(names));
        containerInfoMapper.deleteByHostIdAndContainerNames(params);
        //再批量保存
        for(ContainerInfo containerInfo : recordList){
            containerInfo.setId(UUIDUtil.getUUID());
            containerInfo.setLastHeartbeat(new Date());
            containerInfoMapper.save(containerInfo);
        }
    }

    public int deleteByHostId(String hostId) throws Exception {
        return containerInfoMapper.deleteByHostId(hostId);
    }

    public int countByParams(Map<String, Object> params) throws Exception {
        return containerInfoMapper.countByParams(params);
    }

    @Transactional
    public int deleteById(String[] id) throws Exception {
        return containerInfoMapper.deleteById(id);
    }

    public ContainerInfo selectById(String id) throws Exception {
        return containerInfoMapper.selectById(id);
    }

    public int updateById(ContainerInfo containerInfo) throws Exception {
        return containerInfoMapper.updateById(containerInfo);
    }
    
    public List<ContainerInfo> selectStaleByParams(Map<String, Object> params) throws Exception {
        return containerInfoMapper.selectStaleByParams(params);
    }

    @Transactional
    public void updateAlertState(List<ContainerInfo> recordList) throws Exception {
        if (recordList.size() > 0) {
            containerInfoMapper.updateAlertState(recordList);
        }
    }
}
