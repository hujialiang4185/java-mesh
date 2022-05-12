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
 * 标志位枚举
 *
 * @author y30010171
 * @since 2021-11-27
 **/
@Getter
public enum ValidEnum {
    /**
     * 无效标志
     */
    IN_VALID("0", "无效"),
    /**
     * 有效标志
     */
    VALID("1", "有效");

    private String value;
    private String description;

    ValidEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }
}
