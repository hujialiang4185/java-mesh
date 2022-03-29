/*
 * Copyright (C) Ltd. 2021-2022. Huawei Technologies Co., All rights reserved
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
import com.huawei.emergency.entity.JwtUser;
import com.huawei.emergency.service.EmergencyResourceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * 上传下载编排脚本所依赖的资源
 *
 * @author y30010171
 * @since 2022-01-14
 **/
@Api(tags = "资源管理")
@RestController
@RequestMapping("/api/resource")
public class EmergencyResourceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmergencyResourceController.class);
    private static final String UTF_8 = "UTF-8";
    @Autowired
    EmergencyResourceService resourceService;

    /**
     * 上传依赖资源
     *
     * @param authentication 登录信息
     * @param scriptId 编排脚本的ID
     * @param path 压测脚本路径 暂时不用
     * @param file 资源文件
     * @param response 请求响应
     * @return {@link CommonResult}
     */
    @ApiOperation(value = "上传资源文件", notes = "用于上传脚本运行相关依赖")
    @PostMapping
    public CommonResult upload(UsernamePasswordAuthenticationToken authentication,
        @RequestParam(value = "script_id", required = false) int scriptId,
        @RequestParam(value = "path", defaultValue = "lib") String path,
        @RequestParam(value = "file") MultipartFile file, HttpServletResponse response) throws IOException {
        if (file == null || StringUtils.isEmpty(file.getOriginalFilename())) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return CommonResult.failed("请选择要上传的文件");
        }
        CommonResult upload;
        try {
            upload = resourceService.upload(((JwtUser) authentication.getPrincipal()).getUsername(), scriptId, path,
                file.getOriginalFilename(), file.getInputStream());
        } catch (IOException e) {
            LOGGER.error("can't upload resource.", e);
            upload = CommonResult.failed(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        if (!upload.isSuccess()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return upload;
    }

    /**
     * 下载资源
     *
     * @param resourceId 资源ID
     * @param fileName 资源名称
     * @param response 请求响应
     */
    @ApiOperation(value = "下载资源文件", notes = "用于下载上传后的脚本依赖资源")
    @GetMapping("/{resourceId}/{filename}")
    public void downloadResource(@PathVariable(value = "resourceId") int resourceId,
        @PathVariable(value = "filename") String fileName,
        HttpServletResponse response) {
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            response.setCharacterEncoding(UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition",
                "attachment;fileName=" + new String(URLEncoder.encode(fileName, UTF_8).getBytes(UTF_8)));
            resourceService.download(resourceId, fileName, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            LOGGER.error("can't download path resource", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除资源
     *
     * @param resourceId 资源ID
     * @param fileName 资源名称
     * @return {@link CommonResult}
     */
    @ApiOperation(value = "删除资源文件", notes = "用于删除上传后的脚本相关依赖")
    @DeleteMapping("/{resourceId}/{filename}")
    public CommonResult deleteResource(@PathVariable(value = "resourceId") int resourceId,
        @PathVariable(value = "filename") String fileName) {
        return resourceService.deleteResourceByIdAndName(resourceId, fileName);
    }
}
