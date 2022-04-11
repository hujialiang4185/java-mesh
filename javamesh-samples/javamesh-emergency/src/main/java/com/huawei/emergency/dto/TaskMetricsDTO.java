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

import java.util.List;

/**
 * 压测任务的实时指标
 *
 * @author y30010171
 * @since 2022-04-07
 **/
@Data
public class TaskMetricsDTO {
    private List<Long> time;
    private List<Double> tps;
    private List<Long> errors;
    private List<Long> vuser;
    private List<Long> userDefined;
    private List<Double> meanTestTime;
    private List<Double> meanTimeToFirstByte;
}

