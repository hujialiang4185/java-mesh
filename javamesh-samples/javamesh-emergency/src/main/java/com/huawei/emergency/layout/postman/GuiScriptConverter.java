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

import com.huawei.emergency.layout.TestPlanTestElement;

import java.util.List;

/**
 * 功能描述：把postman脚本转换成gui脚本转换接口
 *
 * @param <T> 需要转换的脚本
 * @author zl
 * @since 2022-03-11
 */
public interface GuiScriptConverter<T> {
    /**
     * 脚本解析之后的对象
     *
     * @param script 脚本内容实例
     * @return gui计划列表
     */
    List<TestPlanTestElement> convertPostmanScript(T script);
}
