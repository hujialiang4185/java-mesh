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
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author y30010171
 * @since 2022-01-24
 **/
@NoArgsConstructor
@Data
public class TaskServiceReport {

    /**
     * 事务名称
     */
    private String transaction;
    /**
     * TPS
     */
    private Integer tps;
    /**
     * 响应时长
     */
    private Integer responseMs;
    /**
     * 成功数
     */
    private Integer successCount;
    /**
     * 失败数
     */
    private Integer failCount;
    /**
     * 失败率
     */
    private String failRate;
}
