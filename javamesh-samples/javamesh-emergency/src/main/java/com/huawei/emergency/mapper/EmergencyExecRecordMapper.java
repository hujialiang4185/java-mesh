package com.huawei.emergency.mapper;

import com.huawei.emergency.entity.EmergencyExecRecord;
import com.huawei.emergency.entity.EmergencyExecRecordExample;
import com.huawei.emergency.entity.EmergencyExecRecordWithBLOBs;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmergencyExecRecordMapper {
    long countByExample(EmergencyExecRecordExample example);

    int deleteByExample(EmergencyExecRecordExample example);

    int deleteByPrimaryKey(Integer recordId);

    int insert(EmergencyExecRecordWithBLOBs record);

    int insertSelective(EmergencyExecRecordWithBLOBs record);

    List<EmergencyExecRecordWithBLOBs> selectByExampleWithBLOBs(EmergencyExecRecordExample example);

    List<EmergencyExecRecord> selectByExample(EmergencyExecRecordExample example);

    EmergencyExecRecordWithBLOBs selectByPrimaryKey(Integer recordId);

    int updateByExampleSelective(@Param("record") EmergencyExecRecordWithBLOBs record, @Param("example") EmergencyExecRecordExample example);

    int updateByExampleWithBLOBs(@Param("record") EmergencyExecRecordWithBLOBs record, @Param("example") EmergencyExecRecordExample example);

    int updateByExample(@Param("record") EmergencyExecRecord record, @Param("example") EmergencyExecRecordExample example);

    int updateByPrimaryKeySelective(EmergencyExecRecordWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(EmergencyExecRecordWithBLOBs record);

    int updateByPrimaryKey(EmergencyExecRecord record);

    List<EmergencyExecRecordWithBLOBs> selectAllPlanDetail(Integer planId);

    int tryUpdateStartTime(@Param("recordId") Integer recordId, @Param("startTime") Date startTime);

    int tryUpdateEndTimeAndLog(@Param("recordId") Integer recordId, @Param("endTime") Date endTime,@Param("log") String log);

    int tryUpdateStatus(Integer recordId);
}