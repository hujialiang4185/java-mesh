package com.huawei.emergency.mapper;

import com.huawei.logaudit.entity.LogAuditEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 日志审计mapper
 *
 * @author h30009881
 * @since 2022-03-16
 **/
@Mapper
public interface EmergencyLogAuditMapper {
    List<LogAuditEntity> queryLogAuditList();

    int addLogAudit(LogAuditEntity logAudit);
}
