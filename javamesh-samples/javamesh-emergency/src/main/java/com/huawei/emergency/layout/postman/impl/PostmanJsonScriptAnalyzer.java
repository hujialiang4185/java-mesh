/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.emergency.layout.postman.impl;

import com.alibaba.fastjson.JSONObject;
import com.huawei.emergency.layout.postman.PostmanScriptAnalyzer;
import com.huawei.emergency.layout.postman.entity.PostmanScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 功能描述：把postman脚本json格式的内容，转换成javabean的实例，方便使用
 *
 * @author zl
 * @since 2022-03-11
 */
public class PostmanJsonScriptAnalyzer implements PostmanScriptAnalyzer {
    /**
     * 日志工具
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PostmanJsonScriptAnalyzer.class);

    @Override
    public PostmanScript processPostmanScript(InputStream postmanScriptStream, Charset charset) {
        if (postmanScriptStream == null) {
            return null;
        }
        Charset useCharset = charset == null ? StandardCharsets.UTF_8 : charset;
        try (InputStreamReader scriptReader = new InputStreamReader(postmanScriptStream, useCharset);
             BufferedReader bufferedReader = new BufferedReader(scriptReader)) {
            String oneLine;
            StringBuilder scriptBuilder = new StringBuilder();
            while ((oneLine = bufferedReader.readLine()) != null) {
                scriptBuilder.append(oneLine);
            }
            return JSONObject.parseObject(scriptBuilder.toString(), PostmanScript.class);
        } catch (IOException exception) {
            LOGGER.error("Convert postman json fail:", exception);
            return null;
        }
    }
}
