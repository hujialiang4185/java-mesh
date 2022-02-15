/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.common.config;

import com.alibaba.fastjson.JSONObject;
import com.huawei.common.util.UserFeignClient;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;

/**
 * 错误页面配置
 *
 * @since 2021-10-30
 */
@Configuration
public class CommonConfig {
    public static final String GRINDER_FOLDER = "emergency";
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonConfig.class);
    private static final ThreadLocal<HttpServletRequest> REQUESTS = new ThreadLocal<>();

    @Autowired
    private UserFeignClient userFeignClient;

    @Bean
    public TomcatServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(connector -> connector.setProperty("relaxedQueryChars", "[]"));
        return factory;
    }


    @Bean("passwordRestTemplate")
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        final HttpClient httpClient = HttpClientBuilder.create()
            .setRedirectStrategy(new LaxRedirectStrategy())
            .build();
        requestFactory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(requestFactory);
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        restTemplate.setUriTemplateHandler(defaultUriBuilderFactory);
        restTemplate.setInterceptors(Arrays.asList((httpRequest, bytes, clientHttpRequestExecution) -> {
            HttpServletRequest request = currentRequest();
            if (request != null) {
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    String headerValue = request.getHeader(headerName);
                    LOGGER.debug("set header: {}={}", headerName, headerValue);
                    httpRequest.getHeaders().add(headerName, headerValue);
                }
            } else {
                try {
                    JSONObject loginResult = userFeignClient.login("admin", "admin", "cn", "Asia/Shanghai");
                    if (loginResult != null && Boolean.parseBoolean(loginResult.get("success").toString())) {
                        String sessionId = loginResult.getOrDefault("JSESSIONID", "").toString();
                        httpRequest.getHeaders().add("Cookie", "JSESSIONID=" + sessionId);
                        LOGGER.debug("set header: Cookie={}", sessionId);
                    } else {
                        LOGGER.error("failed to get cookie. {}", loginResult);
                    }
                } catch (RestClientException e) {
                    LOGGER.error("failed to get cookie. {}", e.getMessage());
                }
            }
            return clientHttpRequestExecution.execute(httpRequest, bytes);
        }));
        return restTemplate;
    }

    public static void setRequest(HttpServletRequest request) {
        REQUESTS.set(request);
    }

    public static HttpServletRequest currentRequest() {
        return REQUESTS.get();
    }
}
