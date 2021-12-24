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
import com.huawei.emergency.layout.template.GroovyClassTemplate;
import com.huawei.emergency.layout.template.GroovyMethodTemplate;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

/**
 * @author y30010171
 * @since 2021-12-17
 **/
@Data
public class TestPlanTestElement implements ParentTestElement {

    private String name;
    private String comment;
    private List<TestElement> testElements = new ArrayList<>();

    @Override
    public void handle(HandlerContext context) {
        List<TransactionController> allTransactional = testElements.stream()
            .filter(handler -> handler instanceof TransactionController)
            .map(handler -> (TransactionController) handler)
            .collect(Collectors.toList());
        GroovyClassTemplate template = context.getTemplate();
        GroovyMethodTemplate testMethod = template.getTestMethod();
        if (testMethod == null) {
            testMethod = new GroovyMethodTemplate().start("public void test() {", 1)
                .end("}", 1)
                .addAnnotation("    @Test");
            template.addMethod(testMethod);
        }
        int rateTotal = allTransactional.stream()
            .mapToInt(TransactionController::getRate)
            .sum();
        if (rateTotal == 100) {
            generateScheduleCode(testMethod,allTransactional);
        } else if (rateTotal == 100 * allTransactional.size()) {
            // todo 顺序执行
            for (TransactionController controller : allTransactional) {
                testMethod.addContent(String.format(Locale.ROOT, " this.%s;", controller.invokeStr()), 2);
            }
        } else {
            throw new RuntimeException("事务控制器压力分配不能超过100");
        }
        testElements.forEach(handler -> {
            context.setCurrentMethod(template.getTestMethod());
            handler.handle(context);
        });
    }

    @Override
    public List<TestElement> nextElements() {
        return testElements;
    }

    private void generateScheduleCode(@NotNull GroovyMethodTemplate testMethod,@NotNull List<TransactionController> allTransactional) {
        testMethod.addContent("int vusers = getVusers();", 2);
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
        }
    }
}
