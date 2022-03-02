/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.emergency.mapper;

import com.huawei.emergency.entity.EmergencyScript;
import com.huawei.emergency.entity.EmergencyScriptExample;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 脚本mapper
 *
 * @author h30009881
 * @since 2021-11-15
 **/
@Mapper
public interface EmergencyScriptMapper {
    long countByExample(EmergencyScriptExample example);

    int deleteByExample(EmergencyScriptExample example);

    int deleteByPrimaryKey(Integer scriptId);

    int insert(EmergencyScript record);

    int insertSelective(EmergencyScript record);

    List<EmergencyScript> selectByExample(EmergencyScriptExample example);

    EmergencyScript selectByPrimaryKey(Integer scriptId);

    int updateByExampleSelective(@Param("record") EmergencyScript record,
        @Param("example") EmergencyScriptExample example);

    int updateByExample(@Param("record") EmergencyScript record, @Param("example") EmergencyScriptExample example);

    int updateByPrimaryKeySelective(EmergencyScript record);

    int updateByPrimaryKey(EmergencyScript record);

    List<EmergencyScript> listScript(String userName, String auth, String scriptName, String scriptUser, String status,
        String group);

    EmergencyScript getScriptInfo(int scriptId);

    @Select("select script_name from emergency_script where script_id = #{scriptId}")
    String selectScriptNameById(int scriptId);

    List<String> searchScript(String scriptName, String userName, String auth, String status, List<String> scriptTypes,
        String groupName);

    EmergencyScript getScriptByName(String scriptName);

    String selectUserById(int scriptId);
}