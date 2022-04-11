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

package com.huawei.user.service.impl;

import com.huawei.user.common.api.CommonResult;
import com.huawei.user.entity.LogAuditEntity;
import com.huawei.user.mapper.EmergencyLogAuditMapper;
import com.huawei.user.service.LogAuditService;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 日志审计service
 *
 * @author y30010171
 * @since 2022-04-07
 **/
@Service
public class LogAuditServiceImpl implements LogAuditService {

    @Autowired
    EmergencyLogAuditMapper logAuditMapper;

    @Override
    public CommonResult queryLogAudit(int pageSize, int current, String keyword) {
        LogAuditEntity logAuditEntity = new LogAuditEntity();
        if (StringUtils.isNotEmpty(keyword)) {
            logAuditEntity.setResourceType(keyword);
        }
        Page<LogAuditEntity> page = PageHelper.startPage(current, pageSize)
            .doSelectPage(() -> logAuditMapper.queryLogAuditList(logAuditEntity));
        return CommonResult.success(page.getResult(), (int) page.getTotal());
    }
}
