package com.wgcloud.service;

import com.wgcloud.entity.TagRelation;
import com.wgcloud.mapper.TagRelationMapper;
import com.wgcloud.util.UUIDUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class TagRelationService {

    @Resource
    private TagRelationMapper tagRelationMapper;

    public List<TagRelation> selectByParams(Map<String, Object> params) throws Exception {
        return tagRelationMapper.selectByParams(params);
    }

    public void save(TagRelation tagRelation) throws Exception {
        tagRelation.setId(UUIDUtil.getUUID());
        tagRelationMapper.save(tagRelation);
    }

    public int deleteByTagId(String tagId) throws Exception {
        return tagRelationMapper.deleteByTagId(tagId);
    }

    public int deleteByRelationId(String relationId) throws Exception {
        return tagRelationMapper.deleteByRelationId(relationId);
    }

    public int deleteByRelationIdAndTagId(Map<String, Object> params) throws Exception {
        return tagRelationMapper.deleteByRelationIdAndTagId(params);
    }

    public int deleteByRelationIdAndRelationType(Map<String, Object> params) throws Exception {
        return tagRelationMapper.deleteByRelationIdAndRelationType(params);
    }
}
