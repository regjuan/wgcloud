package com.wgcloud.mapper;

import com.wgcloud.entity.ContainerInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @version v3.3
 * @ClassName:ContainerInfoMapper.java
 * @author: http://www.wgstart.com
 * @date: 2025年9月8日
 * @Description: 容器信息Mapper
 * @Copyright: 2017-2025 wgcloud. All rights reserved.
 */
@Repository
public interface ContainerInfoMapper {

    public List<ContainerInfo> selectByParams(Map<String, Object> map) throws Exception;

    public void save(ContainerInfo containerInfo) throws Exception;

    public void saveList(List<ContainerInfo> recordList) throws Exception;

    public int deleteByHostId(String hostId) throws Exception;

    public int deleteByHostIdAndContainerNames(Map<String, Object> params) throws Exception;

    public int countByParams(Map<String, Object> params) throws Exception;

    public int deleteById(String[] id) throws Exception;

    public ContainerInfo selectById(String id) throws Exception;

    public int updateById(ContainerInfo containerInfo) throws Exception;

    public List<ContainerInfo> selectStaleByParams(Map<String, Object> params) throws Exception;

    public void updateAlertState(List<ContainerInfo> recordList) throws Exception;

}
