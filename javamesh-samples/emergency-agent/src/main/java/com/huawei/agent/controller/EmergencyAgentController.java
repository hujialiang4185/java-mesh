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

package com.huawei.agent.controller;

import com.huawei.agent.common.api.CommonResult;
import com.huawei.agent.entity.ExecParam;
import com.huawei.agent.service.EmergencyAgentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * agent controller
 *
 * @author h3009881
 * @since 2021-12-17
 **/
@RestController
public class EmergencyAgentController {
    @Autowired
    private EmergencyAgentService service;

    @PostMapping("/execute")
    public CommonResult exec(HttpServletRequest request, @RequestBody ExecParam execParam) {
        return service.exec(request, execParam);
    }

    @PostMapping("cancel")
    public CommonResult cancel(@RequestBody ExecParam execParam) {
        return service.cancel(execParam.getDetailId(), execParam.getScriptType());
    }
}
