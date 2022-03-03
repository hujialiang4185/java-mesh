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

package com.huawei.emergency.layout.controller;

import com.huawei.emergency.layout.ElementProcessContext;
import com.huawei.emergency.layout.TestElement;
import com.huawei.emergency.layout.template.GroovyClassTemplate;
import com.huawei.emergency.layout.template.GroovyFieldTemplate;
import com.huawei.emergency.layout.template.GroovyMethodTemplate;

import lombok.Data;

import org.apache.commons.lang.StringUtils;

import java.util.Locale;

/**
 * 事务控制器
 *
 * @author y30010171
 * @since 2021-12-16
 **/
@Data
public class TransactionController extends Controller {
    private int presure = 100;

    private GroovyMethodTemplate method;
    private GroovyFieldTemplate field;

    @Override
    public void handle(ElementProcessContext context) {
        if (StringUtils.isEmpty(getTitle())) {
            throw new RuntimeException("请输入事务名称");
        }
        GroovyClassTemplate template = context.getTemplate();
        GroovyMethodTemplate method = methodTemplate();
        if (template.containsMethod(method.getMethodName())) {
            throw new RuntimeException(String.format(Locale.ROOT, "存在名称相同的事务 {}", method.getMethodName()));
        }
        template.addMethod(method);
        template.addFiled(fieldTemplate());
        template.getBeforeProcessMethod().addContent(
            String.format(Locale.ROOT, "%s = new GTest(%s, \"%s\")", getTitle(),
                GroovyClassTemplate.TEST_NUMBER_METHOD.invokeStr(), getTitle()), 2);
        template.getBeforeThreadMethod()
            .addContent(String.format(Locale.ROOT, "%s.record(this, \"%s\")", getTitle(), getTitle()), 2);
        for (TestElement testElement : nextElements()) {
            context.setCurrentMethod(method);
            testElement.handle(context);
        }

        // 寻找configElement
    }

    public GroovyMethodTemplate methodTemplate() {
        if (method == null) {
            method = new GroovyMethodTemplate()
                .addAnnotation("    @Test")
                .start(String.format(Locale.ROOT, "public void \"%s\"() {", getTitle()), 1)
                .end("}", 1);
            method.setMethodName(getTitle());
        }
        return method;
    }

    public GroovyFieldTemplate fieldTemplate() {
        if (field == null) {
            field = GroovyFieldTemplate.create(String.format(Locale.ROOT, "    public static GTest %s;", getTitle()));
        }
        return field;
    }

    public String invokeStr() {
        return String.format(Locale.ROOT, "\"%s\"()", getTitle());
    }
}
