package com.wgcloud.service;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.*;
import com.wgcloud.mapper.SystemInfoMapper;
import com.wgcloud.mapper.TagRelationMapper;
import com.wgcloud.mapper.ThreadMonMapper;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ThreadMonService {

    private static final Logger logger = LoggerFactory.getLogger(ThreadMonService.class);

    @Resource
    private ThreadMonMapper threadMonMapper;
    @Autowired
    private ThreadMonDetailService threadMonDetailService;
    @Autowired
    private TagRelationMapper tagRelationMapper;
    @Autowired
    private SystemInfoMapper systemInfoMapper;
    @Autowired
    private TagService tagService;


    public PageInfo<ThreadMon> selectByParams(Map<String, Object> params, int currPage, int pageSize) throws Exception {
        PageHelper.startPage(currPage, pageSize);
        List<ThreadMon> list = threadMonMapper.selectByParams(params);
        for (ThreadMon threadMon : list) {
            if (!StringUtils.isEmpty(threadMon.getTargetTags())) {
                threadMon.setTargetTagsList(JSONUtil.toList(threadMon.getTargetTags(), String.class));
            }
        }
        return new PageInfo<>(list);
    }

    public ThreadMon selectById(String id) throws Exception {
        ThreadMon threadMon = threadMonMapper.selectById(id);
        if (threadMon != null && !StringUtils.isEmpty(threadMon.getTargetTags())) {
            threadMon.setTargetTagsList(JSONUtil.toList(threadMon.getTargetTags(), String.class));
        }
        return threadMon;
    }

    public List<ThreadMon> selectAllByParams(Map<String, Object> params) throws Exception {
        List<ThreadMon> list = threadMonMapper.selectByParams(params);
        for (ThreadMon threadMon : list) {
            if (!StringUtils.isEmpty(threadMon.getTargetTags())) {
                threadMon.setTargetTagsList(JSONUtil.toList(threadMon.getTargetTags(), String.class));
            }
        }
        return list;
    }

    @Transactional
    public void save(ThreadMon threadMon) throws Exception {
        if (CollectionUtils.isEmpty(threadMon.getTargetTagsList()) && !StringUtils.isEmpty(threadMon.getTargetTags())) {
            threadMon.setTargetTagsList(JSONUtil.toList(threadMon.getTargetTags(), String.class));
        }
        if (threadMon.getTargetTagsList() != null) {
            threadMon.setTargetTags(JSONUtil.toJsonStr(threadMon.getTargetTagsList()));
        }
        if (StringUtils.isEmpty(threadMon.getActive())) {
            threadMon.setActive("1");
        }
        threadMon.setId(UUIDUtil.getUUID());
        threadMon.setCreateTime(DateUtil.getNowTime());
        threadMonMapper.save(threadMon);

        // Save tag relations
        if (!CollectionUtils.isEmpty(threadMon.getTargetTagsList())) {
            for (String tagId : threadMon.getTargetTagsList()) {
                Tag tag = tagService.selectByName(tagId);

                TagRelation tagRelation = new TagRelation();
                tagRelation.setId(UUIDUtil.getUUID());
                tagRelation.setRelationId(threadMon.getId());
                tagRelation.setTagId(tag.getId());
                tagRelation.setRelationType("THREAD_MON");
                tagRelationMapper.save(tagRelation);
            }
        }

        updateThreadMonDetails(threadMon);
    }

    @Transactional
    public int updateById(ThreadMon threadMon) throws Exception {
        if (CollectionUtils.isEmpty(threadMon.getTargetTagsList()) && !StringUtils.isEmpty(threadMon.getTargetTags())) {
            threadMon.setTargetTagsList(JSONUtil.toList(threadMon.getTargetTags(), String.class));
        }
        if (threadMon.getTargetTagsList() != null) {
            threadMon.setTargetTags(JSONUtil.toJsonStr(threadMon.getTargetTagsList()));
        }

        Map<String, Object> params = new HashMap<>();
        params.put("relationId", threadMon.getId());
        params.put("relationType", "THREAD_MON");
        tagRelationMapper.deleteByRelationIdAndRelationType(params);

        if (!CollectionUtils.isEmpty(threadMon.getTargetTagsList())) {
            for (String tagId : threadMon.getTargetTagsList()) {

                Tag tag = tagService.selectByName(tagId);
                TagRelation tagRelation = new TagRelation();
                tagRelation.setId(UUIDUtil.getUUID());
                tagRelation.setRelationId(threadMon.getId());
                tagRelation.setTagId(tag.getId());
                tagRelation.setRelationType("THREAD_MON");
                tagRelationMapper.save(tagRelation);
            }
        }

        updateThreadMonDetails(threadMon);
        return threadMonMapper.updateById(threadMon);
    }

    private void updateThreadMonDetails(ThreadMon threadMon) throws Exception {
        threadMonDetailService.deleteByThreadMonId(threadMon.getId());
        if (CollectionUtils.isEmpty(threadMon.getTargetTagsList())) {
            return;
        }
        Map<String, Object> hostParams = new HashMap<>();
        hostParams.put("tags", threadMon.getTargetTagsList());
        hostParams.put("tagsSize", threadMon.getTargetTagsList().size());
//        hostParams.put("state", "1");
        List<SystemInfo> hostInfos = systemInfoMapper.selectByParams(hostParams);
        if (CollectionUtils.isEmpty(hostInfos)) {
            return;
        }
        List<ThreadMonDetail> detailList = new ArrayList<>();
        for (SystemInfo hostInfo : hostInfos) {
            ThreadMonDetail detail = new ThreadMonDetail();
            detail.setThreadMonId(threadMon.getId());
            detail.setHostname(hostInfo.getHostname());
            detail.setProcessKeyword(threadMon.getProcessKeyword());
            detail.setStatus("0");
            detail.setCreateTime(new Date());
            detailList.add(detail);
        }
        if (!CollectionUtils.isEmpty(detailList)) {
            threadMonDetailService.saveRecord(detailList);
        }
    }


    public int countByParams(Map<String, Object> params) throws Exception {
        return threadMonMapper.countByParams(params);
    }

    @Transactional
    public int deleteById(String[] id) throws Exception {
        for (String s : id) {
            threadMonDetailService.deleteByThreadMonId(s);
            Map<String, Object> params = new HashMap<>();
            params.put("relationId", s);
            params.put("relationType", "THREAD_MON");
            tagRelationMapper.deleteByRelationIdAndRelationType(params);
        }
        return threadMonMapper.deleteById(id);
    }

    public List<ThreadMon> selectByIds(List<String> ids) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        return threadMonMapper.selectByIds(params);
    }
}