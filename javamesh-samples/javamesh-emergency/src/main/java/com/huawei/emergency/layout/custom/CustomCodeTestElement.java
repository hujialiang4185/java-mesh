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
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义代码块
 *
 * @author y30010171
 * @since 2021-12-17
 **/
@Data
@Deprecated
public class CustomCodeTestElement extends TestElement {

    private String content;
    private static Pattern compile = Pattern.compile("^(\\//start)(.+)(\\//end)");

    @Override
    public void handle(ElementProcessContext context) {
        if (StringUtils.isNotEmpty(content)) {
            Matcher matcher = compile.matcher(content);
            while (matcher.find()) {
                context.getCurrentMethod().addContent(matcher.group(2), 2);
            }
        }
    }
}
