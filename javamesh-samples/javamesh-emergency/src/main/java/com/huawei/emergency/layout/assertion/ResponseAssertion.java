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
 * @author y30010171
 * @since 2021-12-16
 **/
@Data
public class ResponseAssertion extends Assertion {

    private String type;
    private String testField;
    private boolean ignoreStatus;
    private String matchRulers;
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
        context.getCurrentMethod().addContent(
            String.format(Locale.ROOT, "Assert.assertTrue(\"%s\", RegularAssert.assertRegular(%s,\"%s\"));",
                failureMessage, field, patternToTest), 2);
    }
}
