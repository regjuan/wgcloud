package com.wgcloud.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.ThreadState;
import com.wgcloud.mapper.ThreadStateMapper;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ThreadStateService {

    @Autowired
    private ThreadStateMapper threadStateMapper;

    public PageInfo<ThreadState> selectByParams(Map<String, Object> params, int currPage, int pageSize) throws Exception {
        PageHelper.startPage(currPage, pageSize);
        List<ThreadState> list = threadStateMapper.selectByParams(params);
        return new PageInfo<>(list);
    }

    public List<ThreadState> selectByParams(Map<String, Object> params) throws Exception {
        return threadStateMapper.selectByParams(params);
    }

    public void save(ThreadState threadState) throws Exception {
        threadState.setId(UUIDUtil.getUUID());
        threadState.setCreateTime(DateUtil.getNowTime());
        threadStateMapper.save(threadState);
    }

    public void saveRecord(List<ThreadState> recordList) throws Exception {
        if (recordList.size() < 1) {
            return;
        }
        for (ThreadState as : recordList) {
            as.setId(UUIDUtil.getUUID());
            as.setCreateTime(DateUtil.getNowTime());
        }
        threadStateMapper.insertList(recordList);
    }

    public int deleteByThreadMonId(String threadMonId) throws Exception {
        return threadStateMapper.deleteByThreadMonId(threadMonId);
    }

    public int deleteById(String[] id) throws Exception {
        return threadStateMapper.deleteById(id);
    }

    public ThreadState selectById(String id) throws Exception {
        return threadStateMapper.selectById(id);
    }

    public List<ThreadState> selectAllByParams(Map<String, Object> params) throws Exception {
        return threadStateMapper.selectAllByParams(params);
    }

}
