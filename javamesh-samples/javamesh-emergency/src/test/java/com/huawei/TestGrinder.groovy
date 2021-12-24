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

package com.huawei

import net.grinder.scriptengine.groovy.junit.GrinderRunner



import org.junit.Test
import org.junit.runner.RunWith

@RunWith(GrinderRunner)
class TestGrinder {

    @Test
    public void test() {
        /*// 获取总虚拟用户和运行线程数
        int vusers = getVusers()
        int runThreadNum = getRunThreadNum()

        // 运行百分比例设置
        int runRate1 = 50
        int runRate2 = 50

        // 计算线程运行比例数
        int runNum1 = vusers / 100 * runRate1
        int runNum2 = vusers / 100 * (runRate1 + runRate2)*/

        // 两次请求
        for (i in 0..<2) {
            if (i % 2 == 0)
                this."事务1 占50"()
            else
                this."事务2 占50"()
            this."事务3 默认100"()
        }
    }


    @Test
    public void "事务1 占50"() {
        println("事务1")
    }

    @Test
    public void "事务2 占50"() {
        println("事务2")
    }

    @Test
    public void "事务3 默认100"() {
        println("事务3")
    }
}

