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

import org.apache.commons.lang.StringUtils;

/**
 * mysql的模糊查询时特殊字符转义
 *
 * @author h30009881
 * @since 2022-01-01
 */
public class EscapeUtil {
    private EscapeUtil() {
    }

    public static String escapeChar(String before) {
        String result = before;
        if (StringUtils.isNotBlank(result)) {
            result = result.replaceAll("_", "/_").replaceAll("%", "/%");
        }
        return result;
    }
}
