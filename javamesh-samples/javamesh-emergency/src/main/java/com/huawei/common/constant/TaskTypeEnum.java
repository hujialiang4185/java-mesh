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
    /**
     * 压测任务
     */
    CUSTOM("1", "自定义脚本压测"),
    /**
     * 引流压测任务
     */
    FLOW_RECORD("2", "全链路引流压测"),
    /**
     * 命令行脚本任务
     */
    COMMAND("3", "命令行脚本"),
    /**
     * 场景 不运行脚本
     */
    SCENE("4", "场景");

    private String value;
    private String desc;

    TaskTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static TaskTypeEnum matchByValue(String value) {
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

    public static TaskTypeEnum matchByDesc(String desc) {
        if (desc == null) {
            return null;
        }
        for (TaskTypeEnum item : TaskTypeEnum.values()) {
            if (item.getDesc().toLowerCase(Locale.ROOT).equals(desc.toLowerCase(Locale.ROOT))) {
                return item;
            }
        }
        return null;
    }
}
