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

package com.huawei.common.util;

import com.huawei.common.config.FeignConfiguration;

import com.alibaba.fastjson.JSONObject;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * feign调用
 *
 * @author h30009881
 * @since 2021-11-13
 **/
@FeignClient(url = "${decisionEngine.url}", name = "login", configuration = FeignConfiguration.class)
public interface UserFeignClient {
    @RequestMapping(value = "/form_login", method = RequestMethod.POST)
    JSONObject login(@RequestParam("j_username") String username,
        @RequestParam("j_password") String password,
        @RequestParam("native_language") String nativeLanguage,
        @RequestParam("user_timezone") String userTimezone);

    @RequestMapping(value = "/user/api/information", method = RequestMethod.GET)
    JSONObject getUserInfo();

    @RequestMapping(value = "/user/api/password", method = RequestMethod.POST)
    String encodePassword(JSONObject object);

    @RequestMapping("/logout")
    String logout();
}
