package com.wgcloud.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.AlarmInfo;
import com.wgcloud.mapper.AlarmInfoMapper;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AlarmInfoService {

    private static final Logger logger = LoggerFactory.getLogger(AlarmInfoService.class);

    @Autowired
    private AlarmInfoMapper alarmInfoMapper;

    public PageInfo selectByParams(Map<String, Object> params, int currPage, int pageSize) throws Exception {
        PageHelper.startPage(currPage, pageSize);
        List<AlarmInfo> list = alarmInfoMapper.selectByParams(params);
        PageInfo<AlarmInfo> pageInfo = new PageInfo<AlarmInfo>(list);
        return pageInfo;
    }

    public void save(AlarmInfo alarmInfo) {
        try {
            alarmInfo.setId(UUIDUtil.getUUID());
            alarmInfo.setCreateTime(DateUtil.getNowTime());
            alarmInfoMapper.save(alarmInfo);
        } catch (Exception e) {
            logger.error("保存告警信息异常：", e);
        }
    }

    public int countByParams(Map<String, Object> params) throws Exception {
        return alarmInfoMapper.countByParams(params);
    }

    public int deleteById(String[] id) throws Exception {
        return alarmInfoMapper.deleteById(id);
    }

    public AlarmInfo selectById(String id) throws Exception {
        return alarmInfoMapper.selectById(id);
    }

    public List<AlarmInfo> selectAllByParams(Map<String, Object> params) throws Exception {
        return alarmInfoMapper.selectAllByParams(params);
    }


}
