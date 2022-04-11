/*
 * Copyright (C) Ltd. 2022-2022. Huawei Technologies Co., All rights reserved
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

package com.huawei.user.entity;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 日志实体类
 *
 * @author h30009881
 * @since 2022-03-30
 */
@Data
public class LogAuditEntity {
    private Integer logId;
    /**
     * 资源类型(模块名称)
     */
    private String resourceType;
    /**
     * 操作类型(增删改查)
     */
    private String operationType;
    /**
     * 级别(提示、一般、警告、危险、高危) 0-提示、 1-一般、 2-警告、 3-危险、 4-高危
     */
    private int level;

    private String levelLabel;
    /**
     * 操作结果(成功、失败) 1-成功；0-失败
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
