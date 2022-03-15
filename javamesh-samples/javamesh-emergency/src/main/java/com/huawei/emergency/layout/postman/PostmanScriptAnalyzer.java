/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.emergency.layout.postman;

import com.huawei.emergency.layout.postman.entity.PostmanScript;
import com.huawei.emergency.layout.postman.entity.request.PostmanRequest;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 功能描述：定义postman脚本分析接口，提供分析方法
 *
 * @author zl
 * @since 2022-03-10
 */
public interface PostmanScriptAnalyzer {
    /**
     * 通过处理postman脚本输入流，把脚本内容处理成能直接转换GUI脚本的javabean,
     * 每一个请求封装到{@link PostmanRequest}
     *
     * @param postmanScriptStream postman脚本输入流
     * @param charset             postman脚本字符集
     * @return 处理之后的脚本信息
     */
    PostmanScript processPostmanScript(InputStream postmanScriptStream, Charset charset);
}
