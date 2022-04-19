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

package com.huawei.agent.util;

import com.alibaba.fastjson.JSONObject;
import com.huawei.agent.entity.ExecResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Component
public class RestTemplateUtil {
    @Autowired
    private RestTemplate restTemplate;

    private static RestTemplate client;

    @PostConstruct
    public void init(){
        client = restTemplate;
    }

    public static ResponseEntity sendPostRequest(String cookie, String url, ExecResult result) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie",cookie);
        HttpEntity httpEntity = new HttpEntity(result, headers);
        return client.exchange(url, HttpMethod.POST, httpEntity, JSONObject.class);
    }
}
