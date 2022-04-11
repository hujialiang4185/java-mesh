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

package com.huawei.user.controller;

/**
 * 日志审计controller
 *
 * @author y30010171
 * @since 2022-04-07
 **/

import com.huawei.user.common.api.CommonResult;
import com.huawei.user.service.LogAuditService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logAudit")
public class LogAuditController {

    @Autowired
    LogAuditService logAuditService;

    @GetMapping
    public CommonResult queryLogAudit(@RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
        @RequestParam(value = "current", defaultValue = "1") int current,
        @RequestParam(value = "keywords", required = false) String keyword) {
        return logAuditService.queryLogAudit(pageSize, current, keyword);
    }
}
