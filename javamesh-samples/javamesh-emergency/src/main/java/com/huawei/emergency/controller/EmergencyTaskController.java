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

package com.huawei.emergency.controller;

import com.huawei.common.api.CommonResult;
import com.huawei.emergency.dto.TaskCommonReport;
import com.huawei.emergency.dto.TaskJvmReport;
import com.huawei.emergency.dto.TaskResourceReport;
import com.huawei.emergency.dto.TaskServiceReport;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author y30010171
 * @since 2022-01-24
 **/
@RestController
@RequestMapping("/api/task")
public class EmergencyTaskController {

    private static Random usageRandom = new Random(100);
    private static List<String> mockTestIp = Arrays.asList("127.0.0.1", "0.0.0.0");
    private static TaskCommonReport mockCommonReport = new TaskCommonReport();
    private static TaskResourceReport mockResourceReport = new TaskResourceReport();
    private static TaskJvmReport mockJvmReport = new TaskJvmReport();
    private static TaskServiceReport mockServiceReport = new TaskServiceReport();

    /**
     * 获取任务执行的基本信息
     *
     * @param testId 测试id
     * @return {@link CommonResult}
     */
    @GetMapping("/view")
    public CommonResult<TaskCommonReport> getCommonReport(@RequestParam("test_id") Integer testId) {
        return CommonResult.success(mockCommon());
    }

    /**
     * 获取任务执行的服务器IP地址
     *
     * @param testId 测试id
     * @return {@link CommonResult}
     */
    @GetMapping("/search/ip")
    public CommonResult<List<String>> getTestIp(@RequestParam("test_id") Integer testId) {
        return CommonResult.success(mockTestIp);
    }

    /**
     * 获取任务执行所在服务器下的资源消耗
     *
     * @param testId 测试id
     * @return {@link CommonResult}
     */
    @GetMapping("/resource")
    public CommonResult<TaskResourceReport> getResourceReport(@RequestParam("test_id") Integer testId, @RequestParam(value = "ip", required = false) String ip) {
        return CommonResult.success(mockResource(ip));
    }

    /**
     * 获取任务执行所在服务器下agent的Jvm消耗
     *
     * @param testId 测试id
     * @return {@link CommonResult}
     */
    @GetMapping("/jvm")
    public CommonResult<TaskJvmReport> getJvmReport(@RequestParam("test_id") Integer testId, @RequestParam(value = "ip", required = false) String ip) {
        return CommonResult.success(mockJvm(ip));
    }

    @GetMapping("/service")
    public CommonResult<List<TaskServiceReport>> getServiceReport(@RequestParam("test_id") Integer testId) {
        return CommonResult.success(Arrays.asList(mockService()));
    }

    public TaskCommonReport mockCommon() {
        mockCommonReport.setTestName("对性能测试系统的压测");
        mockCommonReport.setStatus("running");
        mockCommonReport.setStatusLabel("运行中");
        mockCommonReport.setLabel(Arrays.asList("性能测试", "功能自测", "压力测试"));
        mockCommonReport.setDesc("测试数据为mock数据");
        mockCommonReport.setDuration("6:06");
        mockCommonReport.setVuser(66);
        mockCommonReport.setTps(666D);
        mockCommonReport.setTpsPeak(777D);
        mockCommonReport.setAvgTime(16D);
        mockCommonReport.setTestCount(243756);
        mockCommonReport.setSuccessCount(243700);
        mockCommonReport.setFailCount(56);
        mockCommonReport.setLogName(Arrays.asList("log-1.zip", "log-2.zip"));
        mockCommonReport.setProgressMessage(Arrays.asList("start", "running", "end"));
        return mockCommonReport;
    }

    public TaskResourceReport mockResource(String ip) {
        mockResourceReport.setIp(StringUtils.isEmpty(ip) ? mockTestIp.stream().findAny().get() : ip);
        mockResourceReport.setCpu(4);
        mockResourceReport.setMemory(7.9D);
        mockResourceReport.setStartUp(24.06D);
        mockResourceReport.setCpuUsage(usageRandom.nextDouble());
        mockResourceReport.setMemoryUsage(usageRandom.nextDouble());
        mockResourceReport.setIoBusy(usageRandom.nextDouble());
        mockResourceReport.setCpuUser(usageRandom.nextDouble());
        mockResourceReport.setCpuSys(usageRandom.nextDouble());
        mockResourceReport.setCpuWait(usageRandom.nextDouble());
        mockResourceReport.setCpuIdle(usageRandom.nextDouble());
        mockResourceReport.setMemoryTotal(usageRandom.nextDouble());
        mockResourceReport.setMemoryBuffers(usageRandom.nextDouble());
        mockResourceReport.setMemorySwap(usageRandom.nextDouble());
        mockResourceReport.setMemoryUsed(usageRandom.nextDouble());
        mockResourceReport.setDiskRead(usageRandom.nextDouble());
        mockResourceReport.setDiskWrite(usageRandom.nextDouble());
        mockResourceReport.setDiskBusy(usageRandom.nextDouble());
        mockResourceReport.setNetworkRbyte(usageRandom.nextDouble());
        mockResourceReport.setNetworkWbyte(usageRandom.nextDouble());
        mockResourceReport.setMemoryRpackage(usageRandom.nextDouble());
        mockResourceReport.setMemoryWpackage(usageRandom.nextDouble());
        return mockResourceReport;
    }

    public TaskJvmReport mockJvm(String ip) {
        mockJvmReport.setIp(StringUtils.isEmpty(ip) ? mockTestIp.stream().findAny().get() : ip);
        mockJvmReport.setCpuJava(usageRandom.nextDouble());
        mockJvmReport.setHeapInit(usageRandom.nextDouble());
        mockJvmReport.setHeapMax(usageRandom.nextDouble());
        mockJvmReport.setHeapUsed(usageRandom.nextDouble());
        mockJvmReport.setHeapCommitted(usageRandom.nextDouble());
        mockJvmReport.setMemoryInit(usageRandom.nextDouble());
        mockJvmReport.setMemoryMax(usageRandom.nextDouble());
        mockJvmReport.setMemoryUsed(usageRandom.nextDouble());
        mockJvmReport.setMemoryCommitted(usageRandom.nextDouble());
        mockJvmReport.setJvmCache(usageRandom.nextDouble());
        mockJvmReport.setJvmNewgen(usageRandom.nextDouble());
        mockJvmReport.setJvmOldgen(usageRandom.nextDouble());
        mockJvmReport.setJvmSurvivor(usageRandom.nextDouble());
        mockJvmReport.setJvmPenmgen(usageRandom.nextDouble());
        mockJvmReport.setJvmMetaspace(usageRandom.nextDouble());
        mockJvmReport.setGcNewc(12);
        mockJvmReport.setGcOldc(0);
        mockJvmReport.setGcNews(usageRandom.nextDouble());
        mockJvmReport.setGcOlds(usageRandom.nextDouble());
        mockJvmReport.setThreadCount(66);
        mockJvmReport.setThreadDaemon(3);
        mockJvmReport.setThreadPeak(72);
        return mockJvmReport;
    }

    public TaskServiceReport mockService() {
        mockServiceReport.setTransaction("事务1");
        mockServiceReport.setResponseMs(16);
        mockServiceReport.setTps(650);
        mockServiceReport.setSuccessCount(1988);
        mockServiceReport.setFailCount(12);
        mockServiceReport.setFailRate("0.6");
        return mockServiceReport;
    }
}
