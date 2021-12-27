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

package com.huawei.emergency.layout


import com.huawei.emergency.layout.controller.TransactionController
import com.huawei.emergency.layout.template.GroovyClassTemplate
import com.huawei.test.postprocessor.config.RegularExtractorConfig
import com.huawei.test.postprocessor.impl.RegularExpressionExtractor
import org.junit.Test

class TestPlanHandlerTest extends GroovyTestCase {

    void "test when 100 total"() {
        def planHandler = new TestPlanTestElement(testElements: [
                new TransactionController(title: "事务控制器1", rate: 20),
                new TransactionController(title: "事务控制器2", rate: 30),
                new TransactionController(title: "事务控制器3", rate: 50)]);
        def context = new ElementProcessContext(template: GroovyClassTemplate.template())
        planHandler.handle(context)
        context.getTemplate().print(System.out)
    }

    void "test when n*100"() {
        def controller1 = new TransactionController(title: "事务控制器1")
        def controller2 = new TransactionController(title: "事务控制器2")
        def controller3 = new TransactionController(title: "事务控制器3")
        def planHandler = new TestPlanTestElement(testElements: [controller1, controller2, controller3]);
        def context = new ElementProcessContext(template: GroovyClassTemplate.template())
        planHandler.handle(context)
        context.getTemplate().print(System.out)
    }

    void "test planHandler which was provided by Web"() {
        TreeResponseTest test = new TreeResponseTest();
        test.before();
        def context = new ElementProcessContext(template: GroovyClassTemplate.template())
        TreeResponse.parse(test.planTree).handle(context)
        context.template.print(System.out)
    }

    void "test"() {
        def extract = new RegularExpressionExtractor().extract("Zzoo",new RegularExtractorConfig(new RegularExtractorConfig.Builder(regularExpression: "zoo", groupIndex: 0, matchIndex: 0, defaultValue: "Hello")))
        println (extract)
    }
}
