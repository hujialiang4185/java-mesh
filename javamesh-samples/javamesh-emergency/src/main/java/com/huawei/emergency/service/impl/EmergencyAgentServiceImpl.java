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

package com.huawei.emergency.service.impl;

import com.huawei.common.constant.AgentStatusEnum;
import com.huawei.common.constant.ValidEnum;
import com.huawei.emergency.entity.EmergencyAgent;
import com.huawei.emergency.entity.EmergencyAgentExample;
import com.huawei.emergency.mapper.EmergencyAgentMapper;
import com.huawei.emergency.service.EmergencyAgentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * emergency agent service impl
 *
 * @author h3009881
 * @since 2021-12-17
 **/
@Service
public class EmergencyAgentServiceImpl implements EmergencyAgentService {
    @Autowired
    private EmergencyAgentMapper agentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmergencyAgent addAgent(EmergencyAgent agent) {
        EmergencyAgentExample agentExample = new EmergencyAgentExample();
        agentExample.createCriteria()
            .andAgentIpEqualTo(agent.getAgentIp())
            .andAgentPortEqualTo(agent.getAgentPort());
        List<EmergencyAgent> agentList = agentMapper.selectByExample(agentExample);
        if (agentList.size() == 0) {
            EmergencyAgent newAgent = new EmergencyAgent();
            newAgent.setAgentName(agent.getAgentName());
            newAgent.setAgentIp(agent.getAgentIp());
            newAgent.setAgentPort(agent.getAgentPort());
            newAgent.setAgentStatus(AgentStatusEnum.READY.getValue());
            agentMapper.insertSelective(newAgent);
            return newAgent;
        }
        EmergencyAgent oldAgent = agentList.get(0);
        oldAgent.setAgentName(agent.getAgentName());
        if (!AgentStatusEnum.PROGRESSING.getValue().equals(oldAgent.getAgentStatus())) {
            oldAgent.setAgentStatus(AgentStatusEnum.READY.getValue());
        }
        agentMapper.updateByPrimaryKeySelective(oldAgent);
        return oldAgent;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAgent(String ip) {
        EmergencyAgentExample agentExample = new EmergencyAgentExample();
        agentExample.createCriteria().andAgentIpEqualTo(ip)
            .andIsValidEqualTo(ValidEnum.VALID.getValue());
        EmergencyAgent agent = new EmergencyAgent();
        agent.setAgentStatus(AgentStatusEnum.INACTIVE.getValue());
        agentMapper.updateByExampleSelective(agent, agentExample);
    }
}
