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

package com.huawei.common.constant;

import lombok.Getter;

/**
 * agent status enum
 *
 * @author y30010171
 * @since 2022-05-23
 **/
@Getter
public enum AgentStatusEnum {
    /**
     * 下线状态
     */
    INACTIVE("INACTIVE", "INACTIVE"),
    /**
     * 空闲状态
     */
    READY("READY", "READY"),
    /**
     * 运行状态
     */
    PROGRESSING("BUSY", "PROGRESSING");
    private String value;
    private String desc;

    AgentStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
