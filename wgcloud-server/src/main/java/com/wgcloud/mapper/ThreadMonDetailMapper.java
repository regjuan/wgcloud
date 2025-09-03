package com.wgcloud.mapper;

import com.wgcloud.entity.ThreadMonDetail;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ThreadMonDetailMapper {

    List<ThreadMonDetail> selectByParams(Map<String, Object> map) throws Exception;

    void save(ThreadMonDetail ThreadMonDetail) throws Exception;

    void insertList(List<ThreadMonDetail> recordList) throws Exception;

    int deleteByThreadMonId(String threadMonId) throws Exception;

    int updateByHostAndMonId(ThreadMonDetail ThreadMonDetail) throws Exception;

    int deleteById(String[] id) throws Exception;

}
