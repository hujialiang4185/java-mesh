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
 * <p>testField目前支持ResponseCode,ResponseMessage。除了ResponseCode情况下，其余皆为ResponseMessage</p>
 * <p>ignoreStatus暂不使用</p>
 * <p>matchRulers匹配模式，目前只支持正则匹配matches。应当支持contains,matches,equals,substring,not,or</p>
 * <p>patternToTest正则表达式</p>
 * <p>failureMessage错误提示信息</p>
 *
 * @author y30010171
 * @since 2021-12-16
 **/
@Data
public class ResponseAssertion extends Assertion {

    private static final String CONTENT_FORMAT = "Assert.assertTrue(\"%s\", RegularAssert.assertRegular(%s,\"%s\"));";
    private String applyTo;
    private String testField;
    private boolean ignoreStatus;
    private String matchRulers = "matches";
    private String patternToTest;
    private String failureMessage;


    @Override
    public void handle(ElementProcessContext context) {
        if (StringUtils.isEmpty(patternToTest)) {
            return;
        }
        String field = "new String(httpResult.data)";
        if ("ResponseCode".equals(testField)) {
            field = "new String(httpResult.statusCode)";
        }
        context.getCurrentMethod().addContent(String.format(Locale.ROOT, CONTENT_FORMAT, failureMessage, field, patternToTest), 2);
    }
}
