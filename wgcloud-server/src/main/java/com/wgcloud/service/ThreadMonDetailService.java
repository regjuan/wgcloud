package com.wgcloud.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.ThreadMonDetail;
import com.wgcloud.mapper.ThreadMonDetailMapper;
import com.wgcloud.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ThreadMonDetailService {

    @Autowired
    private ThreadMonDetailMapper threadMonDetailMapper;

    public PageInfo selectByParams(Map<String, Object> params, int currPage, int pageSize) throws Exception {
        PageHelper.startPage(currPage, pageSize);
        List<ThreadMonDetail> list = threadMonDetailMapper.selectByParams(params);
        return new PageInfo<>(list);
    }

    public List<ThreadMonDetail> selectByParams(Map<String, Object> params) throws Exception {
        return threadMonDetailMapper.selectByParams(params);
    }

    public void save(ThreadMonDetail ThreadMonDetail) throws Exception {
        ThreadMonDetail.setId(UUIDUtil.getUUID());
        threadMonDetailMapper.save(ThreadMonDetail);
    }

    public void saveRecord(List<ThreadMonDetail> recordList) throws Exception {
        if (recordList.size() < 1) {
            return;
        }
        for (ThreadMonDetail as : recordList) {
            as.setId(UUIDUtil.getUUID());
        }
        threadMonDetailMapper.insertList(recordList);
    }

    public int deleteByThreadMonId(String threadMonId) throws Exception {
        return threadMonDetailMapper.deleteByThreadMonId(threadMonId);
    }

    public int updateByHostAndMonId(ThreadMonDetail threadMonDetail) throws Exception {
        return threadMonDetailMapper.updateByHostAndMonId(threadMonDetail);
    }

    public int deleteById(String[] id) throws Exception {
        return threadMonDetailMapper.deleteById(id);
    }
}
