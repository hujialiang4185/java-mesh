package com.huawei.emergency.mapper;

import com.huawei.emergency.entity.EmergencyExecRecord;
import com.huawei.emergency.entity.EmergencyExecRecordExample;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface EmergencyExecRecordMapper {
    long countByExample(EmergencyExecRecordExample example);

    int deleteByExample(EmergencyExecRecordExample example);

    int deleteByPrimaryKey(Integer recordId);

    int insert(EmergencyExecRecord record);

    int insertSelective(EmergencyExecRecord record);

    List<EmergencyExecRecord> selectByExample(EmergencyExecRecordExample example);

    EmergencyExecRecord selectByPrimaryKey(Integer recordId);

    int updateByExampleSelective(@Param("record") EmergencyExecRecord record,
        @Param("example") EmergencyExecRecordExample example);

    int updateByExample(@Param("record") EmergencyExecRecord record,
        @Param("example") EmergencyExecRecordExample example);

    int updateByPrimaryKeySelective(EmergencyExecRecord record);

    int updateByPrimaryKey(EmergencyExecRecord record);

    List<EmergencyExecRecord> selectAllPlanDetail(Integer planId);

    int tryUpdateStartTime(@Param("recordId") Integer recordId, @Param("startTime") Date startTime);

    int tryUpdateEndTimeAndLog(@Param("recordId") Integer recordId, @Param("endTime") Date endTime,
        @Param("log") String log);

    int tryUpdateStatus(Integer recordId);
}