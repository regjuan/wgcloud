package com.wgcloud.mapper;

import com.wgcloud.entity.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public interface TagMapper {
    List<Tag> selectByParams(Map<String, Object> params);
    Tag selectById(String id);
    Tag selectByName(String tagName);
    void save(Tag tag);
    int updateById(Tag tag);
    int deleteById(String[] id);
    int countByParams(Map<String, Object> params);
}
