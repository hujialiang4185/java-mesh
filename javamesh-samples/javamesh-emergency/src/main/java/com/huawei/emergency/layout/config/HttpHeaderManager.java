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

package com.huawei.emergency.layout.config;

import com.huawei.emergency.layout.ElementProcessContext;
import com.huawei.emergency.layout.template.GroovyMethodTemplate;
import lombok.Data;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author y30010171
 * @since 2021-12-15
 **/
@Data
public class HttpHeaderManager extends Config {
    private Map<String,String> headers = new HashMap<>();

    @Override
    public void handle(ElementProcessContext context) {
        GroovyMethodTemplate beforeProcessMethod = context.getTemplate().getBeforeProcessMethod();
        headers.forEach( (key,value) -> {
            beforeProcessMethod.addContent(String.format(Locale.ROOT,"headers.add(new NVPair(\"%s\", \"%s\"))",key,value),2);
        });
    }
}
