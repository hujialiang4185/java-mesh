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

package com.huawei.emergency.layout.postman.entity.request;

import com.huawei.emergency.layout.postman.entity.MapTypeValue;
import lombok.Data;

/**
 * 功能描述：postman中url参数
 *
 * @author zl
 * @since 2022-03-11
 */
@Data
public class PostmanRequestQueryParam extends MapTypeValue {
    /**
     * 相等
     */
    private boolean equals = true;

    /**
     * 参数描述
     */
    private String description;
}
