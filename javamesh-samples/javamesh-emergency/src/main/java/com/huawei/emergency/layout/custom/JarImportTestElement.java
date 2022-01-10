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

package com.huawei.emergency.layout.custom;

import com.huawei.emergency.layout.ElementProcessContext;
import com.huawei.emergency.layout.TestElement;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * jar导入组件
 *
 * @author y30010171
 * @since 2021-12-25
 **/
@Data
public class JarImportTestElement extends TestElement {

    private String content;

    @Override
    public void handle(ElementProcessContext context) {
        if (StringUtils.isNotEmpty(content)) {
            context.getTemplate().addImport(this.content);
        }
    }
}
