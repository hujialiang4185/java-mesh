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
 * 功能描述：postman中请求url相关信息
 *
 * @author zl
 * @since 2022-03-11
 */
@Data
public class PostmanRequestUrl {
    /**
     * 请求url
     */
    @JSONField(name = "raw")
    private String urlString;

    /**
     * 请求协议
     */
    private String protocol;

    /**
     * 请求host
     */
    private List<String> host = new ArrayList<>();

    /**
     * 请求端口
     */
    private String port;

    /**
     * 请求路径
     */
    private List<String> path = new ArrayList<>();

    /**
     * 请求中url参数
     */
    private List<PostmanRequestQueryParam> query = new ArrayList<>();

    /**
     * 把host组合成完整的ip或则域名，因为postman脚本中，会根据“.”把域名分解成多部分
     *
     * @return 使用"."重新拼接的域名或者ip
     */
    public String getWholeHost() {
        if (host.isEmpty()) {
            return "";
        }
        return String.join(".", host);
    }

    /**
     * 把path组合成完整的ip或则域名，因为postman脚本中，会根据“/”把端口后面的path分解成多部分
     *
     * @return 使用"/"重新拼接的path
     */
    public String getWholePath() {
        if (host.isEmpty()) {
            return "";
        }
        return "/" + String.join("/", path);
    }
}
