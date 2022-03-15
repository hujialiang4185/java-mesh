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

import com.huawei.emergency.layout.TestPlanTestElement;
import com.huawei.emergency.layout.config.HttpCookieManager;
import com.huawei.emergency.layout.config.HttpHeaderManager;
import com.huawei.emergency.layout.controller.TransactionController;
import com.huawei.emergency.layout.custom.BeforeThreadTestElement;
import com.huawei.emergency.layout.postman.GuiScriptConverter;
import com.huawei.emergency.layout.postman.entity.PostmanScript;
import com.huawei.emergency.layout.postman.entity.request.PostmanRequest;
import com.huawei.emergency.layout.postman.entity.request.PostmanRequestBody;
import com.huawei.emergency.layout.postman.entity.request.PostmanRequestDefine;
import com.huawei.emergency.layout.postman.entity.request.PostmanRequestHeader;
import com.huawei.emergency.layout.postman.entity.request.PostmanRequestQueryParam;
import com.huawei.emergency.layout.postman.entity.request.PostmanRequestUrl;
import com.huawei.emergency.layout.sampler.HttpSampler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.huawei.emergency.layout.config.HttpRequestDefault.Parameters;

/**
 * 功能描述：postman script转换成gui中的test plan
 *
 * @author zl
 * @since 2022-03-14
 */
public class PostmanScript2GuiConverter implements GuiScriptConverter<PostmanScript> {
    /**
     * postman脚本类型中，请求体为string类型的请求
     */
    public static final String POSTMAN_STRING_BODY_TYPE = "raw";

    /**
     * postman请求脚本中http请求默认端口
     */
    public static final int POSTMAN_DEFAULT_HTTP_PORT = 8080;

    /**
     * cookie管理器名称后缀
     */
    public static final String HTTP_COOKIE_MANAGER_SUFFIX = "-httpCookieManager";

    /**
     * header管理器名称后缀
     */
    public static final String HTTP_HEADER_MANAGER_SUFFIX = "-httpHeaderManager";

    /**
     * 前置处理器后缀名
     */
    public static final String BEFORE_THREAD_PROCESS_NAME_SUFFIX = " before Thread Process";

    /**
     * 事务处理器后缀名
     */
    public static final String TRANSACTION_CONTROLLER_NAME_SUFFIX = " transaction controller";

    /**
     * 测试计划后缀名
     */
    public static final String TEST_PLAN_NAME_SUFFIX = " test plan";

    /**
     * header中cookie的key名称
     */
    public static final String COOKIE_HEADER_NAME = "Cookie";

