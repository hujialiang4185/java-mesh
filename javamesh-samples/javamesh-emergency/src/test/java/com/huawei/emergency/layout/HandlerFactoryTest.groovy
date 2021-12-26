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

class HandlerFactoryTest extends GroovyTestCase{

    void "test HandlerFactory"() {
        Map<String,Object> variables = new HashMap<>()
        variables.put("title","事务1")
        variables.put("comment","第一个事务")
        def handler = HandlerFactory.getHandler("TransactionController", variables)
        assertTrue(handler instanceof TransactionController)
        def transactional = (TransactionController) handler
        assertEquals("事务1", transactional.title)
        assertEquals("第一个事务", transactional.comment)
        println(this.class.getMethod("test HandlerFactory"))
    }
}

