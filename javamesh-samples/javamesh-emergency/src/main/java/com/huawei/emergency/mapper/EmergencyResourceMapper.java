package com.huawei.emergency.mapper;

import com.huawei.emergency.entity.EmergencyResource;
import com.huawei.emergency.entity.EmergencyResourceExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmergencyResourceMapper {
    long countByExample(EmergencyResourceExample example);

    int deleteByExample(EmergencyResourceExample example);

    int deleteByPrimaryKey(Integer resourceId);

    int insert(EmergencyResource record);

    int insertSelective(EmergencyResource record);

    List<EmergencyResource> selectByExample(EmergencyResourceExample example);

    EmergencyResource selectByPrimaryKey(Integer resourceId);

    int updateByExampleSelective(@Param("record") EmergencyResource record, @Param("example") EmergencyResourceExample example);

    int updateByExample(@Param("record") EmergencyResource record, @Param("example") EmergencyResourceExample example);

    int updateByPrimaryKeySelective(EmergencyResource record);

    int updateByPrimaryKey(EmergencyResource record);
}