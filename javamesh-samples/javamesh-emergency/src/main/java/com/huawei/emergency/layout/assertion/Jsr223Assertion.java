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

package com.huawei.emergency.layout.assertion;

import com.huawei.common.constant.ScriptLanguageEnum;
import com.huawei.common.exception.ApiException;
import com.huawei.emergency.layout.ElementProcessContext;
import com.huawei.emergency.layout.Jsr223Util;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * JSR223断言
 *
 * @author y30010171
 * @since 2022-01-06
 **/
@Data
public class Jsr223Assertion extends Assertion {
    private static final Logger LOGGER = LoggerFactory.getLogger(Jsr223Assertion.class);
    private static final String JS_CONTENT_FORMAT = "new JavaScriptExecutor().executeScript(\"%s\",null);";
    private static final String SHELL_CONTENT_FORMAT = "new CommandService().execCommand(\"%s\",300000L);";
    private String language;
    private String script;

    @Override
    public void handle(ElementProcessContext context) {
        Jsr223Util.handle(context,language,script);
    }
}
