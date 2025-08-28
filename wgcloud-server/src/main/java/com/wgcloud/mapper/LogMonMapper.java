package com.wgcloud.mapper;

import com.wgcloud.entity.LogMon;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface LogMonMapper {

    public List<LogMon> selectByParams(Map<String, Object> params) throws Exception;

    public int countByParams(Map<String, Object> params) throws Exception;

    public LogMon selectById(String id) throws Exception;

    public void save(LogMon LogMon) throws Exception;

    public int updateById(LogMon LogMon) throws Exception;

    public int deleteById(String[] id) throws Exception;

}
