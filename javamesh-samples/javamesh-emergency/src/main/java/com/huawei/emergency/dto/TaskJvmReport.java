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

/**
 * Jvm使用报告
 *
 * @author y30010171
 * @since 2022-01-24
 **/
@NoArgsConstructor
@Data
public class TaskJvmReport {

    /**
     * ip
     */
    private String ip;
    /**
     * cpuJava
     */
    private Double cpuJava;
    /**
     * heapInit
     */
    private Double heapInit;
    /**
     * heapMax
     */
    private Double heapMax;
    /**
     * heapUsed
     */
    private Double heapUsed;
    /**
     * heapCommitted
     */
    private Double heapCommitted;
    /**
     * memoryInit
     */
    private Double memoryInit;
    /**
     * memoryMax
     */
    private Double memoryMax;
    /**
     * memoryUsed
     */
    private Double memoryUsed;
    /**
     * memoryCommitted
     */
    private Double memoryCommitted;
    /**
     * jvmCache
     */
    private Double jvmCache;
    /**
     * jvmNewgen
     */
    private Double jvmNewgen;
    /**
     * jvmOldgen
     */
    private Double jvmOldgen;
    /**
     * jvmSurvivor
     */
    private Double jvmSurvivor;
    /**
     * jvmPenmgen
     */
    private Double jvmPenmgen;
    /**
     * jvmMetaspace
     */
    private Double jvmMetaspace;
    /**
     * gcNewc
     */
    private Integer gcNewc;
    /**
     * gcOldc
     */
    private Integer gcOldc;
    /**
     * gcNews
     */
    private Double gcNews;
    /**
     * gcOlds
     */
    private Double gcOlds;
    /**
     * threadCount
     */
    private Integer threadCount;
    /**
     * threadDaemon
     */
    private Integer threadDaemon;
    /**
     * threadPeak
     */
    private Integer threadPeak;
}
