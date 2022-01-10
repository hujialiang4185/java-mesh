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
import com.huawei.emergency.layout.template.GroovyClassTemplate;
import com.huawei.emergency.layout.template.GroovyFieldTemplate;
import com.huawei.emergency.layout.template.GroovyMethodTemplate;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.Locale;

/**
 * 正则表达式提取器
 *
 * @author y30010171
 * @since 2021-12-21
 **/
@Data
public class RegularPostProcessor extends PostProcessor {
    private String fieldToCheck;
    private String nameOfCreateVariable;
    private String regularExpression;
    private int groupIndex;
    private int matchIndex;
    private String defaultValue = "";

    private static final String FORMAT = "def %s = new RegularExpressionExtractor().extract(\"%s\"," +
        "new RegularExtractorConfig(new RegularExtractorConfig.Builder(regularExpression: \"%s\", groupIndex: %s, matchIndex: %s, defaultValue: \"%s\")));";

    @Override
    public void handle(ElementProcessContext context) {
        if (StringUtils.isEmpty(nameOfCreateVariable)) {
            return;
        }
        GroovyClassTemplate template = context.getTemplate();
        template.addImport("import com.huawei.test.postprocessor.config.RegularExtractorConfig;");
        template.addImport("import com.huawei.test.postprocessor.impl.RegularExpressionExtractor;");
        GroovyMethodTemplate currentMethod = context.getCurrentMethod();
        currentMethod.addContent(String.format(Locale.ROOT, FORMAT, nameOfCreateVariable, "new String(httpResult.data)", regularExpression, groupIndex, matchIndex, defaultValue), 2);
    }
}
