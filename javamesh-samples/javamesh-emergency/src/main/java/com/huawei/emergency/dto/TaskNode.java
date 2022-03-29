/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.emergency.dto;

import com.huawei.emergency.entity.EmergencyServer;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.commons.lang.StringUtils;
import org.ngrinder.model.PerfTest;
import org.ngrinder.model.RampUp;
import org.ngrinder.model.Status;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

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
    private List<EmergencyServer> serviceId;
    private String submitInfo;
    private String sync;
    private List<TaskNode> children;
    private String scenaDesc;

    /**
     * 查询执行记录使用
     */
    private Integer planId;
    private Integer sceneId;
    private Integer taskId;
    private String createUser;

    private String taskType;
    /**
     * 压测任务
     */
    private Long testId;
    private String guiScriptName;
    private Integer perfTestId;
    private String testName; // 测试名称
    private List<String> label; // 标签
    private String desc; // 描述
    private Integer agent; // agent数量
    private Integer vuser; // 线程数
    private String scenarioName; // 场景名称
    private List<HostsDTO> hosts; // 监控主机
    private Integer byTimeH; // 测试时长 小时数
    private Integer byTimeM; // 分钟数
    private Integer byTimeS; // 测试时长 秒数
    private Integer byCount; // 测试次数
    private String basic; // 测试时长 by_count by_time
    private Integer samplingInterval = 2; // 采样间隔
    private Integer samplingIgnore; // 采样忽略数
    private boolean isSafe; // 是否安全文件分发
    private String testParam; // 测试参数
    private boolean isIncreased; // 开启压力递增
    private String concurrency; // 并发量
    private Integer initValue; // 初始化值
    private Integer increment; // 增量
    private Integer initWait; // 初始等待时间
    private Integer growthInterval; // 增长时间

    @NoArgsConstructor
    @Data
    public static class HostsDTO {
        private String domain;
        private String ip;
    }

    public PerfTest parse() {
        PerfTest perfTest = new PerfTest();
        perfTest.setTestName(StringUtils.isEmpty(this.getTestName()) ? this.getTaskName() : this.getTestName());
        perfTest.setAgentCount(this.getServiceId() == null ? 0 : this.getServiceId().size());
        if (this.getVuser() != null) {
            perfTest.setProcesses(getProcessCount(this.getVuser()));
            perfTest.setThreads(this.getVuser() / perfTest.getProcesses());
            perfTest.setVuserPerAgent(this.getVuser());
        }
        perfTest.setTagString(StringUtils.join(this.getLabel(), ","));
        perfTest.setDescription(this.getDesc());
        perfTest.setTargetHosts(targetHostStr());
        perfTest.setThreshold("by_time".equals(this.getBasic()) ? "D" : "R");
        perfTest.setRunCount(this.getByCount());
        perfTest.setDuration(getDurationTime());
        perfTest.setIgnoreSampleCount(this.getSamplingIgnore());
        perfTest.setSamplingInterval(this.getSamplingInterval());
        perfTest.setSafeDistribution(this.isSafe());
        perfTest.setParam(this.getTestParam());
        perfTest.setUseRampUp(this.isIncreased());
        perfTest.setRampUpType("线程".equals(this.getConcurrency()) ? RampUp.THREAD : RampUp.PROCESS);
        perfTest.setRampUpInitCount(this.getInitValue());
        perfTest.setRampUpStep(this.getIncrement());
        perfTest.setRampUpInitSleepTime(this.getInitWait());
        perfTest.setRampUpIncrementInterval(this.getGrowthInterval());
        perfTest.setStatus(Status.SAVED);
        perfTest.setCreatedDate(new Date());
        return perfTest;
    }

    @JsonIgnore
    private String targetHostStr() {
        StringJoiner hostStrJoiner = new StringJoiner(",");
        if (this.getHosts() != null && this.getHosts().size() > 0) {
            for (HostsDTO host : this.getHosts()) {
                if (host == null) {
                    continue;
                }
                StringBuilder hostStr =
                    new StringBuilder(StringUtils.isNotEmpty(host.getDomain()) ? host.getDomain() : host.getIp());
                if (StringUtils.isNotEmpty(host.getDomain()) && StringUtils.isNotEmpty(host.getIp())) {
                    hostStr.append(":").append(host.getIp());
                }
                hostStrJoiner.add(hostStr.toString());
            }
        }
        return hostStrJoiner.toString();
    }

    @JsonIgnore
    private long getDurationTime() {
        long timeH = this.getByTimeH() == null ? 0 : this.getByTimeH();
        long timeM = this.getByTimeM() == null ? 0 : this.getByTimeM();
        long timeS = this.getByTimeS() == null ? 0 : this.getByTimeS();
        return ((timeH * 60L + timeM) * 60L + timeS) * 1000L;
    }

    @JsonIgnore
    private int getProcessCount(int total) {
        if (total < 2) {
            return 1;
        }
        int processCount = 2;
        if (total > 80) {
            processCount = (total / 40) + 1;
        }
        if (processCount > 10) {
            processCount = 10;
        }
        return processCount;
    }
}
