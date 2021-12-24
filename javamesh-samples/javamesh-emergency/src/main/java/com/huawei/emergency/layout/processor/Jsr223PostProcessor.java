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

package com.huawei.emergency.layout.processor;

import com.huawei.emergency.layout.HandlerContext;
import lombok.Data;

/**
 * @author y30010171
 * @since 2021-12-16
 **/
@Data
public class Jsr223PostProcessor implements PostProcessor{

    private String name;
    private String comment;
    private String language;
    private String parameters;
    private String fileName;
    private boolean cacheCompiled;
    private String script;

    @Override
    public void handle(HandlerContext context) {
    }
}
