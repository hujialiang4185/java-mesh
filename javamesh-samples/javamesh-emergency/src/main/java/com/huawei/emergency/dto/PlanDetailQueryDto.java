/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.emergency.dto;

import com.huawei.emergency.entity.EmergencyServer;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用于描述预案下的任务明细信息
 *
 * @author y30010171
 * @since 2021-11-10
 **/
@Data
public class PlanDetailQueryDto {
    private Integer key;
    private String sceneNo;
    private String sceneName;
    private String scenaNo;
    private String scenaName;
    private String taskNo;
    private String taskName;
    private String subtaskNo;
    private String subtaskName;
    private String channelType;
    private String scriptName;
    private String submitInfo;
    private String serverId;
    private Long perfTestId; // 压测任务ID
    private Long testId;
    private Integer vuser;
    private List<EmergencyServer> serverList;
    private String userId; // 压测owner
    private Date startTime; // 压测开始时间
    private Long duration; // 压测持续时间
    private Double tps; // 压测TPS
    private Double meanTestTime; // 压测mtt
    private Double errorRate; // 出错率
    private String tagString; // 压测任务标签
    private String status; // 压测任务状态
}
