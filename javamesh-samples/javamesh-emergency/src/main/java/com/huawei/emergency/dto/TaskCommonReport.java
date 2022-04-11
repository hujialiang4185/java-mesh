/*
 * Copyright (C) Ltd. 2021-2021. Huawei Technologies Co., All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.emergency.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.ngrinder.model.PerfTest;
import org.ngrinder.model.Tag;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务执行报告
 *
 * @author y30010171
 * @since 2022-01-24
 **/
@NoArgsConstructor
@Data
public class TaskCommonReport {
    private static final long ONE_THOUSAND = 1000L;
    /**
     * 测试名称
     */
    private String testName;
    /**
     * 状态值
     */
    private String status;
    /**
     * 状态标签
     */
    private String statusLabel;
    /**
     * 标签集合
     */
    private List<String> label;
    /**
     * 描述
     */
    private String desc;
    /**
     * 运行时间
     */
    private Long duration;
    /**
     * 虚拟用户数
     */
    private Integer vuser;
    /**
     * TPS
     */
    private Double tps;
    /**
     * TPS峰值
     */
    private Double tpsPeak;
    /**
     * 平均时间
     */
    private Double avgTime;

    private Double responseTime25;
    private Double responseTime50;
    private Double responseTime75;
    private Double responseTime90;
    private Double responseTime95;
    private Double responseTime99;
    /**
     * 测试数量
     */
    private Long testCount;
    /**
     * 测试成功数量
     */
    private Long successCount;
    /**
     * 测试失败数量
     */
    private Long failCount;
    /**
     * 测试备注
     */
    private String testComment;
    /**
     * 日志文件集合
     */
    private List<String> logName;
    /**
     * 执行日志
     */
    private List<String> progressMessage;

    /**
     * 插件
     */
    private List plugins;

    private Integer serverId;

    private String serverName;

    private String serverIp;

    private Integer testId;

    /**
     * 压测明细
     */
    private List<TaskCommonReport> taskDetails;

    public static TaskCommonReport parse(PerfTest perfTest) {
        TaskCommonReport commonReport = new TaskCommonReport();
        commonReport.setTestName(perfTest.getTestName() == null ? "null" : perfTest.getTestName());
        commonReport.setStatus(perfTest.getStatus().getIconName());
        commonReport.setStatusLabel(perfTest.getStatus().getIconName());
        commonReport.setLabel(perfTest.getTags()
            == null ? null : perfTest.getTags().stream().map(Tag::getTagValue).collect(Collectors.toList()));
        commonReport.setDesc(perfTest.getDescription());
        commonReport.setDuration(perfTest.getDuration());
        if (perfTest.getFinishTime() != null && perfTest.getStartTime() != null) {
            commonReport.setDuration(
                (perfTest.getFinishTime().getTime() - perfTest.getStartTime().getTime()) / ONE_THOUSAND);
        }
        commonReport.setVuser(perfTest.getVuserPerAgent());
        commonReport.setTps(perfTest.getTps());
        commonReport.setTpsPeak(perfTest.getPeakTps());
        commonReport.setAvgTime(perfTest.getMeanTestTime());
        commonReport.setResponseTime25(perfTest.getResponseTimePercent25());
        commonReport.setResponseTime50(perfTest.getResponseTimePercent50());
        commonReport.setResponseTime75(perfTest.getResponseTimePercent75());
        commonReport.setResponseTime90(perfTest.getResponseTimePercent90());
        commonReport.setResponseTime95(perfTest.getResponseTimePercent95());
        commonReport.setResponseTime99(perfTest.getResponseTimePercent99());
        commonReport.setTestCount((perfTest.getTests() == null ? 0 : perfTest.getTests()) + (
            perfTest.getErrors() == null ? 0 : perfTest.getErrors()));
        commonReport.setSuccessCount(perfTest.getTests());
        commonReport.setFailCount(perfTest.getErrors());
        commonReport.setTestComment(perfTest.getTestComment());
        commonReport.setProgressMessage(perfTest.getProgressMessage()
            == null ? null
            : Arrays.stream(perfTest.getProgressMessage().split(System.lineSeparator())).collect(Collectors.toList()));
        return commonReport;
    }
}
