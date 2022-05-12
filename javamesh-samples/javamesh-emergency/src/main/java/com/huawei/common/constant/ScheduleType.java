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

/**
 * 调度类型枚举
 *
 * @author y30010171
 * @since 2021-11-19
 **/
@Getter
public enum ScheduleType {
    /**
     * 没有调度，即马上运行
     */
    NONE("0", "NONE"),
    /**
     * 仅调度一次，在固定时间点触发
     */
    ONCE("1", "ONCE"),
    /**
     * 按照corn表达式调度，仅限6位
     */
    CORN("2", "CORN"),
    /**
     * 按照固定时间间隔调度 单位秒
     */
    FIX_DATE("3", "FIX_DATE");

    private String value;
    private String description;

    ScheduleType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static ScheduleType match(String value, ScheduleType defaultItem) {
        for (ScheduleType item : ScheduleType.values()) {
            if (value.equals(item.value)) {
                return item;
            }
        }
        return defaultItem;
    }
}
