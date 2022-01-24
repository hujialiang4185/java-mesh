/*
 * Copyright (C) Ltd. 2021-2021. Huawei Technologies Co., All rights reserved
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

package com.huawei.emergency.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 任务执行报告
 *
 * @author y30010171
 * @since 2022-01-24
 **/
@NoArgsConstructor
@Data
public class TaskCommonReport {

    /**
     * 测试名称
     */
    private String testName;
    /**
     * 状态值
     */
    private String status;
    /**
     * 状态标签
     */
    private String statusLabel;
    /**
     * 标签集合
     */
    private List<String> label;
    /**
     * 描述
     */
    private String desc;
    /**
     * 运行时间
     */
    private String duration;
    /**
     * 虚拟用户数
     */
    private Integer vuser;
    /**
     * TPS
     */
    private Double tps;
    /**
     * TPS峰值
     */
    private Double tpsPeak;
    /**
     * 平均时间
     */
    private Double avgTime;
    /**
     * 测试数量
     */
    private Integer testCount;
    /**
     * 测试成功数量
     */
    private Integer successCount;
    /**
     * 测试失败数量
     */
    private Integer failCount;
    /**
     * 测试备注
     */
    private String testComment;
    /**
     * 日志文件集合
     */
    private List<String> logName;
    /**
     * 执行日志
     */
    private List<String> progressMessage;
}
