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
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author y30010171
 * @since 2021-12-15
 **/
@Data
@Deprecated
public class DnsCacheManager extends Config {

    private static final Pattern pattern = Pattern.compile("(https?|ftp|file)://(.+)(/)");

    private boolean clearCacheEachIteration;
    private boolean useCustomDnsResolvers;
    private List<String> dnsServers = new ArrayList<>();
    private Map<String, String> staticHostTable = new HashMap<>();

    @Override
    public void handle(ElementProcessContext context) {
    }

    public String resolver(String url) {
        if (!useCustomDnsResolvers) {
            return url;
        }
        String result = url;
        for (Map.Entry<String, String> stringStringEntry : staticHostTable.entrySet()) {
            result = result.replaceAll(stringStringEntry.getKey(), stringStringEntry.getValue());
        }
        return result;
    }
}
