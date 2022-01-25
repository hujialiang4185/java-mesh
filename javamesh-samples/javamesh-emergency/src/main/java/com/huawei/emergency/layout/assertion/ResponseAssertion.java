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

import com.huawei.emergency.layout.ElementProcessContext;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.Locale;

/**
 * 响应断言
 *
 * <p>applyTo暂不使用。应支持四种 1. main and sub 2. main only 3. sub only 4. variable name to use</p>
 * <p>testField目前支持响应代码,响应头,响应文本,响应消息,请求头,URL样本,文档(文本),请求数据</p>
 * <p>ignoreStatus暂不使用</p>
 * <p>matchRulers匹配模式，支持contains,matches,equals</p>
 * <p>patternToTest正则表达式</p>
 * <p>failureMessage错误提示信息</p>
 *
 * @author y30010171
 * @since 2021-12-16
 **/
@Data
public class ResponseAssertion extends Assertion {
    private static final String CONTAINS_FORMAT = "Assert.assertTrue(\"%s\", %s.contains(\"%s\"));";
    private static final String MATCHES_FORMAT = "Assert.assertTrue(\"%s\", RegularAssert.assertRegular(%s,\"%s\"));";
    private static final String EQUALS_FORMAT = "Assert.assertEquals(\"%s\", \"%s\", %s);";

    private String testField;
    private String testType;
    private String testStrings;

    private String applyTo;
    private boolean ignoreStatus;
    private String matchRulers = "matches";
    private String patternToTest;
    private String failureMessage = "响应断言失败";


    @Override
    public void handle(ElementProcessContext context) {
        if (StringUtils.isEmpty(testStrings)) {
            return;
        }
        String request = context.getHttpRequestVariableName();
        String response = context.getHttpResultVariableName();
        String variableName = "";
        String fieldStr = "";
        if ("响应代码".equals(testField)) {
            variableName = response;
            fieldStr = "statusCode.toString()";
        } else if ("响应头".equals(testField) || "响应文本".equals(testField) || "响应消息".equals(testField)) {
            variableName = response;
            fieldStr = "data";
        } else if ("请求头".equals(testField)) {
            variableName = request;
            fieldStr = "headers.toString()";
        } else if ("URL样本".equals(testField)) {
            variableName = request;
            fieldStr = "url";
        } else if ("文档(文本)".equals(testField)) {
            variableName = request;
            fieldStr = "data";
        } else if ("请求数据".equals(testField)) {
            variableName = request;
            fieldStr = "formData.toString()";
        }
        String field = String.format(Locale.ROOT, "new String(%s.%s)", variableName, fieldStr);
        if ("包括".equals(testType)) {
            context.getCurrentMethod().addContent(String.format(Locale.ROOT, CONTAINS_FORMAT, failureMessage, field, testStrings), 2);
        } else if ("相等".equals(testType)) {
            context.getCurrentMethod().addContent(String.format(Locale.ROOT, EQUALS_FORMAT, failureMessage, testStrings, field), 2);
        } else {
            context.getCurrentMethod().addContent(String.format(Locale.ROOT, MATCHES_FORMAT, failureMessage, field, testStrings), 2);
        }
    }
}