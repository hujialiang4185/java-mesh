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

    SHELL("0", "Shell", "sh", "普通shell脚本"),
    GROOVY("1", "Groovy", "groovy", "普通groovy脚本"),
    PYTHON("2", "Python", "py", "普通python脚本"),
    GUI("3", "GUI", "groovy", "脚本编排产生的压测groovy脚本"),
    IDE_GROOVY("4", "IDE_Groovy", "groovy", "压测groovy脚本"),
    IDE_PYTHON("5", "IDE_Python", "py", "压测python脚本"),
    IDE_GROOVY_MAVEN("6", "IDE_Groovy_Maven", "groovy", "压测groovy的mvn工程脚本"),
    JAVASCRIPT("7", "JavaScript", "js", "普通js脚本");

    private String value;
    private String suffix;
    private String desc;
    private String type;

    ScriptLanguageEnum(String value, String type, String suffix, String desc) {
        this.value = value;
        this.suffix = suffix;
        this.desc = desc;
        this.type = type;
    }

    public static ScriptLanguageEnum match(String type) {
        if (type == null) {
            return null;
        }
        for (ScriptLanguageEnum item : ScriptLanguageEnum.values()) {
            if (item.getType().toLowerCase(Locale.ROOT).equals(type.toLowerCase(Locale.ROOT))) {
                return item;
            }
        }
        return null;
    }
}
