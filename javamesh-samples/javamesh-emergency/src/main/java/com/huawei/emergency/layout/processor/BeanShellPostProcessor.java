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

package com.huawei.emergency.layout.processor;

import com.huawei.emergency.layout.ElementProcessContext;
import com.huawei.emergency.layout.template.GroovyMethodTemplate;
import lombok.Data;

/**
 * BeanShell后置处理器
 *
 * @author y30010171
 * @since 2021-12-16
 **/
@Data
public class BeanShellPostProcessor extends PostProcessor{
    private boolean resetInterpreter;
    private String parameters;
    private String fileName;
    private String script;

    @Override
    public void handle(ElementProcessContext context) {
        GroovyMethodTemplate currentMethod = context.getCurrentMethod();
        for (String line : this.script.split(System.lineSeparator())) {
            currentMethod.addContent(line,2);
        }
    }
}
