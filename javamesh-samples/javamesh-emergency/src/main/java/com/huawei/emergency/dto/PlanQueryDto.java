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

import java.util.List;

/**
 * 用于描述预案查询的显示信息
 *
 * @author y30010171
 * @since 2021-11-10
 **/
@Data
public class PlanQueryDto {
    private Integer key;
    private Integer planId;
    private Integer historyId;
    private String planNo;
    private String planName;
    private String status;
    private String statusLabel;
    private String checkResult;
    private String checkUser;
    private String approve;
    private String comment;
    private String createTime;
    private String creator;
    private String updateTime;
    private String executeTime;
    private String startTime;
    private String confirm;
    private String groupName;
    private boolean auditable;
    private List<PlanDetailQueryDto> expand;
}
