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

import com.huawei.emergency.layout.template.GroovyClassTemplate;
import com.huawei.emergency.layout.template.GroovyMethodTemplate;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 元件处理上下文
 *
 * @author y30010171
 * @since 2022-01-19
 **/
public class DefaultElementProcessContext implements ElementProcessContext {

    private AtomicInteger variableCount = new AtomicInteger();
    private GroovyClassTemplate template = new GroovyClassTemplate();
    private GroovyMethodTemplate currentMethod = new GroovyMethodTemplate();
    private String httpRequestVariableName = "";
    private String httpResultVariableName = "";

    @Override
    public int getVariableCount() {
        return variableCount.getAndIncrement();
    }

    @Override
    public GroovyClassTemplate getTemplate() {
        return this.template;
    }

    @Override
    public void setTemplate(GroovyClassTemplate template) {
        this.template = template;
    }

    @Override
    public GroovyMethodTemplate getCurrentMethod() {
        return this.currentMethod;
    }

    @Override
    public void setCurrentMethod(GroovyMethodTemplate currentMethod) {
        this.currentMethod = currentMethod;
    }

    @Override
    public String getHttpRequestVariableName() {
        return this.httpRequestVariableName;
    }

    @Override
    public void setHttpRequestVariableName(String httpRequestVariableName) {
        this.httpRequestVariableName = httpRequestVariableName;
    }

    @Override
    public String getHttpResultVariableName() {
        return this.httpResultVariableName;
    }

    @Override
    public void setHttpResultVariableName(String httpResultVariableName) {
        this.httpResultVariableName = httpResultVariableName;
    }
}
