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

package com.huawei.agent.service;

import com.huawei.agent.common.api.CommonResult;
import com.huawei.agent.entity.ExecParam;

import javax.servlet.http.HttpServletRequest;

/**
 * agent执行脚本接口
 *
 * @author h3009881
 * @since 2021-12-17
 **/
public interface EmergencyAgentService extends EmergencyCallBack {
    CommonResult exec(HttpServletRequest request, ExecParam execParam);

    CommonResult cancel(int recordId, String scriptType);
}
