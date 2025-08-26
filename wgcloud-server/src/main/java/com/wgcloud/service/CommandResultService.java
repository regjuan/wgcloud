        package com.wgcloud.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wgcloud.dto.PlaybookHisDto;
import com.wgcloud.entity.Command;
import com.wgcloud.entity.CommandResult;
import com.wgcloud.entity.Playbook;
import com.wgcloud.mapper.CommandResultMapper;
import com.wgcloud.util.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class CommandResultService {

    @Resource
    private CommandResultMapper commandResultMapper;
    @Resource
    private CommandService commandService;
    @Resource
    private PlaybookService playbookService;

    public PageInfo<CommandResult> selectByParams(Map<String, Object> params, int currPage, int pageSize) throws Exception {
        PageHelper.startPage(currPage, pageSize);
        List<CommandResult> list = commandResultMapper.selectByParams(params);
        for(CommandResult commandResult : list) {
            if(!StringUtils.isEmpty(commandResult.getCommandId())) {
                Command command = commandService.selectById(commandResult.getCommandId());
                if(command != null) {
                    commandResult.setCommandName(command.getCmdName());
                }
            }
            if(!StringUtils.isEmpty(commandResult.getTaskId())) {
                Playbook playbook = playbookService.selectById(commandResult.getTaskId());
                if(playbook != null) {
                    commandResult.setTaskName(playbook.getPlaybookName());
                }
            }
        }
        return new PageInfo<>(list);
    }

    public List<PlaybookHisDto> selectHisByParams(Map<String, Object> params) throws Exception {
        return commandResultMapper.selectHisByParams(params);
    }

    public void save(CommandResult commandResult) throws Exception {
        commandResult.setId(UUIDUtil.getUUID());
        commandResultMapper.save(commandResult);
    }

    public int updateById(CommandResult commandResult) throws Exception {
        return commandResultMapper.updateById(commandResult);
    }

    public int deleteById(String[] ids) throws Exception {
        return commandResultMapper.deleteById(ids);
    }

    public int updateExpiredTasks() throws Exception {
        return commandResultMapper.updateExpiredTasks();
    }
}
