package com.wgcloud.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.Command;
import com.wgcloud.entity.Tag;
import com.wgcloud.entity.TagRelation;
import com.wgcloud.mapper.CommandMapper;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommandService {

    @Resource
    private CommandMapper commandMapper;
    @Resource
    private TagRelationService tagRelationService;
    @Resource
    private TagService tagService;

    public PageInfo<Command> selectByParams(Map<String, Object> params, int currPage, int pageSize) throws Exception {
        PageHelper.startPage(currPage, pageSize);
        List<Command> list = commandMapper.selectByParams(params);
        for (Command command : list) {
            setTagsForCommand(command);
        }
        return new PageInfo<>(list);
    }

    public Command selectById(String id) throws Exception {
        Command command = commandMapper.selectById(id);
        if (command != null) {
            setTagsForCommand(command);
        }
        return command;
    }

    @Transactional
    public void save(Command command) throws Exception {
        command.setId(UUIDUtil.getUUID());
        command.setCreateTime(DateUtil.getNowTime());
        commandMapper.save(command);
        if (!StringUtils.isEmpty(command.getTags())) {
            String[] tagIds = command.getTags().split(",");
            for (String tagId : tagIds) {
                TagRelation tagRelation = new TagRelation();
                tagRelation.setTagId(tagId);
                tagRelation.setRelationId(command.getId());
                tagRelation.setRelationType("COMMAND");
                tagRelationService.save(tagRelation);
            }
        }
    }

    @Transactional
    public int updateById(Command command) throws Exception {
        tagRelationService.deleteByRelationId(command.getId());
        if (!StringUtils.isEmpty(command.getTags())) {
            String[] tagIds = command.getTags().split(",");
            for (String tagId : tagIds) {
                TagRelation tagRelation = new TagRelation();
                tagRelation.setTagId(tagId);
                tagRelation.setRelationId(command.getId());
                tagRelation.setRelationType("COMMAND");
                tagRelationService.save(tagRelation);
            }
        }
        return commandMapper.updateById(command);
    }

    @Transactional
    public int deleteById(String[] ids) throws Exception {
        for (String id : ids) {
            tagRelationService.deleteByRelationId(id);
        }
        return commandMapper.deleteById(ids);
    }

    private void setTagsForCommand(Command command) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("relationId", command.getId());
        List<TagRelation> tagRelations = tagRelationService.selectByParams(params);
        if (tagRelations.size() > 0) {
            List<String> tagIds = new ArrayList<>();
            for (TagRelation tr : tagRelations) {
                tagIds.add(tr.getTagId());
            }
            
            Map<String, Object> tagParams = new HashMap<>();
            tagParams.put("ids", tagIds); // 假设TagMapper支持ids批量查询
            List<Tag> tags = tagService.selectAllByParams(tagParams);
            
            command.setTags(tags.stream().map(Tag::getId).collect(Collectors.joining(",")));
            command.setTagNameList(tags.stream().map(Tag::getTagName).collect(Collectors.joining(",")));
        }
    }
}
