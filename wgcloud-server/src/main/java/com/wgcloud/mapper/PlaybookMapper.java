package com.wgcloud.mapper;

import com.wgcloud.entity.Playbook;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PlaybookMapper {

    List<Playbook> selectByParams(Map<String, Object> params);

    Playbook selectById(String id);

    void save(Playbook playbook);

    int updateById(Playbook playbook);

    int deleteById(String[] id);
}