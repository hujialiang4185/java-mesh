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

import com.huawei.emergency.layout.controller.TransactionController;
import com.huawei.emergency.layout.custom.CustomMethodTestElement;
import com.huawei.emergency.layout.custom.DefaultTestElement;
import com.huawei.emergency.layout.template.GroovyClassTemplate;
import com.huawei.emergency.layout.template.GroovyMethodTemplate;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author y30010171
 * @since 2021-12-17
 **/
@Data
public class TestPlanTestElement extends ParentTestElement {

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
        if (rateTotal == 100 * allTransactional.size()) {
            // todo 顺序执行
            /*for (TransactionController controller : allTransactional) {
                testMethod.addContent(String.format(Locale.ROOT, " this.%s;", controller.invokeStr()), 2);
            }*/
        } else if (rateTotal == 100) {
            generateScheduleCode(allTransactional);
        } else {
            throw new RuntimeException("事务控制器压力分配不能超过100");
        }
        nextElements().stream()
            .filter(testElement -> testElement instanceof DefaultTestElement || testElement instanceof CustomMethodTestElement || testElement instanceof TransactionController)
            .forEach(handler -> handler.handle(context));
    }

    private void generateScheduleCode(List<TransactionController> allTransactional) {
        /*testMethod.addContent("int vusers = getVusers();", 2);
        testMethod.addContent("int runThreadNum = getRunThreadNum();", 2);
        testMethod.addContent("int preRate = 0;", 2);
        List<String> runNums = new ArrayList<>();
        for (int i = 0; i < allTransactional.size(); i++) {
            String variableName = "runNum" + i;
            testMethod.addContent(String.format(Locale.ROOT, "preRate += %s;int %s = vusers / 100 * preRate;", allTransactional.get(i).getRate(), variableName), 2);
            runNums.add(variableName);
        }
        for (int i = 0; i < runNums.size(); i++) {
            if (i == 0) {
                testMethod.addContent(String.format(Locale.ROOT, "if (runThreadNum > 0 && runThreadNum <= %s)", runNums.get(i)), 2);
                testMethod.addContent(String.format(Locale.ROOT, "this.%s;", allTransactional.get(i).invokeStr()), 3);
            } else {
                testMethod.addContent(String.format(Locale.ROOT, "else if (runThreadNum > %s && runThreadNum <= %s)", runNums.get(i - 1), runNums.get(i)), 2);
                testMethod.addContent(String.format(Locale.ROOT, "this.%s;", allTransactional.get(i).invokeStr()), 3);
            }
        }*/
        int preRate = 0;
        for (TransactionController controller : allTransactional) {
            GroovyMethodTemplate method = controller.getMethod();
            method.addContent("int vusers = getVusers();", 2);
            method.addContent("int runThreadNum = getRunThreadNum();", 2);
            method.addContent(String.format(Locale.ROOT, "int preRunNum = vusers / 100 * %s;int runNum = vusers / 100 * (%s + %s);", preRate, preRate, controller.getPresure()), 2);
            method.addContent("if (runThreadNum <= preRunNum || runThreadNum > runNum) ", 2);
            method.addContent("return;", 3);
            preRate += controller.getPresure();
        }
    }
}
