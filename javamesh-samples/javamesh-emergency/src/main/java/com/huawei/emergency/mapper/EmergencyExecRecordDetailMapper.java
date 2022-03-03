/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.emergency.mapper;

import com.huawei.emergency.entity.EmergencyExecRecordDetail;
import com.huawei.emergency.entity.EmergencyExecRecordDetailExample;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 预案执行记录明细mapper
 *
 * @author y30010171
 * @since 2021-11-15
 **/
@Mapper
public interface EmergencyExecRecordDetailMapper {
    long countByExample(EmergencyExecRecordDetailExample example);

    int deleteByExample(EmergencyExecRecordDetailExample example);

    int deleteByPrimaryKey(Integer detailId);

    int insert(EmergencyExecRecordDetail record);

    int insertSelective(EmergencyExecRecordDetail record);

    List<EmergencyExecRecordDetail> selectByExample(EmergencyExecRecordDetailExample example);

    EmergencyExecRecordDetail selectByPrimaryKey(Integer detailId);

    int updateByExampleSelective(@Param("record") EmergencyExecRecordDetail record,
        @Param("example") EmergencyExecRecordDetailExample example);

    int updateByExample(@Param("record") EmergencyExecRecordDetail record,
        @Param("example") EmergencyExecRecordDetailExample example);

    int updateByPrimaryKeySelective(EmergencyExecRecordDetail record);

    int updateByPrimaryKey(EmergencyExecRecordDetail record);

    List<EmergencyExecRecordDetail> selectAllServerDetail(int recordId);

    int updateLogIfAbsent(@Param("detailId") int detailId, @Param("log") String log);
}