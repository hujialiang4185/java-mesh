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

import com.huawei.emergency.layout.template.GroovyClassTemplate;
import com.huawei.emergency.layout.template.GroovyMethodTemplate;

/**
 * 处理上下文
 *
 * @author y30010171
 * @since 2021-12-15
 **/
public interface ElementProcessContext {

    /**
     * 获取下一个参数序号，避免变量名称重复
     *
     * @return 参数序号
     */
    int getVariableCount();

    /**
     * 获取当前上下文中所使用的groovy文件模板
     *
     * @return {@link GroovyClassTemplate}
     */
    GroovyClassTemplate getTemplate();

    /**
     * 设置当前上下文中所使用的groovy文件模板
     *
     * @param template {@link GroovyClassTemplate}
     */
    void setTemplate(GroovyClassTemplate template);

    /**
     * 获取当前上下文中正在使用的groovy文件模板中的方法块
     *
     * @return {@link GroovyMethodTemplate}
     */
    GroovyMethodTemplate getCurrentMethod();

    /**
     * 设置当前上下文中，正在使用的groovy文件模板中的方法块。
     * 以便当前节点的子节点知道应该在哪个方法块添加内容
     *
     * @param currentMethod
     */
    void setCurrentMethod(GroovyMethodTemplate currentMethod);

    /**
     * 获取当前http请求产生的request变量名称。
     * 以便子节点中的响应断言使用
     *
     * @return 变量名称
     */
    String getHttpRequestVariableName();

    /**
     * 设置当前http请求产生的request变量名称。
     *
     * @param httpRequestVariableName 变量名称
     */
    void setHttpRequestVariableName(String httpRequestVariableName);

    /**
     * 获取当前http请求产生的response变量名称。
     * 以便子节点中的响应断言使用
     *
     * @return 变量名称
     */
    String getHttpResultVariableName();

    /**
     * 设置当前http请求产生的response变量名称。
     *
     * @param httpResultVariableName 变量名称
     */
    void setHttpResultVariableName(String httpResultVariableName);
}
