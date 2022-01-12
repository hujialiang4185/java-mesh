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

import java.util.List;

/**
 * @author y30010171
 * @since 2021-12-15
 **/
@Data
@Deprecated
public class HttpRequestDefault extends Config {

    private String protocol = "http";
    private String hostName;
    private int port;
    private String path;
    private String contentEncoding;


    private List<Parameters> defaultParams;
    private String body;

    @Override
    public void handle(ElementProcessContext context) {
    }

    @Data
    public static class Parameters{
        private String name;
        private String value;
        private boolean urlEncode;
        private String contentType;
        private boolean includeEquals;
    }
}
