package com.wgcloud.dto;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * @author Gemini
 * @date 2025-08-26
 * @description Playbook执行历史记录的DTO
 */
@Data
public class PlaybookHisDto {

    private String playbookId;

    private String playbookName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    private Integer totalSteps = 0;

    private Integer successSteps = 0;

    private Integer failedSteps = 0;

    private Integer pendingSteps = 0;

    private Integer runningSteps = 0;

    private Integer timeoutSteps = 0;

    /**
     * 综合状态，根据各步骤状态计算得出
     */
    private String overallStatus;

    public String getOverallStatus() {
        if (failedSteps > 0) {
            return "FAILED";
        }
        if (runningSteps > 0) {
            return "RUNNING";
        }
        if (pendingSteps > 0) {
            return "PENDING";
        }
        if (timeoutSteps > 0 ) {
            return "TIMEOUT";
        }
        if (totalSteps > 0 && totalSteps.equals(successSteps)) {
            return "SUCCESS";
        }
        return "UNKNOWN";
    }
}
