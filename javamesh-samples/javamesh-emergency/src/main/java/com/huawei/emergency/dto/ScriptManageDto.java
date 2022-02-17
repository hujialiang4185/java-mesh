/*
 * Copyright (C) Ltd. 2021-2021. Huawei Technologies Co., All rights reserved
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.huawei.emergency.entity.EmergencyScript;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 创建压测ide脚本
 *
 * @author y30010171
 * @since 2022-02-15
 **/
@NoArgsConstructor
@Data
public class ScriptManageDto extends EmergencyScript {

    /**
     * 被检测方法
     */
    private String method;
    /**
     * 被检测的URL
     */
    private String forUrl;
    /**
     * 创建资源库目录
     */
    private boolean hasResource;
    /**
     * http请求头设置
     */
    private List<ParamsDTO> headers;
    /**
     * http请求cookies设置
     */
    private List<CookiesDTO> cookies;
    /**
     * http请求参数设置
     */
    private List<ParamsDTO> params;
    /**
     * 脚本类型
     */
    private String type;
    /**
     * 编排类型
     */
    private String orchestrateType;

    /**
     * CookiesDTO
     */
    @NoArgsConstructor
    @Data
    public static class CookiesDTO {
        /**
         * name
         */
        @JsonProperty("key")
        private String name;
        /**
         * value
         */
        private String value;
        /**
         * domain
         */
        @JsonProperty("value_a")
        private String domain;
        /**
         * path
         */
        @JsonProperty("value_b")
        private String path;
    }

    /**
     * ParamsDTO
     */
    @NoArgsConstructor
    @Data
    public static class ParamsDTO {
        /**
         * key
         */
        @JsonProperty("key")
        private String name;
        /**
         * value
         */
        private String value;
    }
}
