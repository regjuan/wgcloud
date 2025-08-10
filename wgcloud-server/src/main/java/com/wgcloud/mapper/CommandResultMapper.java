package com.wgcloud.mapper;

import com.wgcloud.entity.CommandResult;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandResultMapper {
    List<CommandResult> selectByParams(Map<String, Object> params);
    void save(CommandResult CommandResult);
    int deleteById(String[] id);
    int countByParams(Map<String, Object> params);
    int updateById(CommandResult commandResult);
}
