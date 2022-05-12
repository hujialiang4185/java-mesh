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

package com.huawei.common.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 预案状态枚举
 *
 * @author y30010171
 * @since 2021-11-26
 **/
@Getter
public enum PlanStatus {
    /**
     * 新增状态
     */
    NEW("0", "unapproved", "待提审"),
    /**
     * 审核中状态
     */
    APPROVING("1", "approving", "待审核"),
    /**
     * 审核通过状态
     */
    APPROVED("2", "approved", "审核通过"),
    /**
     * 审核被拒绝状态
     */
    REJECT("3", "unapproved", "驳回"),
    /**
     * 运行中状态
     */
    RUNNING("4", "running", "运行中"),
    /**
     * 运行成功状态
     */
    SUCCESS("5", "ran", "运行成功"),
    /**
     * 运行失败状态
     */
    FAILED("6", "ran", "运行失败"),
    /**
     * 运行失败状态
     */
    SCHEDULED("7", "wait", "已预约");

    /**
     * 没有通过审核的状态的集合
     */
    public static final List<String> UN_PASSED_STATUS = Arrays.asList(
        NEW.getValue(),
        APPROVING.getValue(),
        REJECT.getValue()
    );

    private String value;
    private String status;
    private String statusLabel;

    PlanStatus(String value, String status, String statusLabel) {
        this.value = value;
        this.status = status;
        this.statusLabel = statusLabel;
    }

    public static PlanStatus matchByLabel(String statusLabel, PlanStatus defaultStatus) {
        for (PlanStatus status : PlanStatus.values()) {
            if (status.getStatusLabel().equals(statusLabel)) {
                return status;
            }
        }
        return defaultStatus;
    }
}
