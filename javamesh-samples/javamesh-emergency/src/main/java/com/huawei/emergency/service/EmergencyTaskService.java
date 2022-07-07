/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.emergency.service;

import com.huawei.common.api.CommonResult;
import com.huawei.emergency.entity.EmergencyExecRecord;
import com.huawei.emergency.entity.EmergencyScript;
import com.huawei.emergency.entity.EmergencyTask;
import com.huawei.test.ShareTest;

import java.util.List;

/**
 * 任务管理接口
 *
 * @author y30010171
 * @since 2021-11-04
 **/
public interface EmergencyTaskService extends EmergencyCommonService<EmergencyTask>, EmergencyCallBack {
    /**
     * 任务是否存在
     *
     * @param taskId 任务id
     * @return 是否存在
     */
    boolean isTaskExist(int taskId);

    /**
     * 判断任务是否为共享任务
     *
     * @param taskId 任务id
     * @return 是否共享
     */
    boolean isTaskShared(Integer taskId);

    /**
     * 获取单个压测任务的执行情况
     *
     * @param perfTestId 性能测试id
     * @return {@link CommonResult} 压测报告
     */
    CommonResult getCommonReport(Long perfTestId);

    /**
     * 获取某次执行下各agent的压测报告
     *
     * @param recordId {@link EmergencyExecRecord#getRecordId()} 执行记录id
     * @return {@link CommonResult} 压测报告集合
     */
    CommonResult getTaskReport(Integer recordId);

    /**
     * 获取压测任务 性能测试指标的折线图数据
     *
     * @param perfTestId 性能测试id
     * @param step 指标颗粒度
     * @return {@link CommonResult}
     */
    CommonResult getMetricsReport(long perfTestId, int step);

    /**
     * 通过项目和任务获取此项目下 该任务的子任务的脚本集合
     *
     * @param taskId 任务id
     * @return 子任务的脚本集合
     */
    List<EmergencyScript> getAllScriptOnTaskShared(Integer taskId);

    /**
     * 生成调用所有脚本的代码模板，需要方法名及类名满足标准格式，同时实现{@link ShareTest#invokeTest()}
     *
     * @param allScriptInfo 所有脚本信息
     * @return 生成的代码
     */
    String generateScriptOnTaskShared(List<EmergencyScript> allScriptInfo);
}
