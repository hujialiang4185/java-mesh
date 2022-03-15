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
import lombok.Data;

/**
 * 功能描述：对应postman脚本中info字段部分数据，反序列化postman脚本时使用
 *
 * @author zl
 * @since 2022-03-10
 */
@Data
public class PostmanScriptInfo {
    /**
     * 对应postman脚本中info中的_postman_id
     */
    @JSONField(name = "_postman_id")
    private String postmanId;

    /**
     * 对应postman脚本中info字段中的name
     */
    private String name;

    /**
     * 对应postman脚本中info字段中的schema
     */
    private String schema;
}
