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
 * 脚本语言枚举
 *
 * @author y30010171
 * @since 2022-01-11
 **/
@Getter
public enum ScriptLanguageEnum {

    SHELL("Shell"),
    GROOVY("Groovy"),
    PYTHON("Python"),
    JAVASCRIPT("JavaScript");

    private String value;

    ScriptLanguageEnum(String value) {
        this.value = value;
    }

    public static ScriptLanguageEnum match(String value) {
        if (value == null) {
            return null;
        }
        for (ScriptLanguageEnum item : ScriptLanguageEnum.values()) {
            if (item.getValue().toLowerCase(Locale.ROOT).equals(value.toLowerCase(Locale.ROOT))) {
                return item;
            }
        }
        return null;
    }
}
