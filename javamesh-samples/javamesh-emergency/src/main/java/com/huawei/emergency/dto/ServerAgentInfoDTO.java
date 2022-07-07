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

import com.huawei.emergency.entity.EmergencyServer;

import lombok.Data;

/**
 * agent in emergency server
 *
 * @author y30010171
 * @since 2022-05-24
 **/
@Data
public class ServerAgentInfoDTO extends EmergencyServer {
    private String id;
    private Integer agentId;
    private String agentName;
    private String agentIp;
    private Integer agentPort;
    private String agentStatus;
    private String agentStatusLabel;
    private String agentType;
    private String agentTypeLabel;
    private String statusLabel;
    private String groupName;
    private String filterActive;
    private Boolean licensed;
}