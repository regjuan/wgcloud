package com.wgcloud.service;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wgcloud.dto.TaskStep;
import com.wgcloud.entity.Playbook;
import com.wgcloud.mapper.PlaybookMapper;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class PlaybookService {

    @Autowired
    private PlaybookMapper playbookMapper;

    public PageInfo<Playbook> selectByParams(Map<String, Object> params, int currPage, int pageSize) throws Exception {
        PageHelper.startPage(currPage, pageSize);
        List<Playbook> list = playbookMapper.selectByParams(params);
        // Deserialize taskSteps JSON to taskStepList for each playbook
        for (Playbook playbook : list) {
            if (!StringUtils.isEmpty(playbook.getTaskSteps())) {
                playbook.setTaskStepList(JSONUtil.toList(JSONUtil.parseArray(playbook.getTaskSteps()), TaskStep.class));
            }
        }
        return new PageInfo<>(list);
    }

    public Playbook selectById(String id) throws Exception {
        Playbook playbook = playbookMapper.selectById(id);
        if (playbook != null && !StringUtils.isEmpty(playbook.getTaskSteps())) {
            playbook.setTaskStepList(JSONUtil.toList(JSONUtil.parseArray(playbook.getTaskSteps()), TaskStep.class));
        }
        return playbook;
    }

    @Transactional
    public void save(Playbook playbook) throws Exception {
        // Serialize taskStepList to taskSteps JSON string
        if (playbook.getTaskStepList() != null) {
            playbook.setTaskSteps(JSONUtil.toJsonStr(playbook.getTaskStepList()));
        }
        playbook.setId(UUIDUtil.getUUID());
        playbook.setCreateTime(DateUtil.getNowTime());
        playbookMapper.save(playbook);
    }

    @Transactional
    public void updateById(Playbook playbook) throws Exception {
        // Serialize taskStepList to taskSteps JSON string
        if (playbook.getTaskStepList() != null) {
            playbook.setTaskSteps(JSONUtil.toJsonStr(playbook.getTaskStepList()));
        }
        playbookMapper.updateById(playbook);
    }

    @Transactional
    public int deleteById(String[] id) throws Exception {
        return playbookMapper.deleteById(id);
    }
}