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

package com.huawei.emergency.layout.sampler;

import com.huawei.emergency.layout.ElementProcessContext;
import com.huawei.emergency.layout.Jsr223Util;
import com.huawei.emergency.layout.config.HttpRequestDefault;
import com.huawei.emergency.layout.template.GroovyMethodTemplate;

import lombok.Data;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http 取样器
 *
 * @author y30010171
 * @since 2021-12-15
 **/
@Data
public class HttpSampler extends Sampler {
    /**
     * 支持的http请求
     */
    public static final List<String> ALL_METHODS = Arrays.asList("GET", "POST", "PUT", "DELETE", "TRACE", "HEAD",
        "OPTIONS");
    public static final Pattern PARAMETER_PATTERN = Pattern.compile("#\\{(\\w+)}");
    private String protocol = "http";
    private String domain;
    private String port;
    private String method;
    private String path;
    private String contentEncoding;

    private boolean autoRedirect;
    private boolean followRedirects;
    private boolean useKeepAlive;
    private boolean useFormData;
    private boolean compatibleHeaders;
    private List<HttpRequestDefault.Parameters> parameters = new ArrayList<>();
    private String body;

    @Override
    public void handle(ElementProcessContext context) {
        if (!"http".equals(protocol) || StringUtils.isEmpty(method) || !ALL_METHODS.contains(
            method.toUpperCase(Locale.ROOT))) {
            return;
        }
        GroovyMethodTemplate currentMethod = context.getCurrentMethod();
        String requestVariableName = "request" + context.getVariableCount();
        if (StringUtils.isNotEmpty(path) && path.startsWith("/")) {
            path = path.substring(1);
        }
        String url = String.format(Locale.ROOT, "\"%s://\"+\"%s\" + \":\" + \"%s\" + \"/\" + \"%s\"",
            protocol,
            parameterized(domain),
            parameterized(port),
            parameterized(path));
        currentMethod.addContent(String.format(Locale.ROOT, "def %s = new HTTPRequest();", requestVariableName), 2);
        currentMethod.addContent(String.format(Locale.ROOT, "%s.setUrl(%s);", requestVariableName, url), 2);
        currentMethod.addContent(
            String.format(Locale.ROOT, "%s.setHeaders( headers as NVPair[]);", requestVariableName), 2);
        currentMethod.addContent(String.format(Locale.ROOT, "%s.setData(%s);", requestVariableName, generateBodyData()),
            2);
        currentMethod.addContent(
            String.format(Locale.ROOT, "%s.setFormData(%s);", requestVariableName, generateNvPairs()), 2);
        String resultVariableName = "httpResult" + context.getVariableCount();
        currentMethod.addContent(String.format(Locale.ROOT, "def %s = %s.%s()", resultVariableName, requestVariableName,
            method.toUpperCase(Locale.ROOT)), 2);
        context.setHttpRequestVariableName(requestVariableName);
        context.setHttpResultVariableName(resultVariableName);
        nextElements().forEach(testElement -> testElement.handle(context)); // 生成header cookie等组件信息
    }

    private String generateNvPairs() {
        StringBuilder result = new StringBuilder("[");
        for (HttpRequestDefault.Parameters parameter : parameters) {
            result.append(
                String.format(Locale.ROOT, "new NVPair(\"%s\", \"%s\"),", parameterized(parameter.getName()),
                    parameterized(parameter.getValue())));
        }
        if (result.length() > 1) {
            result.delete(result.length() - 1, result.length());
        }
        return result.append("] as NVPair[]").toString();
    }

    private String generateBodyData() {
        return String.format(Locale.ROOT, "\"%s\".bytes", Jsr223Util.parseScript(body));
    }

    private static String parameterized(String source) {
        Matcher matcher = PARAMETER_PATTERN.matcher(source);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, "\"+" + matcher.group(1) + "+\"");
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
