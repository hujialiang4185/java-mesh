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

package com.huawei.emergency.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

/**
 * 数据结构，用于描述脚本执行时的实时日志
 *
 * @author y30010171
 * @since 2021-10-25
 **/
@Data
@AllArgsConstructor
public class LogResponse {
    private static final String[] EMPTY_ARRAY = new String[0];
    private static final LogResponse END = new LogResponse(null, EMPTY_ARRAY);

    /**
     * 当前日志的行号。需要为null，代表后续没有日志产生了
     */
    private Integer line;
    private String[] data;

    public static LogResponse emptyLog(int line) {
        return new LogResponse(line, EMPTY_ARRAY);
    }

    public static LogResponse parse(String logs, int line) {
        if (StringUtils.isEmpty(logs)) {
            return LogResponse.endLog();
        }
        String[] split = logs.split(System.lineSeparator());
        if (split.length >= line) {
            String[] needLogs = Arrays.copyOfRange(split, line - 1, split.length);
            return new LogResponse(null, needLogs);
        }
        return new LogResponse(null, new String[]{logs});
    }

    public static LogResponse endLog() {
        return END;
    }
}
