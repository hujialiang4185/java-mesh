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

package com.huawei.emergency.controller;

import com.huawei.common.api.CommonResult;
import com.huawei.emergency.dto.ArgusScript;
import com.huawei.emergency.layout.TreeResponse;
import com.huawei.emergency.service.EmergencyArgusScriptService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 用于原压测脚本管理页面
 * 脚本编辑框
 *
 * @author y30010171
 * @since 2021-12-27
 **/
@Api(tags = "压测脚本管理")
@RestController
@RequestMapping("/api/script/argus/")
public class EmergencyArgusScriptController {

    @Autowired
    EmergencyArgusScriptService argusScriptService;

    /**
     * 生成压测脚本的编排模板
     *
     * @param script {@link ArgusScript} 压测脚本信息
     * @return {@link CommonResult}
     */
    @ApiOperation(value = "生成压测脚本的编排模板", notes = "用于第一次编排时，生成压测脚本的编排模板", hidden = true)
    @PostMapping("/orchestrate")
    public CommonResult createArgusOrchestrate(@RequestBody ArgusScript script) {
        return argusScriptService.createArgusOrchestrate(script);
    }

    /**
     * 获取压测脚本的编排模板，如果为空则返回默认模板
     *
     * @param path 压测脚本路径
     * @return {@link CommonResult}
     */
    @ApiOperation(value = "获取压测脚本的编排模板", notes = "获取压测脚本的编排模板，如果不存在则返回一个默认模板")
    @GetMapping("/orchestrate")
    public CommonResult getArgusOrchestrate(@RequestParam("path") String path) {
        return argusScriptService.getArgusOrchestrate(path);
    }

    /**
     * 保存压测脚本模板，返回生成的脚本信息
     *
     * @param request      {@link HttpServletRequest} 请求信息
     * @param treeResponse {@link TreeResponse} 编排模板数据
     * @return {@link CommonResult}
     */
    @ApiOperation(value = "更新压测脚本的编排模板", notes = "更新压测脚本的编排模板，同时返回生成的脚本内容")
    @PutMapping("/orchestrate")
    public CommonResult updateArgusOrchestrate(HttpServletRequest request, @RequestBody TreeResponse treeResponse) {
        return argusScriptService.updateArgusOrchestrate(request, treeResponse);
    }
}
