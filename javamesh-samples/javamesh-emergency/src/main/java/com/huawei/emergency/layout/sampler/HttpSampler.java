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

import com.huawei.emergency.layout.TestElement;
import com.huawei.emergency.layout.HandlerContext;
import com.huawei.emergency.layout.config.DnsCacheManager;
import com.huawei.emergency.layout.config.HttpRequestDefault;
import com.huawei.emergency.layout.template.GroovyFieldTemplate;
import com.huawei.emergency.layout.template.GroovyMethodTemplate;
import lombok.Data;

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
public class HttpSampler implements Sampler {

    private String name;
    private String comment;
    private String protocol = "http";
    private String serviceName;
    private int port;
    private String method;
    private String path;
    private String contentEncoding;

    private boolean autoRedirect;
    private boolean followRedirects;
    private boolean useKeepAlive;
    private boolean useFormData;
    private boolean compatibleHeaders;
    private List<HttpRequestDefault.Parameters> defaultParams;
    private String body;

    private List<TestElement> testElements = new ArrayList<>();

    @Override
    public void handle(HandlerContext context) {
        context.getTemplate().addFiled(GroovyFieldTemplate.create(String.format(Locale.ROOT, "    public static HTTPRequest %s;", name)));
        context.getTemplate().getBeforeProcessMethod().addContent(String.format(Locale.ROOT, "%s = new HTTPRequest();", name), 2);

        // todo 生成请求header
        GroovyMethodTemplate currentMethod = context.getCurrentMethod();
        currentMethod.addContent("def headerList = new ArrayList<NVPair>();", 2);
        context.getHeaderManagers().forEach(httpHeaderManager -> httpHeaderManager.handle(context));
        currentMethod.addContent("def header = headerList.toArray();", 2);

        // todo 使用自定义dns解析器
        String url = path;
        for (DnsCacheManager dnsCacheManager : context.getDnsCacheManagers()) {
            url = dnsCacheManager.resolver(url);
        }
    }

    @Override
    public List<TestElement> nextElements() {
        return testElements;
    }
}
