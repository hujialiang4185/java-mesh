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

package com.huawei.emergency.service.impl;

import com.huawei.common.api.CommonResult;
import com.huawei.common.constant.ValidEnum;
import com.huawei.emergency.dto.ResourceDto;
import com.huawei.emergency.entity.EmergencyResource;
import com.huawei.emergency.entity.EmergencyResourceExample;
import com.huawei.emergency.mapper.EmergencyResourceMapper;
import com.huawei.emergency.service.EmergencyResourceService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;

/**
 * @author y30010171
 * @since 2022-01-14
 **/
@Service
@Transactional
public class EmergencyResourceServiceImpl implements EmergencyResourceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmergencyResourceServiceImpl.class);

    @Autowired
    EmergencyResourceMapper resourceMapper;

    @Value("${script.resource.uploadPath}")
    private String uploadPath;

    @Override
    public CommonResult upload(String userName, int scriptId, String originalFilename, InputStream inputStream) {
        File resourceDirectory = new File(uploadPath + File.separatorChar + scriptId);
        if (!resourceDirectory.exists()) {
            resourceDirectory.mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(
            resourceDirectory.getPath() + File.separatorChar + originalFilename)) {
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (FileNotFoundException e) {
            LOGGER.error("Can't found upload file. scriptId={},fileName={}", scriptId, originalFilename);
            return CommonResult.failed("无法找到上传文件");
        } catch (IOException e) {
            LOGGER.error("Can't upload file. scriptId={},fileName={}. {}", scriptId, originalFilename, e.getMessage());
            return CommonResult.failed("上传文件失败");
        }
        EmergencyResourceExample sameResource = new EmergencyResourceExample();
        sameResource.createCriteria()
            .andScriptIdEqualTo(scriptId).andResourceNameEqualTo(originalFilename);
        EmergencyResource updateInvalid = new EmergencyResource();
        updateInvalid.setIsValid(ValidEnum.IN_VALID.getValue());
        resourceMapper.updateByExampleSelective(updateInvalid, sameResource); // 将资源名称相同的记录删除

        EmergencyResource resource = new EmergencyResource();
        resource.setResourceName(originalFilename);
        resource.setScriptId(scriptId);
        resource.setCreateUser(userName);
        resourceMapper.insertSelective(resource);
        ResourceDto dto = new ResourceDto();
        dto.setUid(resource.getResourceId());
        return CommonResult.success(dto);
    }

    @Override
    public void refreshResource(int scriptId, List<String> resourceList) {
        List<Integer> currentResourceId = new ArrayList<>();
        if (resourceList != null) {
            for (String resourceStr : resourceList) {
                String[] resourceIdAndName = resourceStr.split("/");
                if (resourceIdAndName.length == 0) {
                    continue;
                }
                try {
                    currentResourceId.add(Integer.valueOf(resourceIdAndName[0]));
                } catch (NumberFormatException e) {
                    LOGGER.error("cast resourceId error.", e);
                    return;
                }
            }
        }
        EmergencyResourceExample needDeleteResource = new EmergencyResourceExample();
        EmergencyResourceExample.Criteria criteria = needDeleteResource.createCriteria()
            .andScriptIdEqualTo(scriptId)
            .andIsValidEqualTo(ValidEnum.VALID.getValue());
        if (currentResourceId.size() > 0) {
            criteria.andResourceIdNotIn(currentResourceId);
        }
        List<EmergencyResource> emergencyResources = resourceMapper.selectByExample(needDeleteResource);
        emergencyResources.forEach(this::deleteResource);
    }

    @Override
    public CommonResult download(int resourceId, String resourceName, ServletOutputStream outputStream) {
        if (StringUtils.isEmpty(resourceName)) {
            return CommonResult.failed("文件名称为空");
        }
        EmergencyResource resource = resourceMapper.selectByPrimaryKey(resourceId);
        if (resource == null || resource.getScriptId() == null || !resourceName.equals(resource.getResourceName())) {
            return CommonResult.failed("请选择正确的资源");
        }
        try (InputStream resourceIn = new FileInputStream(
            uploadPath + File.separatorChar + resource.getScriptId() + File.separatorChar
                + resource.getResourceName())) {
            IOUtils.copy(resourceIn, outputStream);
        } catch (FileNotFoundException e) {
            LOGGER.error("can't found resource. resourceId={}, scriptId={}, resourceName={}", resourceId,
                resource.getScriptId(), resourceName);
            return CommonResult.failed("资源不存在");
        } catch (IOException e) {
            LOGGER.error("can't download resource. resourceId={}, scriptId={}, resourceName={}. {}", resourceId,
                resource.getScriptId(), resourceName, e.getMessage());
            return CommonResult.failed("资源下载错误");
        }
        return CommonResult.success();
    }

    @Override
    public CommonResult deleteResourceByIdAndName(int resourceId, String fileName) {
        EmergencyResource resource = resourceMapper.selectByPrimaryKey(resourceId);
        if (resource == null || ValidEnum.IN_VALID.equals(resource.getIsValid())) {
            return CommonResult.failed("请选择正确的资源");
        }
        if (StringUtils.isEmpty(fileName) || !fileName.equals(resource.getResourceName())) {
            return CommonResult.failed("资源名不一致");
        }
        deleteResource(resource);
        return CommonResult.success();
    }

    /**
     * 删除资源文件，同时删除与脚本的关系
     *
     * @param resource
     */
    public void deleteResource(EmergencyResource resource) {
        File resourceFile = new File(
            uploadPath + File.separatorChar + resource.getScriptId() + File.separatorChar + resource.getResourceName());
        resourceFile.delete();
        LOGGER.info("delete resource file, scriptId={}, resourceId={}, resourceName={}.", resource.getScriptId(),
            resource.getResourceId(), resource.getResourceName());
        EmergencyResource updateResource = new EmergencyResource();
        updateResource.setResourceId(resource.getResourceId());
        updateResource.setIsValid(ValidEnum.IN_VALID.getValue());
        resourceMapper.updateByPrimaryKeySelective(updateResource);
    }
}
