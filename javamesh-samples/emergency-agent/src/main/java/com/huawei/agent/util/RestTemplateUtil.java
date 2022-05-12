/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.agent.util;

import com.huawei.agent.entity.ExecResult;

import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * RestTemplate工具类
 *
 * @author h3009881
 * @since 2021-12-17
 **/
@Component
public class RestTemplateUtil {

    private static RestTemplate client;

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        client = restTemplate;
    }

    public static ResponseEntity sendPostRequest(String cookie, String url, ExecResult result) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", cookie);
        HttpEntity httpEntity = new HttpEntity(result, headers);
        return client.exchange(url, HttpMethod.POST, httpEntity, JSONObject.class);
    }
}
