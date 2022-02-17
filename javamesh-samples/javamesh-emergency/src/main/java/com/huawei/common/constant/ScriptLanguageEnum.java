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

    SHELL("Shell", "0", ScriptTypeEnum.NORMAL),
    JYTHON("Jython", "1", ScriptTypeEnum.NORMAL),
    GROOVY("Groovy", "2", ScriptTypeEnum.NORMAL),
    GUI("GUI", "3", ScriptTypeEnum.GUI),
    PERF_JYTHON("jython", "4", ScriptTypeEnum.IDE),
    PERF_GROOVY("groovy", "5", ScriptTypeEnum.IDE),
    PERF_GROOVY_MAVEN("groovy_maven", "6", ScriptTypeEnum.IDE),
    JAVASCRIPT("JavaScript", "7", ScriptTypeEnum.NORMAL);

    private String language;
    private String value;
    private ScriptTypeEnum scriptType;


    ScriptLanguageEnum(String language, String value, ScriptTypeEnum scriptType) {
        this.language = language;
        this.value = value;
        this.scriptType = scriptType;
    }

    public static ScriptLanguageEnum match(String language, ScriptTypeEnum scriptType) {
        if (language == null) {
            return null;
        }
        if ("Groovy Maven Project".equals(language)) {
            return PERF_GROOVY_MAVEN;
        }
        for (ScriptLanguageEnum item : ScriptLanguageEnum.matchScriptType(scriptType)) {
            if (item.getLanguage().toLowerCase(Locale.ROOT).equals(language.toLowerCase(Locale.ROOT))) {
                return item;
            }
        }
        return null;
    }

    public static List<ScriptLanguageEnum> matchScriptType(ScriptTypeEnum scriptType) {
        List<ScriptLanguageEnum> result = new ArrayList<>();
        for (ScriptLanguageEnum item : ScriptLanguageEnum.values()) {
            if (item.getScriptType() == scriptType) {
                result.add(item);
            }
        }
        return result;
    }
}
