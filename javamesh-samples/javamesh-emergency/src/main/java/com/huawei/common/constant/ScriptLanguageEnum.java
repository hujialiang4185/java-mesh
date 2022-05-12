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
    /**
     * shell脚本
     */
    SHELL("Shell", "0", ScriptTypeEnum.NORMAL, "sh", "Shell"),
    /**
     * jython脚本
     */
    JYTHON("Jython", "1", ScriptTypeEnum.NORMAL, "py", "Jython"),
    /**
     * groovy脚本
     */
    GROOVY("Groovy", "2", ScriptTypeEnum.NORMAL, "groovy", "Groovy"),
    /**
     * gui脚本
     */
    GUI("GUI", "3", ScriptTypeEnum.GUI, "groovy", "GUI"),
    /**
     * 压测jython脚本
     */
    PERF_JYTHON("jython", "4", ScriptTypeEnum.IDE, "py", "IDE_Jython"),
    /**
     * 压测groovy脚本
     */
    PERF_GROOVY("groovy", "5", ScriptTypeEnum.IDE, "groovy", "IDE_Groovy"),
    /**
     * 压测groovy_maven工程脚本
     */
    PERF_GROOVY_MAVEN("groovy_maven", "6", ScriptTypeEnum.IDE, "groovy", "Groovy_Maven"),
    /**
     * JavaScript脚本
     */
    JAVASCRIPT("JavaScript", "7", ScriptTypeEnum.NORMAL, "js", "JavaScript");

    private String language;
    private String value;
    private ScriptTypeEnum scriptType;
    private String suffix;
    private String view;

    ScriptLanguageEnum(String language, String value, ScriptTypeEnum scriptType, String suffix, String view) {
        this.language = language;
        this.value = value;
        this.scriptType = scriptType;
        this.suffix = suffix;
        this.view = view;
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
