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

package com.huawei.emergency.mapper;

import com.huawei.emergency.entity.EmergencyAgentConfig;
import com.huawei.emergency.entity.EmergencyAgentConfigExample;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * emergency_agent_config mapper
 *
 * @author yds
 * @since 2022-05-30
 */
@Mapper
public interface EmergencyAgentConfigMapper {
    long countByExample(EmergencyAgentConfigExample example);

    int deleteByExample(EmergencyAgentConfigExample example);

    int deleteByPrimaryKey(Integer agentId);

    int insert(EmergencyAgentConfig record);

    int insertSelective(EmergencyAgentConfig record);

    List<EmergencyAgentConfig> selectByExample(EmergencyAgentConfigExample example);

    EmergencyAgentConfig selectByPrimaryKey(Integer agentId);

    int updateByExampleSelective(@Param("record") EmergencyAgentConfig record,
        @Param("example") EmergencyAgentConfigExample example);

    int updateByExample(@Param("record") EmergencyAgentConfig record,
        @Param("example") EmergencyAgentConfigExample example);

    int updateByPrimaryKeySelective(EmergencyAgentConfig record);

    int updateByPrimaryKey(EmergencyAgentConfig record);
}