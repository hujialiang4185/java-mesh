/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.script.exec.log;

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
    public static final LogResponse END = new LogResponse(null, new String[]{});
    /**
     * 当前日志的行号。需要为null，代表后续没有日志产生了
     */
    private Integer line;
    private String[] data;

    public static LogResponse parse(String logs, int line) {
        if (StringUtils.isEmpty(logs)) {
            return LogResponse.END;
        }
        String[] split = logs.split(System.lineSeparator());
        if (split.length >= line) {
            String[] needLogs = Arrays.copyOfRange(split, line - 1, split.length);
            return new LogResponse(null, needLogs);
        }
        return new LogResponse(null, new String[]{logs});
    }
}
