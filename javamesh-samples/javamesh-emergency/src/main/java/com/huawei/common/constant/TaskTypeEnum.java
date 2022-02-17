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

package com.huawei.common.constant;

import lombok.Getter;

import java.util.Locale;

/**
 * 任务类型枚举
 *
 * @author y30010171
 * @since 2022-02-08
 **/
@Getter
public enum TaskTypeEnum {
    CUSTOM("1", "自定义脚本压测"),
    FLOW_RECORD("2", "全链路引流压测"),
    COMMAND("3", "命令行脚本");

    private String value;
    private String desc;

    TaskTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static TaskTypeEnum match(String value) {
        if (value == null) {
            return null;
        }
        for (TaskTypeEnum item : TaskTypeEnum.values()) {
            if (item.getValue().toLowerCase(Locale.ROOT).equals(value.toLowerCase(Locale.ROOT))) {
                return item;
            }
        }
        return null;
    }
}
