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

package com.huawei.emergency.controller;

import com.huawei.common.api.CommonPage;
import com.huawei.common.api.CommonResult;
import com.huawei.emergency.dto.ServerAgentInfoDTO;
import com.huawei.emergency.dto.ServerDto;
import com.huawei.emergency.entity.EmergencyAgentConfig;
import com.huawei.emergency.entity.EmergencyServer;
import com.huawei.emergency.entity.JwtUser;
import com.huawei.emergency.service.EmergencyServerService;
import com.huawei.logaudit.aop.WebOperationLog;
import com.huawei.logaudit.constant.OperationDetails;
import com.huawei.logaudit.constant.OperationTypeEnum;
import com.huawei.logaudit.constant.ResourceType;

import io.swagger.annotations.Api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 压测引擎
 *
 * @author y30010171
 * @since 2021-12-07
 **/
@Api(tags = "主机管理")
@RestController
@RequestMapping("/api/host")
public class EmergencyServerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmergencyServerController.class);

    @Autowired
    EmergencyServerService serverService;

    @GetMapping("/search/password_uri")
    public CommonResult allServerUser(@RequestParam(value = "server_ip", required = false) String serverIp) {
        return serverService.allServerUser(serverIp);
    }

    @GetMapping("/search")
    public CommonResult allServerName(UsernamePasswordAuthenticationToken authentication,
        @RequestParam(value = "value", required = false) String serverName) {
        return serverService.search(((JwtUser) authentication.getPrincipal()).getGroupName(), serverName);
    }

    @PostMapping
    @WebOperationLog(resourceType = ResourceType.SERVER_MANAGEMENT,
        operationType = OperationTypeEnum.CREATE,
        operationDetails = OperationDetails.CREATE_SERVER)
    public CommonResult createServer(UsernamePasswordAuthenticationToken authentication,
        @RequestBody EmergencyServer server) {
        if ("有".equals(server.getHavePassword())) {
            server.setHavePassword("1");
            if ("平台".equals(server.getPasswordMode())) {
                server.setPasswordMode("1");
            } else if ("本地".equals(server.getPasswordMode())) {
                server.setPasswordMode("0");
            }
        } else if ("无".equals(server.getHavePassword())) {
            server.setHavePassword("0");
        }
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        server.setCreateUser(jwtUser.getUsername());
        server.setServerGroup(jwtUser.getGroupName());
        return serverService.add(server);
    }

    @DeleteMapping
    @WebOperationLog(resourceType = ResourceType.SERVER_MANAGEMENT,
        operationType = OperationTypeEnum.DELETE,
        operationDetails = OperationDetails.DELETE_SERVER)
    public CommonResult deleteServer(UsernamePasswordAuthenticationToken authentication,
        @RequestParam(value = "server_id[]", required = false) String[] serverIds) {
        return serverService.deleteServerList(serverIds, ((JwtUser) authentication.getPrincipal()).getUsername());
    }

    @PostMapping("/install")
    public CommonResult installServer(@RequestBody ServerDto serverDto) {
        if (serverDto == null) {
            return CommonResult.success();
        }
        return serverService.install(serverDto.getServerId());
    }

    @PostMapping("/license")
    public CommonResult license(@RequestBody EmergencyServer server) {
        return serverService.license(server);
    }

    @GetMapping
    @WebOperationLog(resourceType = ResourceType.SERVER_MANAGEMENT,
        operationType = OperationTypeEnum.SELECT,
        operationDetails = OperationDetails.QUERY_SERVER_INFO)
    public CommonResult queryServerInfo(UsernamePasswordAuthenticationToken authentication,
        @RequestParam(value = "keywords", required = false) String nameOrIp,
        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
        @RequestParam(value = "current", defaultValue = "1") int current,
        @RequestParam(value = "sorter", defaultValue = "create_time") String sorter,
        @RequestParam(value = "order", defaultValue = "DESC") String order,
        @RequestParam(value = "server_name", required = false) String serverName,
        @RequestParam(value = "excludes[]", required = false) int[] excludeServerIds,
        @RequestParam(value = "includes[]", required = false) int[] includeAgentIds,
        @RequestParam(value = "type", required = false) String agentType) {
        CommonPage<ServerAgentInfoDTO> params = new CommonPage<>();
        params.setPageSize(pageSize);
        params.setPageIndex(current);
        params.setSortField(sorter);
        if ("ascend".equals(order)) {
            params.setSortType("ASC");
        } else if ("descend".equals(order)) {
            params.setSortType("DESC");
        }
        params.setObject(new ServerAgentInfoDTO());
        params.getObject().setServerName(serverName);
        params.getObject().setServerGroup(((JwtUser) authentication.getPrincipal()).getGroupName());
        params.getObject().setAgentType(agentType);
        return serverService.queryServerInfo(params, nameOrIp,
            excludeServerIds, includeAgentIds);
    }

    @GetMapping("/agent_active/{agent_type}")
    public CommonResult getActiveAgent(
        @PathVariable("agent_type") String agentType,
        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
        @RequestParam(value = "current", defaultValue = "1") int current,
        @RequestParam(value = "excludes[]", required = false) int[] excludeAgentIds,
        @RequestParam(value = "agent_name", required = false) String nameOrIp) {
        CommonPage<ServerAgentInfoDTO> params = new CommonPage<>();
        params.setPageSize(pageSize);
        params.setPageIndex(current);
        params.setObject(new ServerAgentInfoDTO());
        params.getObject().setGroupName(
            ((JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getGroupName());
        params.getObject().setAgentType(agentType);
        return serverService.getActiveAgent(params, excludeAgentIds, nameOrIp);
    }

    @GetMapping("/agent_config")
    public CommonResult queryAgentConfig(@RequestParam("agent_id") int agentId) {
        return serverService.queryAgentConfig(agentId);
    }

    @PostMapping("/agent_config")
    public CommonResult saveAgentConfig(@RequestBody EmergencyAgentConfig config) {
        return serverService.saveAgentConfig(config);
    }
}
