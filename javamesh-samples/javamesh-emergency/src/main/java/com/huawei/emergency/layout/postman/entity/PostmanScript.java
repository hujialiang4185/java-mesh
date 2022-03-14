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

package com.huawei.emergency.layout.postman.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.huawei.emergency.layout.postman.entity.request.PostmanRequestDefine;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述：对应postman脚本所有数据，反序列化postman脚本时使用
 *
 * @author zl
 * @since 2022-03-10
 */
@Data
public class PostmanScript {
    /**
     * 对应postman脚本中info字段
     */
    @JSONField(name = "info")
    private PostmanScriptInfo postmanScriptInfo;

    /**
     * 对应postman脚本中item字段，即postman中http请求列表
     */
    @JSONField(name = "item")
    private List<PostmanRequestDefine> postmanRequests = new ArrayList<>();
}
