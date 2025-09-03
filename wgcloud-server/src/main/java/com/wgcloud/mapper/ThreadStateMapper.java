package com.wgcloud.mapper;

import com.wgcloud.entity.ThreadState;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ThreadStateMapper {

    List<ThreadState> selectByParams(Map<String, Object> map) throws Exception;

    List<ThreadState> selectAllByParams(Map<String, Object> params) throws Exception;

    void save(ThreadState threadState) throws Exception;

    void insertList(List<ThreadState> recordList) throws Exception;

    int deleteByThreadMonId(String threadMonId) throws Exception;

    int deleteByDate(Map<String, Object> map) throws Exception;

    int deleteById(String[] id) throws Exception;

    ThreadState selectById(String id) throws Exception;

}
