package com.huawei.logaudit.entity;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 日志实体类
 *
 * @author zhengbin zhao
 * @since 2021-03-09
 */
@Data
public class LogAuditEntity {
    /**
     * 资源类型(模块名称)
     */
    private String resourceType;
    /**
     * 操作类型(增删改查)
     */
    private String operationType;
    /**
     * 级别(提示、一般、警告、危险、高危)
     * 0-提示、
     * 1-一般、
     * 2-警告、
     * 3-危险、
     * 4-高危
     */
    private int level;
    /**
     * 操作结果(成功、失败)
     * 1-成功；0-失败
     */
    private String operationResults;
    /**
     * 操作人
     */
    private String operationPeople;
    /**
     * IP地址
     */
    private String ipAddress;
    /**
     * 操作详情(具体接口名称)
     */
    private String operationDetails;
    /**
     * 操作时间戳
     */
    private Timestamp operationDate;
}
