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

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试元件-根节点
 *
 * @author y30010171
 * @since 2021-12-17
 **/
@Data
public class TestPlanTestElement extends ParentTestElement {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestPlanTestElement.class);

    @Override
    public void handle(ElementProcessContext context) {
        nextElements().stream()
            .filter(testElement -> testElement instanceof DefaultTestElement
                || testElement instanceof CustomMethodTestElement || testElement instanceof TransactionController)
            .forEach(handler -> handler.handle(context));
    }
}
