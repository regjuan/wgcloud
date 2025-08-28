package com.wgcloud.mapper;

import com.wgcloud.entity.TagRelation;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRelationMapper {
    List<TagRelation> selectByParams(Map<String, Object> params);
    void save(TagRelation TagRelation);
    int deleteByTagId(String tagId);
    int deleteByRelationId(String relationId);
    int deleteByRelationIdAndTagId(Map<String, Object> params);
    int deleteByRelationIdAndRelationType(Map<String, Object> params);

    List<String> selectTagNamesByHostId(String hostId);
}
