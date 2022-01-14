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
import com.huawei.emergency.entity.EmergencyResource;
import com.huawei.emergency.layout.HandlerFactory;
import com.huawei.emergency.service.EmergencyResourceService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author y30010171
 * @since 2022-01-14
 **/
@RestController
@RequestMapping("/api/resource")
public class EmergencyResourceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmergencyResourceController.class);

    @Autowired
    EmergencyResourceService resourceService;

    @PostMapping
    public CommonResult upload(@RequestParam(value = "script_id", required = false) Integer scriptId,
                               @RequestParam(value = "path", required = false) String path,
                               @RequestParam(value = "file") MultipartFile file, HttpServletResponse response) throws IOException {
        if (file == null || StringUtils.isEmpty(file.getOriginalFilename())) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return CommonResult.failed("请选择要上传的文件");
        }
        if (scriptId == null && StringUtils.isEmpty(path)) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return CommonResult.failed("请选择脚本");
        }
        CommonResult upload;
        if (scriptId != null) {
            upload = resourceService.upload(scriptId, file.getOriginalFilename(), file.getInputStream());
        } else {
            LOGGER.warn("can't upload path resource");
            upload = CommonResult.failed("压测脚本不能在此操作资源");
        }
        if (!upload.isSuccess()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return upload;
    }

    @GetMapping("/{pathOrId}/{filename}")
    public void download(HttpServletResponse response,
                                 @PathVariable(value = "pathOrId") String pathOrId,
                                 @PathVariable(value = "filename") String fileName) {
        try {
            int resourceId = Integer.valueOf(pathOrId);
        } catch (NumberFormatException e) {
            LOGGER.error("can't download path resource");
        }
    }

    @DeleteMapping("/{pathOrId}/{filename}")
    public CommonResult delete(@PathVariable(value = "pathOrId") String pathOrId,
                               @PathVariable(value = "filename") String fileName) {
        try {
            int resourceId = Integer.valueOf(pathOrId);
        } catch (NumberFormatException e) {
            LOGGER.error("can't delete path resource");
            return CommonResult.failed("压测脚本不能在此操作资源");
        }
        return CommonResult.success();
    }
}