    /**
     * 日志工具
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PostmanScript2GuiConverter.class);


    @Override
    public List<TestPlanTestElement> convertPostmanScript(PostmanScript postmanScript) {
        if (postmanScript == null) {
            LOGGER.error("The postman script instance is null, return empty test plan list.");
            return Collections.emptyList();
        }
        final List<PostmanRequestDefine> postmanRequestDefines = postmanScript.getPostmanRequests();
        if (postmanRequestDefines.isEmpty()) {
            LOGGER.error("The request defines list is empty, return empty test plan list.");
            return Collections.emptyList();
        }
        List<TestPlanTestElement> testPlans = new ArrayList<>();
        for (PostmanRequestDefine postmanRequestDefine : postmanRequestDefines) {
            // 判断当前是否能处理这种类型的postman请求
            if (!canHandleRequest(postmanRequestDefine)) {
                LOGGER.error("Can not handle this request type at now:{}", postmanRequestDefine);
                continue;
            }
            String httpRequestName = postmanRequestDefine.getName();
            PostmanRequest postmanRequest = postmanRequestDefine.getPostmanRequest();

            // 构造请求header
            HttpHeaderManager httpHeaderManager = getHttpHeaderManager(postmanRequest, httpRequestName);

            // 构造请求cookie
            HttpCookieManager httpCookieManager = getHttpCookieManager(postmanRequest, httpRequestName);

            // 构造HttpSampler
            HttpSampler httpSampler = getHttpSampler(postmanRequest, httpRequestName);

            // 封装BeforeThreadProcess
            BeforeThreadTestElement beforeThreadTestElement = new BeforeThreadTestElement();
            beforeThreadTestElement.getTestElements().add(httpCookieManager);
            beforeThreadTestElement.getTestElements().add(httpHeaderManager);
            beforeThreadTestElement.setTitle(httpRequestName + BEFORE_THREAD_PROCESS_NAME_SUFFIX);

            // 封装TransactionController
            TransactionController transactionController = new TransactionController();
            transactionController.getTestElements().add(httpSampler);
            transactionController.setTitle(httpRequestName + TRANSACTION_CONTROLLER_NAME_SUFFIX);

            // 封装TestPlan
            TestPlanTestElement testPlanTestElement = new TestPlanTestElement();
            testPlanTestElement.getTestElements().add(beforeThreadTestElement);
            testPlanTestElement.getTestElements().add(transactionController);
            testPlanTestElement.setTitle(httpRequestName + TEST_PLAN_NAME_SUFFIX);
            testPlans.add(testPlanTestElement);
        }
        LOGGER.info("Successfully converted test plans as below:{}", testPlans);
        return testPlans;
    }

    /**
     * 判断该postman转义过来的请求是否能处理，目前只能处理请求体为string类型的请求，表单，文件类请求无法处理
     *
     * @param postmanRequestDefine postman请求定义
     * @return 如果能处理这个请求返回true，反之返回false
     */
    private boolean canHandleRequest(PostmanRequestDefine postmanRequestDefine) {
        if (postmanRequestDefine == null) {
            return false;
        }
        PostmanRequest postmanRequest = postmanRequestDefine.getPostmanRequest();
        if (postmanRequest == null) {
            return false;
        }
        PostmanRequestUrl url = postmanRequest.getUrl();
        return url != null;
    }

    /**
     * 根据postman请求数据，封装httpSampler
     *
     * @param postmanRequest postman脚本转义过来的请求数据
     * @param title          元件名称
     * @return HttpSampler请求实例，封装了请求数据
     */
    private HttpSampler getHttpSampler(PostmanRequest postmanRequest, String title) {
        HttpSampler httpSampler = new HttpSampler();
        httpSampler.setTitle(title);
        httpSampler.setMethod(postmanRequest.getMethod());
        httpSampler.setProtocol(postmanRequest.getUrl().getProtocol());
        httpSampler.setDomain(postmanRequest.getUrl().getWholeHost());
        httpSampler.setPort(getIntTypePort(postmanRequest.getUrl().getPort()));
        httpSampler.setPath(postmanRequest.getUrl().getWholePath());
        httpSampler.setParameters(getQueryParams(postmanRequest.getUrl().getQuery()));
        PostmanRequestBody requestBody = postmanRequest.getBody();
        String bodyType = requestBody.getMode();
        if (POSTMAN_STRING_BODY_TYPE.equals(bodyType)) {
            httpSampler.setBody(requestBody.getRaw());
        } else {
            LOGGER.warn("Not supported body type:{}, so use empty body.", bodyType);
            httpSampler.setBody("");
        }
        httpSampler.setAutoRedirect(false);
        return httpSampler;
    }

    /**
     * 把postman中的query参数封装成HttpSampler中的请求参数
     *
     * @param postmanQueryParams postman中的请求参数列表
     * @return gui脚本中需求的请求参数列表
     */
    private List<Parameters> getQueryParams(List<PostmanRequestQueryParam> postmanQueryParams) {
        if (postmanQueryParams == null || postmanQueryParams.isEmpty()) {
            return Collections.emptyList();
        }
        List<Parameters> needTypeParams = new ArrayList<>();
        for (PostmanRequestQueryParam postmanQueryParam : postmanQueryParams) {
            Parameters parameters = new Parameters();
            parameters.setName(postmanQueryParam.getKey());
            parameters.setValue(postmanQueryParam.getValue());
            parameters.setIncludeEquals(postmanQueryParam.isEquals());
            parameters.setUrlEncode(true);
            needTypeParams.add(parameters);
        }
        return needTypeParams;
    }

