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

import com.huawei.emergency.layout.HandlerContext;
import com.huawei.emergency.layout.template.GroovyFieldTemplate;
import lombok.Data;

/**
 * @author y30010171
 * @since 2021-12-15
 **/
@Data
public class CsvDataSetConfig implements Config {

    private String fileName;
    private String fileEncoding;
    private String variableNames; // not empty
    private boolean ignoreFirstLine = false;
    private String delimiter = ",";
    private boolean allowQuoteData = false;
    private boolean recycleOnEof = true;
    private boolean stopOnEof = false;
    private String sharingMode;

    @Override
    public void handle(HandlerContext context) {
        if (!context.isInitParams()){
            context.getTemplate().addFiled(GroovyFieldTemplate.create("public Map<String,Object> allVariable = new HashMap<>();"));
        }
        // todo 生成调用代码：调用csv函数 获取参数map，将其填充进实例变量 allVariable
    }
}
