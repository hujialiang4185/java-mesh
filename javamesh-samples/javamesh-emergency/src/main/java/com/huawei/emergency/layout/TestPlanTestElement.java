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

package com.huawei.emergency.layout;

import com.huawei.common.exception.ApiException;
import com.huawei.emergency.layout.controller.TransactionController;
import com.huawei.emergency.layout.custom.CustomMethodTestElement;
import com.huawei.emergency.layout.custom.DefaultTestElement;
import com.huawei.emergency.layout.template.GroovyMethodTemplate;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 测试元件-根节点
 *
 * @author y30010171
 * @since 2021-12-17
 **/
@Data
public class TestPlanTestElement extends ParentTestElement {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestPlanTestElement.class);
    private static final int ONE_HUNDRED = 100;

    private String agent; // 代理数
    private String vuser; // 虚拟用户数
    private List<String> hosts;
    private String basic; // 测试类型
    private int byCount; // 测试次数
    private int byTimeH;
    private int byTimeM;
    private int byTimeS;
    private int samplingInterval; // 采样间隔
    private int samplingIgnore; // 忽略采样数
    private String testParam; // 测试参数
    private boolean isSafe; // 是否安全文件分发
    private boolean isIncreased; // 是否压力递增
    private String concurrency = "线程"; // 并发量
    private int initValue; // 初始数
    private int increment; // 增量
    private int initWait; // 初始等待时间ms
    private int growthInterval; // 进程增长间隔ms
    private boolean isMonitor = true;
    private List<String> jvmMonitor = Arrays.asList("GC", "Thread", "Memory", "ClassLoading", "MemoryPool", "CPU");

    @Override
    public void handle(ElementProcessContext context) {
        List<TransactionController> allTransactional = nextElements().stream()
            .filter(handler -> handler instanceof TransactionController)
            .map(handler -> (TransactionController) handler)
            .collect(Collectors.toList());
        int rateTotal = allTransactional.stream()
            .mapToInt(TransactionController::getPresure)
            .sum();
        if (rateTotal == ONE_HUNDRED * allTransactional.size()) {
            LOGGER.info("all pressure equals {}*{}", ONE_HUNDRED, allTransactional.size());
        } else if (rateTotal == ONE_HUNDRED) {
            generateScheduleCode(allTransactional);
        } else {
            throw new ApiException("事务控制器压力分配不能超过100");
        }
        nextElements().stream()
            .filter(testElement -> testElement instanceof DefaultTestElement
                || testElement instanceof CustomMethodTestElement || testElement instanceof TransactionController)
            .forEach(handler -> handler.handle(context));
    }

    private void generateScheduleCode(List<TransactionController> allTransactional) {
        int preRate = 0;
        for (TransactionController controller : allTransactional) {
            GroovyMethodTemplate method = controller.methodTemplate();
            method.addContent("int vusers = getVusers();", 2);
            method.addContent("int runThreadNum = getRunThreadNum();", 2);
            method.addContent(
                String.format(Locale.ROOT, "int preRunNum = vusers / 100 * %s;int runNum = vusers / 100 * (%s + %s);",
                    preRate, preRate, controller.getPresure()), 2);
            method.addContent("if (runThreadNum <= preRunNum || runThreadNum > runNum) ", 2);
            method.addContent("return;", 3);
            preRate += controller.getPresure();
        }
    }
}
