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

import java.util.Arrays;
import java.util.List;

/**
 * 脚本执行记录状态枚举类
 *
 * @author y30010171
 * @since 2021-11-18
 **/
@Getter
public enum RecordStatus {
    /**
     * 待执行
     */
    PENDING("0", "待执行"),
    /**
     * 正在执行
     */
    RUNNING("1", "正在执行"),
    /**
     * 执行成功
     */
    SUCCESS("2", "执行成功"),
    /**
     * 执行失败
     */
    FAILED("3", "执行失败"),
    /**
     * 执行取消
     */
    CANCEL("4", "执行取消"),
    /**
     * 人工确认成功
     */
    ENSURE_SUCCESS("5", "人工确认成功"),
    /**
     * 人工确认失败
     */
    ENSURE_FAILED("6", "人工确认失败");

    /**
     * 还处于运行状态的集合
     */
    public static final List<String> HAS_RUNNING_STATUS = Arrays.asList(
        PENDING.getValue(),
        RUNNING.getValue(),
        FAILED.getValue(),
        CANCEL.getValue()
    );

    private String value;
    private String description;

    RecordStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static RecordStatus matchByValue(String value) {
        for (RecordStatus item : RecordStatus.values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
