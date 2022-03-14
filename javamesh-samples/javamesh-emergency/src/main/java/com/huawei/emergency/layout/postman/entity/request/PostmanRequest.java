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

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述：对应postman脚本中每一个请求数据，反序列化postman脚本时使用
 *
 * @author zl
 * @since 2022-03-10
 */
@Data
public class PostmanRequest {
    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求header
     */
    private List<PostmanRequestHeader> header = new ArrayList<>();

    /**
     * 请求体
     */
    private PostmanRequestBody body;

    /**
     * 请求url实例，包含了请求协议，链接，host，query参数等
     */
    private PostmanRequestUrl url;

}
