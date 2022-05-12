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

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 文件工具类
 *
 * @since 2021-10-30
 */
@Slf4j
public class FileUtil {
    private FileUtil() {
    }

    public static String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        // new一个StringBuffer用于字符串拼接
        StringBuilder sb = new StringBuilder();
        String line = null;

        // 当输入流内容读取完毕时
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            log.error("Exception occurs. Exception info:{}", e.getMessage());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                log.error("close InputStream error.", e);
            }
            try {
                reader.close();
            } catch (IOException e) {
                log.error("close BufferedReader error.", e);
            }
        }
        return sb.toString();
    }
}
