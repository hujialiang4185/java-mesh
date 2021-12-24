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

package com.huawei.emergency.layout.config;

import com.huawei.emergency.layout.HandlerContext;
import com.huawei.emergency.layout.template.GroovyMethodTemplate;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author y30010171
 * @since 2021-12-15
 **/
@Data
public class HttpCookieManager implements Config {
    private String name;
    private String comment;
    private boolean clearCookiesEachIteration;
    private String policy;
    private List<CookieValue> cookies = new ArrayList<>();

    @Override
    public void handle(HandlerContext context) {
        GroovyMethodTemplate beforeMethod = context.getTemplate().getBeforeMethod();
        for (CookieValue cookie : cookies) {
            beforeMethod.addContent(
                String.format(Locale.ROOT,
                    "CookieModule.addCookie(new Cookie(\"%s\", \"%s\", \"%s\", \"/%s\", null, %s),threadContext);",
                    cookie.getName(), cookie.getValue(), cookie.getDomain(), cookie.getPath(), Boolean.parseBoolean(cookie.getSecure())
                ), 2);
        }
    }

    @Data
    class CookieValue {
        private String name;
        private String value;
        private String domain;
        private String path;
        private String secure;
    }
}
