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

package com.huawei.emergency.layout.sampler

import com.huawei.emergency.layout.ElementProcessContext
import com.huawei.emergency.layout.TestPlanTestElement
import com.huawei.emergency.layout.controller.TransactionController
import com.huawei.emergency.layout.template.GroovyClassTemplate

class HttpSamplerTest extends GroovyTestCase {


    void "test when get"() {
        def planHandler = new TestPlanTestElement(testElements: [
                new TransactionController(title: "事务控制器1", rate: 20),
                new TransactionController(title: "事务控制器2", rate: 30),
                new TransactionController(title: "事务控制器3", rate: 50, testElements:
                        [new HttpSampler(title: "测试",serviceName: "127.0.0.1",port: 9093,method: "Get",path: "/argus-emergency/api/plan")]
                )
        ]);
        def context = new ElementProcessContext(template: GroovyClassTemplate.template())
        planHandler.handle(context)
        context.getTemplate().print(System.out)
    }

    void "test when post"() {
    }

    void "test when put"() {
    }

    void "test when delete"() {
    }

    void "test when head"() {
    }

    void "test when options"() {
    }

    void "test when trace"() {
    }
}
