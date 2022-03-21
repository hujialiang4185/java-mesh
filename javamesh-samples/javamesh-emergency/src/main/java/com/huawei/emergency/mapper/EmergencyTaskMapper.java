package com.huawei.emergency.mapper;

import com.huawei.emergency.dto.TaskCommonReport;
import com.huawei.emergency.entity.EmergencyTask;
import com.huawei.emergency.entity.EmergencyTaskExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmergencyTaskMapper {
    long countByExample(EmergencyTaskExample example);

    int deleteByExample(EmergencyTaskExample example);

    int deleteByPrimaryKey(Integer taskId);

    int insert(EmergencyTask record);

    int insertSelective(EmergencyTask record);

    List<EmergencyTask> selectByExample(EmergencyTaskExample example);

    EmergencyTask selectByPrimaryKey(Integer taskId);

    int updateByExampleSelective(@Param("record") EmergencyTask record, @Param("example") EmergencyTaskExample example);

    int updateByExample(@Param("record") EmergencyTask record, @Param("example") EmergencyTaskExample example);

    int updateByPrimaryKeySelective(EmergencyTask record);

    int updateByPrimaryKey(EmergencyTask record);

    long countPassedPlanByTaskId(Integer id);

    int tryClearTaskNo(Integer planId);

    int selectMaxSubTaskNo(String preTaskNo);

    List<TaskCommonReport> getTaskReportByRecordId(Integer recordId);
}