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
import com.huawei.emergency.layout.config.HttpRequestDefault;
import com.huawei.emergency.layout.template.GroovyMethodTemplate;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * http 取样器
 *
 * @author y30010171
 * @since 2021-12-15
 **/
@Data
public class HttpSampler extends Sampler {

    private String protocol = "http";
    private String domain;
    private int port;
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
        if (!"http".equals(protocol) || StringUtils.isEmpty(method)) {
            return;
        }
        /*
        // todo 使用自定义dns解析器
        String url = path;
        for (DnsCacheManager dnsCacheManager : context.getDnsCacheManagers()) {
            url = dnsCacheManager.resolver(url);
        }*/
        GroovyMethodTemplate currentMethod = context.getCurrentMethod();
        String requestVariableName = "request" + context.getVariableCount().getAndIncrement();
        if (StringUtils.isNotEmpty(path) && path.startsWith("/")) {
            path = path.substring(1);
        }
        String url = String.format(Locale.ROOT, "%s://%s:%s/%s", protocol, domain, port, path);
        currentMethod.addContent(String.format(Locale.ROOT, "def %s = new HTTPRequest();", requestVariableName), 2);
        currentMethod.addContent(String.format(Locale.ROOT, "%s.setHeaders( headers as NVPair[]);", requestVariableName), 2);
        String resultVariableName = "httpResult" + context.getVariableCount().getAndIncrement();
        resovleMethod(requestVariableName, resultVariableName, method.toUpperCase(Locale.ROOT), url, context); // 根据方法类型生成调用
        context.setHttpRequestVariableName(requestVariableName);
        context.setHttpResultVariableName(resultVariableName);
        nextElements().forEach(testElement -> testElement.handle(context)); // 生成header cookie等组件信息
    }

    public void resovleMethod(String requestName, String resultVariableName, String methodType, String url, ElementProcessContext context) {
        GroovyMethodTemplate currentMethod = context.getCurrentMethod();
        if ("GET".equals(methodType)) {
            currentMethod.addContent(String.format(Locale.ROOT, "def %s = %s.%s(\"%s\",%s)", resultVariableName, requestName, methodType, url, generateNvPairs()), 2);
        } else if ("POST".equals(methodType)) {
            currentMethod.addContent(String.format(Locale.ROOT, "def %s = %s.%s(\"%s\",%s)", resultVariableName, requestName, methodType, url, generateBodyData()), 2);
        } else if ("PUT".equals(methodType)) {
            currentMethod.addContent(String.format(Locale.ROOT, "def %s = %s.%s(\"%s\",%s)", resultVariableName, requestName, methodType, url, generateBodyData()), 2);
        } else if ("DELETE".equals(methodType) || "TRACE".equals(methodType)) {
            currentMethod.addContent(String.format(Locale.ROOT, "def %s = %s.%s(\"%s\")", resultVariableName, requestName, methodType, url), 2);
        } else if ("HEAD".equals(methodType)) {
            currentMethod.addContent(String.format(Locale.ROOT, "def %s = %s.%s(\"%s\",%s)", resultVariableName, requestName, methodType, url, generateNvPairs()), 2);
        } else if ("OPTIONS".equals(methodType)) {
            currentMethod.addContent(String.format(Locale.ROOT, "def %s = %s.%s(\"%s\",%s)", resultVariableName, requestName, methodType, url, generateBodyData()), 2);
        }
    }

    private String generateNvPairs() {
        StringBuilder result = new StringBuilder("[");
        for (HttpRequestDefault.Parameters parameter : parameters) {
            result.append(String.format(Locale.ROOT, "new NVPair(\"%s\", \"%s\"),", parameter.getName(), parameter.getValue()));
        }
        if (result.length() > 1) {
            result.delete(result.length() - 1, result.length());
        }
        return result.append("] as NVPair[]").toString();
    }

    private String generateBodyData() {
        return String.format(Locale.ROOT, "\"%s\".bytes", body);
    }
}
