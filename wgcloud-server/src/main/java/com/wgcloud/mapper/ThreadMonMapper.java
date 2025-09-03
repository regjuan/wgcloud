package com.wgcloud.mapper;

import com.wgcloud.entity.ThreadMon;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ThreadMonMapper {

    List<ThreadMon> selectByParams(Map<String, Object> map);

    ThreadMon selectById(String id) throws Exception;

    List<ThreadMon> selectAllByParams(Map<String, Object> params) throws Exception;

    void save(ThreadMon ThreadMon) throws Exception;

    int deleteById(String[] id) throws Exception;

    int countByParams(Map<String, Object> map) throws Exception;

    int updateById(ThreadMon ThreadMon) throws Exception;

}
