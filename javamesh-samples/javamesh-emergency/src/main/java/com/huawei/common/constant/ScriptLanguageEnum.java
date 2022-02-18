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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 脚本语言枚举
 *
 * @author y30010171
 * @since 2022-01-11
 **/
@Getter
public enum ScriptLanguageEnum {

    SHELL("Shell", "0", ScriptTypeEnum.NORMAL,"sh"),
    JYTHON("Jython", "1", ScriptTypeEnum.NORMAL,"py"),
    GROOVY("Groovy", "2", ScriptTypeEnum.NORMAL,"groovy"),
    GUI("GUI", "3", ScriptTypeEnum.GUI,"groovy"),
    PERF_JYTHON("jython", "4", ScriptTypeEnum.IDE,"py"),
    PERF_GROOVY("groovy", "5", ScriptTypeEnum.IDE,"groovy"),
    PERF_GROOVY_MAVEN("groovy_maven", "6", ScriptTypeEnum.IDE,"groovy"),
    JAVASCRIPT("JavaScript", "7", ScriptTypeEnum.NORMAL, "js");

    private String language;
    private String value;
    private ScriptTypeEnum scriptType;
    private String suffix;


    ScriptLanguageEnum(String language, String value, ScriptTypeEnum scriptType, String suffix) {
        this.language = language;
        this.value = value;
        this.scriptType = scriptType;
        this.suffix = suffix;
    }

    public static ScriptLanguageEnum matchByValue(String value) {
        if (value == null) {
            return null;
        }
        for (ScriptLanguageEnum item : values()) {
            if (item.getValue().toLowerCase(Locale.ROOT).equals(value.toLowerCase(Locale.ROOT))) {
                return item;
            }
        }
        return null;
    }

    public static ScriptLanguageEnum match(String language, ScriptTypeEnum scriptType) {
        if (language == null || scriptType == null) {
            return null;
        }
        if ("Groovy Maven Project".equals(language)) {
            return PERF_GROOVY_MAVEN;
        }
        for (ScriptLanguageEnum item : ScriptLanguageEnum.matchScriptType(Arrays.asList(scriptType))) {
            if (item.getLanguage().toLowerCase(Locale.ROOT).equals(language.toLowerCase(Locale.ROOT))) {
                return item;
            }
        }
        return null;
    }

    public static List<ScriptLanguageEnum> matchScriptType(List<ScriptTypeEnum> scriptTypes) {
        List<ScriptLanguageEnum> result = new ArrayList<>();
        if (scriptTypes == null) {
            return result;
        }
        for (ScriptLanguageEnum item : ScriptLanguageEnum.values()) {
            if (scriptTypes.contains(item.getScriptType())) {
                result.add(item);
            }
        }
        return result;
    }
}
