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

import com.alibaba.fastjson.JSONObject
import com.huawei.emergency.layout.controller.TransactionController
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4)
class TreeResponseTest extends GroovyTestCase {

    TreeResponse planTree;

    @Before
    void before() {
        planTree = JSONObject.parseObject("{\n" +
                "    \"map\": {\n" +
                "        \"1\": {\n" +
                "            \"name\": \"测试计划1\",\n" +
                "            \"comment\": \"第一个测试计划\"\n" +
                "        },\n" +
                "        \"2\": {\n" +
                "            \"name\": \"事务1\",\n" +
                "            \"rate\": 50,\n" +
                "            \"comment\": \"第1个事务\"\n" +
                "        },\n" +
                "        \"3\": {\n" +
                "            \"name\": \"事务2\",\n" +
                "            \"rate\": 50,\n" +
                "            \"comment\": \"第2个事务\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"tree\": {\n" +
                "        \"children\": [\n" +
                "            {\n" +
                "                \"key\": \"2\",\n" +
                "                \"type\": \"transactional_controller\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"key\": \"3\",\n" +
                "                \"type\": \"transactional_controller\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"key\": \"1\",\n" +
                "        \"type\": \"root\"\n" +
                "    }\n" +
                "}", TreeResponse.class)
        assertNotNull(planTree.tree)
        assertNotNull(planTree.map)
    }


    @Test
    void "test TreeResponse.parse()"() {
        def parse = TreeResponse.parse(planTree)
        assertNotNull(parse)
        def handlers = parse.testElements
        assertNotNull(handlers)
        assertEquals("解析handler类型不成功",2,handlers.size())
        assertTrue("实例化handler不成功",handlers.get(0) instanceof TransactionController)
        def transactional = (TransactionController) handlers.get(0)
        assertEquals("name属性赋值不成功","事务1", transactional.title)
        assertEquals("comment","第一个事务", transactional.comment)
    }
}

