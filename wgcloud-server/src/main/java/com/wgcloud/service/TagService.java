package com.wgcloud.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.Tag;
import com.wgcloud.mapper.TagMapper;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.UUIDUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class TagService {

    @Resource
    private TagMapper tagMapper;
    @Resource
    private TagRelationService tagRelationService;

    public PageInfo<Tag> selectByParams(Map<String, Object> params, int currPage, int pageSize) throws Exception {
        PageHelper.startPage(currPage, pageSize);
        List<Tag> list = tagMapper.selectByParams(params);
        return new PageInfo<>(list);
    }

    public Tag selectById(String id) throws Exception {
        return tagMapper.selectById(id);
    }

    public void save(Tag Tag) throws Exception {
        Tag.setId(UUIDUtil.getUUID());
        Tag.setCreateTime(DateUtil.getNowTime());
        tagMapper.save(Tag);
    }

    public int updateById(Tag Tag) throws Exception {
        return tagMapper.updateById(Tag);
    }

    @Transactional
    public int deleteById(String[] ids) throws Exception {
        for (String id : ids) {
            tagRelationService.deleteByTagId(id);
        }
        return tagMapper.deleteById(ids);
    }

    public List<Tag> selectAllByParams(Map<String, Object> params) throws Exception {
        return tagMapper.selectByParams(params);
    }
}
