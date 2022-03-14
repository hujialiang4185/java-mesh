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
import com.huawei.emergency.layout.postman.entity.request.body.BinaryBody;
import com.huawei.emergency.layout.postman.entity.request.body.FormDataBody;
import com.huawei.emergency.layout.postman.entity.request.body.UrlencodedBody;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述：postman请求中请求体数据
 *
 * @author zl
 * @since 2022-03-10
 */
@Data
public class PostmanRequestBody {
    /**
     * body体类型
     */
    private String mode;

    /**
     * 文本类型数据
     */
    private String raw;

    /**
     * form格式数据保存到这个字段
     */
    @JSONField(name = "formdata")
    private List<FormDataBody> formDataBody = new ArrayList<>();

    /**
     * urlencoded表单格式数据保存到这个字段
     */
    @JSONField(name = "urlencoded")
    private List<UrlencodedBody> urlencodedBody = new ArrayList<>();

    /**
     * file表单格式数据保存到这个字段
     */
    @JSONField(name = "file")
    private BinaryBody binaryBody;
}
