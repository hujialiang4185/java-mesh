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

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述：每一个postman请求定义，封装了实际的请求信息，响应信息和配置信息等
 *
 * @author zl
 * @since 2022-03-11
 */
@Data
public class PostmanRequestDefine {
    /**
     * 该请求的名称
     */
    private String name;

    /**
     * 实际请求信息
     */
    @JSONField(name = "request")
    private PostmanRequest postmanRequest;

    /**
     * 请求的响应信息
     */
    private List<Object> response = new ArrayList<>();
}
