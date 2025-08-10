package com.wgcloud.mapper;

import com.wgcloud.entity.Command;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandMapper {
    List<Command> selectByParams(Map<String, Object> params);
    Command selectById(String id);
    void save(Command command);
    int updateById(Command command);
    int deleteById(String[] id);
    int countByParams(Map<String, Object> params);
}
