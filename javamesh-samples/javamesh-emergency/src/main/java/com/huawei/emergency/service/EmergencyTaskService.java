/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.emergency.service;

import com.huawei.common.api.CommonResult;
import com.huawei.emergency.entity.EmergencyTask;

/**
 * 任务管理接口
 *
 * @author y30010171
 * @since 2021-11-04
 **/
public interface EmergencyTaskService extends EmergencyCommonService<EmergencyTask>, EmergencyCallBack {
    boolean isTaskExist(int taskId);

    /**
     * 获取单个压测任务的执行情况
     *
     * @param perfTestId 压测任务ID
     * @return
     */
    CommonResult getCommonReport(Long perfTestId);

    /**
     * 获取任务下各agent的压测报告
     *
     * @param recordId
     * @return {@link CommonResult} 压测报告集合
     */
    CommonResult getTaskReport(Integer recordId);
}
