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

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 账号密码加解密
 *
 * @author h30009881
 * @since 2021-11-13
 **/
@Component
public class PasswordUtil {
    @Value("${key}")
    private String key;

    public String encodePassword(String password) throws UnsupportedEncodingException {
        AES aes = SecureUtil.aes(key.getBytes(StandardCharsets.UTF_8));

        // AES加密
        byte[] encrypt = aes.encrypt(password);

        // Base64加密
        byte[] encode = Base64.getEncoder().encode(encrypt);
        return new String(encode, "utf-8");
    }

    public String decodePassword(String password) throws UnsupportedEncodingException {
        AES aes = SecureUtil.aes(key.getBytes(StandardCharsets.UTF_8));
        byte[] decode = Base64.getDecoder().decode(password);
        byte[] decrypt = aes.decrypt(decode);
        return new String(decrypt, "utf-8");
    }
}
