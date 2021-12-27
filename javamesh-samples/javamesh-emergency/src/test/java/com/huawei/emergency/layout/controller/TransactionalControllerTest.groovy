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

package com.huawei.emergency.layout.controller

import com.huawei.emergency.layout.ElementProcessContext
import com.huawei.emergency.layout.template.GroovyClassTemplate
import org.junit.Test

class TransactionalControllerTest extends GroovyTestCase {

    @Test
    void "test controller"() {
        GroovyClassTemplate template = GroovyClassTemplate
                .create("D:\\IdeaProject\\hercules-server\\src\\main\\resources\\GroovyTemplate.groovy");
        ElementProcessContext context = new ElementProcessContext();
        context.setTemplate(template);
        TransactionController controller = new TransactionController();
        controller.setTitle("事务1号");
        controller.handle(context);
        context.getTemplate().print(System.out);
        assertNull(template)
    }
}