    /**
     * 把字符串类型的端口，转变成整型的端口
     *
     * @param portString 字符串端口
     * @return 整型端口
     */
    private int getIntTypePort(String portString) {
        try {
            return Integer.parseInt(portString);
        } catch (NumberFormatException exception) {
            LOGGER.error("The port from postman script is invalid, use default {}", POSTMAN_DEFAULT_HTTP_PORT);
            return POSTMAN_DEFAULT_HTTP_PORT;
        }
    }

    /**
     * postman脚本转义的请求中，每一个请求的cookie都放入到CookieManager中进行管理，该方法提取出cookie并封装到管理器中
     *
     * @param postmanRequest postman脚本转义过来的请求数据
     * @param title          http请求元件名称
     * @return HttpCookieManager，封装了被管理的cookie
     */
    private HttpCookieManager getHttpCookieManager(PostmanRequest postmanRequest, String title) {
        HttpCookieManager httpCookieManager = new HttpCookieManager();
        httpCookieManager.setTitle(title + HTTP_COOKIE_MANAGER_SUFFIX);
        List<PostmanRequestHeader> headers = postmanRequest.getHeader();
        String wholeHost = postmanRequest.getUrl().getWholeHost();
        List<HttpCookieManager.CookieValue> cookieValues = new ArrayList<>();
        for (PostmanRequestHeader header : headers) {
            if (!COOKIE_HEADER_NAME.equalsIgnoreCase(header.getKey())) {
                continue;
            }
            String wholeCookieString = header.getValue();
            if (StringUtils.isEmpty(wholeCookieString)) {
                break;
            }
            String[] cookieEntries = wholeCookieString.split(";");
            for (String cookieEntry : cookieEntries) {
                String[] cookieKeyValueArray = cookieEntry.trim().split("=");
                if (cookieKeyValueArray.length != 2) {
                    continue;
                }
                HttpCookieManager.CookieValue cookieValue = new HttpCookieManager.CookieValue();
                cookieValue.setName(cookieKeyValueArray[0]);
                cookieValue.setValue(cookieKeyValueArray[1]);
                cookieValue.setDomain(wholeHost);
                cookieValue.setPath("/");
                cookieValues.add(cookieValue);
            }
            break;
        }
        httpCookieManager.setCookies(cookieValues);
        return httpCookieManager;
    }

    /**
     * postman脚本转义的请求中，每一个请求的header都放入HeaderManager中进行管理，该方法提取出header并封装到管理器中返回
     *
     * @param postmanRequest postman脚本转义过来的请求数据
     * @param title          请求名称
     * @return HttpHeaderManager，封装了被管理的header
     */
    private HttpHeaderManager getHttpHeaderManager(PostmanRequest postmanRequest, String title) {
        HttpHeaderManager httpHeaderManager = new HttpHeaderManager();
        httpHeaderManager.setTitle(title + HTTP_HEADER_MANAGER_SUFFIX);
        List<PostmanRequestHeader> postmanHeaders = postmanRequest.getHeader();
        List<HttpHeaderManager.Header> headerDefines = new ArrayList<>();
        for (PostmanRequestHeader postmanHeader : postmanHeaders) {
            if (COOKIE_HEADER_NAME.equalsIgnoreCase(postmanHeader.getKey())) {
                continue;
            }
            HttpHeaderManager.Header headerDefine = new HttpHeaderManager.Header();
            headerDefine.setName(postmanHeader.getKey());
            headerDefine.setValue(postmanHeader.getValue());
            headerDefines.add(headerDefine);
        }
        httpHeaderManager.setHeaders(headerDefines);
        return httpHeaderManager;
    }
}
