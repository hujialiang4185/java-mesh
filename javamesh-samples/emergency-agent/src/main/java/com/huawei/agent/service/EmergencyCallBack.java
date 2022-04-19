/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.agent.service;


import com.huawei.agent.entity.ExecResult;


/**
 * 回调函数
 **/
public interface EmergencyCallBack {
    void onComplete(ExecResult execResult);
}
