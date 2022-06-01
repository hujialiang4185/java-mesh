/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.emergency.service;

import com.huawei.common.api.CommonPage;
import com.huawei.common.api.CommonResult;
import com.huawei.emergency.dto.AgentConfigDTO;
import com.huawei.emergency.entity.EmergencyAgentConfig;
import com.huawei.emergency.entity.EmergencyServer;

import java.util.List;

/**
 * 服务器信息管理
 *
 * @author y30010171
 * @since 2021-11-29
 **/
public interface EmergencyServerService extends EmergencyCommonService<EmergencyServer> {
    /**
     * 从平台获取该服务器所有可用账号
     *
     * @param serverIp
     * @return
     */
    CommonResult allServerUser(String serverIp);

    /**
     * 查询已创建的服务器信息
     *
     * @param params
     * @return
     */
    CommonResult queryServerInfo(String groupName, CommonPage<EmergencyServer> params, String keyword,
        int[] excludeServerIds, int[] includeAgentIds, String agentType);

    CommonResult search(String groupName, String serverName);

    CommonResult license(EmergencyServer server);

    CommonResult deleteServerList(String[] serverIds, String userName);

    /**
     * 安装agent代理
     *
     * @param serverIds 服务器id
     * @return
     */
    CommonResult install(List<Integer> serverIds);

    /**
     * 保存agent的启动配置
     *
     * @param agentConfigDto {@link AgentConfigDTO agentConfigDto}
     * @return {@link CommonResult}
     */
    CommonResult saveAgentConfig(EmergencyAgentConfig config);

    /**
     * 查询agent的启动配置
     *
     * @param agentId agent id
     * @return {@link CommonResult}
     */
    CommonResult queryAgentConfig(int agentId);

    /**
     * agentType agent类型 gui / normal
     *
     * @param params 请求分页参数
     * @param agentType agent类型 normal / gui
     * @param excludeAgentIds 排除的id
     * @param agentName agent名称或者IP
     * @return
     */
    CommonResult getActiveAgent(CommonPage params, String agentType, int[] excludeAgentIds, String agentName);
}
