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

package com.huawei.emergency.layout.postman.entity.request.body;

import com.huawei.emergency.layout.postman.entity.MapTypeValue;
import lombok.Data;

/**
 * 功能描述：formData类型数据，即有文件也有文本数据
 *
 * @author zl
 * @since 2022-03-11
 */
@Data
public class FormDataBody extends MapTypeValue {
    /**
     * formData类型，分为text和file类型，text就是文本，file就是文件
     */
    private String type;

    /**
     * formData描述
     */
    private String description;
}
