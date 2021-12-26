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

package com.huawei.emergency.layout.custom;

import com.huawei.emergency.layout.TestElement;
import com.huawei.emergency.layout.ElementProcessContext;
import com.huawei.emergency.layout.template.GroovyMethodTemplate;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.Locale;

/**
 * 自定义方法
 *
 * @author y30010171
 * @since 2021-12-17
 **/
@Data
public class CustomMethodTestElement implements TestElement {

    private String title;
    private String comment;
    private String content;
    private GroovyMethodTemplate method;

    @Override
    public void handle(ElementProcessContext context) {
        if (StringUtils.isNotEmpty(content)) {
            if (context.getTemplate().containsMethod(method.getMethodName())) {
                throw new RuntimeException(String.format(Locale.ROOT, "存在名称相同的方法 {}", method.getMethodName()));
            }
            GroovyMethodTemplate method = getMethod();
            method.addContent(content, 2);
            context.getTemplate().addMethod(method);
        }
    }

    public GroovyMethodTemplate getMethod() {
        if (method == null) {
            method = new GroovyMethodTemplate()
                .start(String.format(Locale.ROOT, "public void \"%s\"() {", title), 1)
                .end("}", 1);
            method.setMethodName(title);
        }
        return method;
    }
}
