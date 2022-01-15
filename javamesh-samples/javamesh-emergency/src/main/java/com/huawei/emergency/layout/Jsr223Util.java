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

import com.huawei.common.constant.ScriptLanguageEnum;
import com.huawei.common.exception.ApiException;
import com.huawei.emergency.layout.assertion.Jsr223Assertion;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;

/**
 * @author y30010171
 * @since 2022-01-12
 **/
public class Jsr223Util {
    private static final Logger LOGGER = LoggerFactory.getLogger(Jsr223Util.class);
    private static final String JS_CONTENT_FORMAT = "new JavaScriptExecutor().executeScript(\"%s\",null);";
    private static final String SHELL_CONTENT_FORMAT = "new CommandService().execCommand(\"%s\",300000L);";

    public static void handle(ElementProcessContext context, String language, String script) {
        if (StringUtils.isEmpty(script)) {
            return;
        }
        ScriptLanguageEnum languageEnum = ScriptLanguageEnum.match(language);
        if (languageEnum == null) {
            throw new ApiException(String.format(Locale.ROOT, "不支持的脚本语言类型%s", language));
        }
        if (languageEnum == ScriptLanguageEnum.GROOVY) {
            try (BufferedReader scriptReader = new BufferedReader(new StringReader(script))) {
                String line;
                while ((line = scriptReader.readLine()) != null) {
                    context.getCurrentMethod().addContent(line, 2);
                }
            } catch (IOException e) {
                LOGGER.error("Can't not read script.", e);
            }
        } else if (languageEnum == ScriptLanguageEnum.JAVASCRIPT) {
            context.getCurrentMethod().addContent(String.format(Locale.ROOT, JS_CONTENT_FORMAT, StringEscapeUtils.escapeJava(script)), 2);
        } else if (languageEnum == ScriptLanguageEnum.SHELL) {
            context.getCurrentMethod().addContent(String.format(Locale.ROOT, SHELL_CONTENT_FORMAT, StringEscapeUtils.escapeJava(script)), 2);
        }
    }
}
