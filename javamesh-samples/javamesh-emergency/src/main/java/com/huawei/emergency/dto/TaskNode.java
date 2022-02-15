/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.emergency.dto;

import lombok.Data;
import net.grinder.common.GrinderProperties;
import org.ngrinder.model.MonitoringHost;
import org.ngrinder.model.PerfScene;
import org.ngrinder.model.PerfTest;
import org.ngrinder.model.Status;
import org.ngrinder.model.Tag;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * 用于描述预案修改下的每个任务节点
 *
 * @author y30010171
 * @since 2021-11-09
 **/
@Data
public class TaskNode {
    private Integer key;
    private String title;
    private String taskNo;
    private String taskName;
    private String channelType;
    private Integer scriptId;
    private String scriptName;
    private Integer[] serviceId;
    private String submitInfo;
    private String sync;
    private List<TaskNode> children;

    /**
     * 查询执行记录使用
     */
    private Integer planId;
    private Integer sceneId;
    private Integer taskId;
    private String createUser;


    private String taskType;
    private String testName;
    private String tagString;
    private String description;
    private Status status;
    private Integer ignoreSampleCount;
    private Date scheduledTime;
    private Date startTime;
    private Date finishTime;
    private String targetHosts;
    private Boolean sendMail;
    private Boolean useRampUp;
    private String rampUpType;
    private String threshold;
    //private String scriptName;
    private Long duration;
    private Integer runCount;
    private Integer agentCount;
    private Integer vuserPerAgent;
    private Integer processes;
    private Integer rampUpInitCount;
    private Integer rampUpInitSleepTime;
    private Integer rampUpStep;
    private Integer rampUpIncrementInterval;
    private Integer threads;
    private Long tests;
    private Long errors;
    private Double meanTestTime;
    private Double testTimeStandardDeviation;
    private Double tps;
    private Double peakTps;
    private Integer port;
    private Status testErrorCause;
    private String distributionPath;
    private String progressMessage;
    private String lastProgressMessage;
    private String testComment;
    private Long scriptRevision;
    private Boolean stopRequest;
    private String region;
    private Boolean safeDistribution;
    private String dateString;
    private GrinderProperties grinderProperties;
    private SortedSet<Tag> tags;
    private String runningSample;
    private String agentState;
    private String monitorState;
    private Integer samplingInterval;
    private String param;
    private Set<MonitoringHost> monitoringHosts = new HashSet();
    private PerfScene perfScene;
    private String agentIds;
    private Long perfTestReportId;
    private String userId;

    public PerfTest translate() {
        PerfTest test = new PerfTest();
        BeanUtils.copyProperties(this,test);
        return test;
    }
}
