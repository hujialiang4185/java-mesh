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

import com.huawei.emergency.entity.EmergencyAgent;
import com.huawei.emergency.entity.EmergencyAgentExample;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * emergency_agent mapper
 *
 * @author yds
 * @since 2022-05-23
 */
@Mapper
public interface EmergencyAgentMapper {
    long countByExample(EmergencyAgentExample example);

    int deleteByExample(EmergencyAgentExample example);

    int deleteByPrimaryKey(Integer agentId);

    int insert(EmergencyAgent record);

    int insertSelective(EmergencyAgent record);

    List<EmergencyAgent> selectByExample(EmergencyAgentExample example);

    EmergencyAgent selectByPrimaryKey(Integer agentId);

    int updateByExampleSelective(@Param("record") EmergencyAgent record,
        @Param("example") EmergencyAgentExample example);

    int updateByExample(@Param("record") EmergencyAgent record, @Param("example") EmergencyAgentExample example);

    int updateByPrimaryKeySelective(EmergencyAgent record);

    int updateByPrimaryKey(EmergencyAgent record);
}