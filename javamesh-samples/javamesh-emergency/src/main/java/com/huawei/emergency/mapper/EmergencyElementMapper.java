package com.huawei.emergency.mapper;

import com.huawei.emergency.entity.EmergencyElement;
import com.huawei.emergency.entity.EmergencyElementExample;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmergencyElementMapper {
    long countByExample(EmergencyElementExample example);

    int deleteByExample(EmergencyElementExample example);

    int deleteByPrimaryKey(Integer elementId);

    int insert(EmergencyElement record);

    int insertSelective(EmergencyElement record);

    List<EmergencyElement> selectByExample(EmergencyElementExample example);

    EmergencyElement selectByPrimaryKey(Integer elementId);

    int updateByExampleSelective(@Param("record") EmergencyElement record,
        @Param("example") EmergencyElementExample example);

    int updateByExample(@Param("record") EmergencyElement record, @Param("example") EmergencyElementExample example);

    int updateByPrimaryKeySelective(EmergencyElement record);

    int updateByPrimaryKey(EmergencyElement record);
}