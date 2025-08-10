package com.wgcloud.service;

import cn.hutool.json.JSONUtil;
import com.wgcloud.dto.TaskStep;
import com.wgcloud.entity.CommandResult;
import com.wgcloud.entity.Playbook;
import com.wgcloud.entity.SystemInfo;
import com.wgcloud.entity.TagRelation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class CommandRunService {

    private static final Logger logger = LoggerFactory.getLogger(CommandRunService.class);

    @Resource
    private CommandResultService commandResultService;
    @Resource
    private TagRelationService tagRelationService;
    @Resource
    private SystemInfoService systemInfoService;

    /**
     * 它开始遍历 playbook 的 taskSteps 列表这里是所有的待处理指令
     * 从tag 获取指定的目标
     * CommandResult 会存储 C-T的笛卡尔积
     * @param playbook
     */
    public void runTask(Playbook playbook) {
        try {
            if (playbook == null || StringUtils.isEmpty(playbook.getTaskSteps())) {
                return;
            }

            List<TaskStep> taskSteps = JSONUtil.parseArray(playbook.getTaskSteps()).toList(TaskStep.class);

            for (TaskStep step : taskSteps) {
                if (step.getTargets() == null || step.getTargets().isEmpty()) {
                    continue;
                }

                Set<String> targetHostIds = new HashSet<>();

                if ("TAG".equals(step.getTargetType())) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("tagIds", step.getTargets());
                    List<TagRelation> relations = tagRelationService.selectByParams(params);
                    for (TagRelation relation : relations) {
                        if("HOST".equals(relation.getRelationType())){
                           targetHostIds.add(relation.getRelationId());
                        }
                    }
                } else {
                    targetHostIds.addAll(step.getTargets());
                }

                for (String hostId : targetHostIds) {
                    SystemInfo systemInfo = systemInfoService.selectById(hostId);
                    if (systemInfo == null) {
                        continue;
                    }

                    CommandResult commandResult = new CommandResult();
                    commandResult.setTaskId(playbook.getId());
                    commandResult.setCommandId(step.getCommandId()); // 使用当前步骤的commandId
                    commandResult.setHostname(systemInfo.getHostname());
                    commandResult.setStatus("PENDING");
                    commandResult.setStartTime(new Date());
                    commandResultService.save(commandResult);
                    logger.info("已为任务[{}]的步骤[{}]在主机[{}]上生成待执行指令", playbook.getPlaybookName(), step.getStepName(), systemInfo.getHostname());
                }
            }
        } catch (Exception e) {
            logger.error("执行任务失败：{}", playbook.getPlaybookName(), e);
        }
    }
}