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

import java.util.ArrayList;
import java.util.List;

/**
 * 压测任务的实时指标
 *
 * @author y30010171
 * @since 2022-04-07
 **/
@Data
public class TaskMetricsDTO {
    private List<Long> time = new ArrayList<>();
    private List<Double> tps = new ArrayList<>();
    private List<Long> errors = new ArrayList<>();
    private List<Long> vuser = new ArrayList<>();
    private List<Long> userDefined = new ArrayList<>();
    private List<Double> meanTestTime = new ArrayList<>();
    private List<Double> meanTimeToFirstByte = new ArrayList<>();
}

