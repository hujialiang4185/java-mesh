/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.emergency.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 压测脚本dto
 *
 * @author y30010171
 * @since 2021-12-26
 **/
@ApiModel(value = "压测脚本对象", description = "压测脚本对象")
@Data
public class ArgusScript {
    @ApiModelProperty(value = "脚本路径", required = true, example = "emergency/test.groovy")
    private String path;

    @ApiModelProperty(value = "提交信息", required = true, example = "第一次commit")
    private String commit;

    @ApiModelProperty(value = "脚本内容", required = true, example = "这是脚本内容")
    private String script;
}