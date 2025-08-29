package com.wgcloud.mapper;

import com.wgcloud.entity.AlarmInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AlarmInfoMapper {

    public List<AlarmInfo> selectAllByParams(Map<String, Object> map);

    public int countByParams(Map<String, Object> params) throws Exception;

    public List<AlarmInfo> selectByParams(Map<String, Object> params) throws Exception;

    public AlarmInfo selectById(String id) throws Exception;

    public void save(AlarmInfo alarmInfo) throws Exception;

    public int deleteById(String[] id) throws Exception;

    public void insertList(List<AlarmInfo> recordList) throws Exception;

    public int deleteByDate(Map<String, Object> map) throws Exception;

}
