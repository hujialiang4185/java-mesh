/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

/**
 * 服务器资源报告
 *
 * @author y30010171
 * @since 2022-01-24
 **/
@NoArgsConstructor
@Data
public class TaskResourceReport {
    /**
     * IP地址
     */
    private String ip;
    /**
     * CPU核数
     */
    private Integer cpu;
    /**
     * 内存大小,单位G
     */
    private Double memory;
    /**
     * 启动时长,单位H
     */
    private Double startUp;
    /**
     * CPU利用率
     */
    private Double cpuUsage;
    /**
     * 内存利用率
     */
    private Double memoryUsage;
    /**
     * IO繁忙率
     */
    private Double ioBusy;
    /**
     * cpuUser
     */
    private Double cpuUser;
    /**
     * cpuSys
     */
    private Double cpuSys;
    /**
     * cpuWait
     */
    private Double cpuWait;
    /**
     * cpuIdle
     */
    private Double cpuIdle;
    /**
     * memoryTotal
     */
    private Double memoryTotal;
    /**
     * memorySwap
     */
    private Double memorySwap;
    /**
     * memoryBuffers
     */
    private Double memoryBuffers;
    /**
     * memoryUsed
     */
    private Double memoryUsed;
    /**
     * diskRead
     */
    private Double diskRead;
    /**
     * diskWrite
     */
    private Double diskWrite;
    /**
     * diskBusy
     */
    private Double diskBusy;
    /**
     * 网络读速率
     */
    private Double networkRbyte;
    /**
     * 网络写速率
     */
    private Double networkWbyte;
    /**
     * memoryRpackage
     */
    private Double memoryRpackage;
    /**
     * memoryWpackage
     */
    private Double memoryWpackage;
}
