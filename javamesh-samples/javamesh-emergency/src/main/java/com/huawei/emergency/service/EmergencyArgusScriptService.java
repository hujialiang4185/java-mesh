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

package com.huawei.emergency.service;

import com.huawei.common.api.CommonResult;
import com.huawei.emergency.dto.ArgusScript;
import com.huawei.emergency.layout.TreeResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author y30010171
 * @since 2021-12-27
 **/
public interface EmergencyArgusScriptService {
    CommonResult createArgusOrchestrate(String userName,ArgusScript script);

    /**
     * 获取压测脚本的编排脚本信息
     *
     * @param path 压测脚本路径
     * @return
     */
    CommonResult getArgusOrchestrate(String userName,String path);

    /**
     * 更新压测脚本的编排脚本信息
     *
     * @param userName
     * @param treeResponse
     * @return
     */
    CommonResult updateArgusOrchestrate(String userName, TreeResponse treeResponse);
}
