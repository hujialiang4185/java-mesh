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

package com.huawei.emergency.layout;

import com.huawei.emergency.layout.config.DnsCacheManager;
import com.huawei.emergency.layout.config.HttpHeaderManager;
import com.huawei.emergency.layout.config.HttpRequestDefault;
import com.huawei.emergency.layout.template.GroovyClassTemplate;
import com.huawei.emergency.layout.template.GroovyMethodTemplate;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 处理上下文
 *
 * @author y30010171
 * @since 2021-12-15
 **/
@Data
public class ElementProcessContext {
    private AtomicInteger variableCount = new AtomicInteger();
    private Map<String,Object> params = new HashMap<>();
    private GroovyClassTemplate template = new GroovyClassTemplate();
    private boolean initParams = false;
    private GroovyMethodTemplate currentMethod = new GroovyMethodTemplate();
    private List<HttpHeaderManager> headerManagers = new ArrayList<>();
    private List<DnsCacheManager> dnsCacheManagers = new ArrayList<>();
    private List<HttpRequestDefault> httpRequestDefaults = new ArrayList<>();
}
