/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.emergency.controller;

import com.huawei.common.api.CommonResult;
import com.huawei.common.constant.FailedInfo;
import com.huawei.common.constant.ResultCode;
import com.huawei.emergency.dto.ScriptManageDto;
import com.huawei.emergency.entity.EmergencyExecRecord;
import com.huawei.emergency.entity.EmergencyScript;
import com.huawei.emergency.entity.JwtUser;
import com.huawei.emergency.layout.TreeResponse;
import com.huawei.emergency.service.EmergencyExecService;
import com.huawei.emergency.service.EmergencyScriptService;
import com.huawei.logaudit.aop.WebOperationLog;
import com.huawei.logaudit.constant.OperationDetails;
import com.huawei.logaudit.constant.OperationTypeEnum;
import com.huawei.logaudit.constant.ResourceType;
import com.huawei.script.exec.ExecResult;
import com.huawei.script.exec.log.LogResponse;

import io.swagger.annotations.Api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * 脚本管理controller
 *
 * @author h30009881
 * @since 2021-10-14
 */
@Api(tags = "脚本管理")
@RestController
@RequestMapping("/api/script")
public class EmergencyScriptController {
    private static final String SUCCESS = "success";

    @Autowired
    private EmergencyScriptService service;

    @Autowired
    private EmergencyExecService execService;

    @GetMapping
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.SELECT,
        operationDetails = OperationDetails.QUERY_SCRIPT_LIST)
    public CommonResult<List<EmergencyScript>> listScript(
        UsernamePasswordAuthenticationToken authentication,
        @RequestParam(value = "script_name", required = false) String scriptName,
        @RequestParam(value = "owner", required = false) String scriptUser,
        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
        @RequestParam(value = "current", defaultValue = "1") int current,
        @RequestParam(value = "sorter", required = false) String sorter,
        @RequestParam(value = "order", required = false) String order,
        @RequestParam(value = "status", required = false) String status) {
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        return service.listScript(jwtUser, scriptName, scriptUser, pageSize, current, sorter, order, status);
    }

    /**
     * 脚本删除
     *
     * @param scriptId
     * @return {@link CommonResult}
     */
    @DeleteMapping
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.DELETE,
        operationDetails = OperationDetails.DELETE_SCRIPT)
    public CommonResult deleteScript(@RequestParam(value = "script_id") int[] scriptId) {
        return service.deleteScripts(scriptId);
    }

    /**
     * 脚本下载
     *
     * @param scriptId
     * @param response
     */
    @GetMapping("/download")
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.DOWNLOAD,
        operationDetails = OperationDetails.DOWNLOAD_SCRIPT)
    public void downloadScript(@RequestParam(value = "script_id") int scriptId, HttpServletResponse response) {
        service.downloadScript(scriptId, response);
    }

    /**
     * 上传文件
     *
     * @param file
     * @return {@link CommonResult} 上传结果
     */
    @PostMapping("/upload")
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.UPLOAD,
        operationDetails = OperationDetails.UPLOAD_SCRIPT)
    public CommonResult uploadScript(UsernamePasswordAuthenticationToken authentication,
        @RequestParam(value = "script_name") String scriptName,
        @RequestParam(value = "submit_info") String submitInfo,
        @RequestParam(value = "account", required = false) String serverUser,
        @RequestParam(value = "server_ip", required = false) String serverIp,
        @RequestParam(value = "has_pwd", required = false) String havePassword,
        @RequestParam(value = "language") String scriptType,
        @RequestParam(value = "param", required = false) String param,
        @RequestParam(value = "public") String isPublic,
        @RequestParam(value = "pwd", required = false) String password,
        @RequestParam(value = "pwd_from", required = false) String passwordMode,
        @RequestParam(value = "file") MultipartFile file) {
        EmergencyScript script = new EmergencyScript();
        script.setScriptName(scriptName);
        script.setSubmitInfo(submitInfo);
        script.setServerUser(serverUser);
        script.setServerIp(serverIp);
        script.setHavePassword(havePassword);
        script.setScriptType(scriptType);
        script.setParam(param);
        script.setIsPublic(isPublic);
        script.setPassword(password);
        script.setPasswordMode(passwordMode);
        int result = service.uploadScript(((JwtUser) authentication.getPrincipal()).getUserEntity(), script, file);
        if (result == ResultCode.SCRIPT_NAME_EXISTS) {
            return CommonResult.failed(FailedInfo.SCRIPT_NAME_EXISTS);
        } else if (result == ResultCode.PARAM_INVALID) {
            return CommonResult.failed(FailedInfo.PARAM_INVALID);
        } else if (result == ResultCode.FAIL || result == 0) {
            return CommonResult.failed(FailedInfo.SCRIPT_CREATE_FAIL);
        } else {
            return CommonResult.success(result);
        }
    }

    /**
     * 获取脚本实例
     *
     * @param scriptId
     * @return CommonResult 脚本信息
     */
    @GetMapping("/get")
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.SELECT,
        operationDetails = OperationDetails.QUERYING_SCRIPT_INSTANCES)
    public CommonResult<EmergencyScript> selectScript(@RequestParam(value = "script_id") int scriptId) {
        EmergencyScript script = service.selectScript(scriptId);
        if (script == null) {
            return CommonResult.failed(FailedInfo.SCRIPT_NOT_EXISTS);
        }
        return CommonResult.success(script);
    }

    @PostMapping
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.CREATE,
        operationDetails = OperationDetails.INSERT_SCRIPT)
    public CommonResult insertScript(UsernamePasswordAuthenticationToken authentication,
        @RequestBody EmergencyScript script) {
        int result = service.insertScript(((JwtUser) authentication.getPrincipal()).getUserEntity(), script);
        if (result == ResultCode.SCRIPT_NAME_EXISTS) {
            return CommonResult.failed(FailedInfo.SCRIPT_NAME_EXISTS);
        } else if (result == ResultCode.PARAM_INVALID) {
            return CommonResult.failed(FailedInfo.PARAM_INVALID);
        } else if (result == ResultCode.FAIL) {
            return CommonResult.failed(FailedInfo.SCRIPT_CREATE_FAIL);
        } else {
            return CommonResult.success(result);
        }
    }

    @PutMapping
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.UPDATE,
        operationDetails = OperationDetails.UPDATE_SCRIPT)
    public CommonResult updateScript(@RequestBody EmergencyScript script) {
        int count = service.updateScript(script);
        if (count == 1) {
            return CommonResult.success(count);
        } else if (count == ResultCode.SCRIPT_NAME_EXISTS) {
            return CommonResult.failed(FailedInfo.SCRIPT_NAME_EXISTS);
        } else {
            return CommonResult.failed(FailedInfo.UPDATE_SCRIPT_FAIL);
        }
    }

    @GetMapping("/search")
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.SELECT,
        operationDetails = OperationDetails.SEARCH_SCRIPT)
    public CommonResult searchScript(UsernamePasswordAuthenticationToken authentication,
        @RequestParam(value = "value", required = false) String scriptName,
        @RequestParam(value = "status", required = false) String status,
        @RequestParam(value = "type", required = false) String scriptType) {
        List<String> scriptNames = service.searchScript((JwtUser) authentication.getPrincipal(), scriptName, status,
            scriptType);
        return CommonResult.success(scriptNames);
    }

    @GetMapping("/getByName")
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.SELECT,
        operationDetails = OperationDetails.QUERYING_SCRIPT_INSTANCES_BY_NAME)
    public CommonResult getScriptEntityByName(@RequestParam(value = "name") String scriptName) {
        EmergencyScript script = service.getScriptByName(scriptName);
        return CommonResult.success(script);
    }

    @PostMapping("/submitReview")
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.SUBMIT_REVIEW,
        operationDetails = OperationDetails.SUBMIT_SCRIPT_REVIEW)
    public CommonResult submitReview(UsernamePasswordAuthenticationToken authentication,
        @RequestBody EmergencyScript script) {
        String result = service.submitReview((JwtUser) authentication.getPrincipal(), script);
        if (result.equals(SUCCESS)) {
            return CommonResult.success(result);
        } else {
            return CommonResult.failed(result);
        }
    }

    @PostMapping("/approve")
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.AUDIT,
        operationDetails = OperationDetails.AUDIT_SCRIPT)
    public CommonResult approve(UsernamePasswordAuthenticationToken authentication,
        @RequestBody Map<String, Object> map) {
        int count = service.approve(((JwtUser) authentication.getPrincipal()).getUsername(), map);
        if (count == 0) {
            return CommonResult.failed(FailedInfo.APPROVE_FAIL);
        } else {
            return CommonResult.success(SUCCESS);
        }
    }

    public CommonResult debugScript(@RequestBody Map<String, Integer> param) {
        return service.debugScript(param.get("script_id"));
    }

    @PostMapping("/debug")
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.EXECUTE,
        operationDetails = OperationDetails.DEBUG_SCRIPT)
    public CommonResult debugScriptBeforeSave(@RequestBody Map<String, String> param) {
        return service.debugScriptBeforeSave(param.get("content"), param.get("server_name"));
    }

    @PostMapping("/debugStop")
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.DISCONTINUE,
        operationDetails = OperationDetails.STOP_DEBUG_SCRIPT)
    public CommonResult debugStop(@RequestBody EmergencyExecRecord param) {
        return service.debugScriptStop(param.getDebugId());
    }

    @GetMapping("/debugLog")
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.EXECUTION_LOG,
        operationDetails = OperationDetails.DEBUG_SCRIPT_LOG)
    public LogResponse debugLog(@RequestParam(value = "debug_id") int id,
        @RequestParam(value = "line", defaultValue = "1") int lineNum) {
        int lineIndex = lineNum;
        if (lineIndex <= 0) {
            lineIndex = 1;
        }
        return service.debugLog(id, lineIndex);
    }

    /**
     * 创建GUI脚本
     *
     * @param script {@link EmergencyScript script}
     * @return
     */
    @PostMapping("/orchestrate")
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.CREATE,
        operationDetails = OperationDetails.INSERT_GUI_SCRIPT)
    public CommonResult createGuiScript(UsernamePasswordAuthenticationToken authentication,
        @RequestBody EmergencyScript script) {
        return service.createGuiScript(((JwtUser) authentication.getPrincipal()).getUserEntity(), script);
    }

    /**
     * 修改GUI脚本
     *
     * @param treeResponse {@link TreeResponse} 编排树
     * @return {@link CommonResult}
     */
    @PutMapping("/orchestrate")
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.UPDATE,
        operationDetails = OperationDetails.UPDATE_GUI_SCRIPT)
    public CommonResult updateGuiScript(UsernamePasswordAuthenticationToken authentication,
        @RequestBody TreeResponse treeResponse) {
        return service.updateGuiScript(((JwtUser) authentication.getPrincipal()).getUsername(), treeResponse);
    }

    /**
     * 查询GUI脚本的编排树
     *
     * @param scriptId 脚本ID
     * @return {@link CommonResult}
     */
    @GetMapping("/orchestrate/get")
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.SELECT,
        operationDetails = OperationDetails.QUERY_GUI_SCRIPT)
    public CommonResult queryGuiScript(@RequestParam("script_id") int scriptId) {
        return service.queryGuiScript(scriptId);
    }

    /**
     * 创建IDE脚本
     *
     * @param scriptManageDto {@link ScriptManageDto} 脚本信息
     * @return
     */
    @PostMapping("/ide")
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.CREATE,
        operationDetails = OperationDetails.CREATE_IDE_SCRIPT)
    public CommonResult createIdeScript(UsernamePasswordAuthenticationToken authentication,
        @RequestBody ScriptManageDto scriptManageDto) {
        return service.createIdeScript(((JwtUser) authentication.getPrincipal()).getUserEntity(), scriptManageDto);
    }

    /**
     * 修改IDE脚本
     *
     * @param scriptManageDto {@link ScriptManageDto} 脚本信息
     * @return
     */
    @PutMapping("/ide")
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.UPDATE,
        operationDetails = OperationDetails.UPDATE_IDE_SCRIPT)
    public CommonResult updateIdeScript(@RequestBody ScriptManageDto scriptManageDto) {
        return updateScript(scriptManageDto);
    }

    /**
     * 获取IDE脚本
     *
     * @param scriptId 脚本ID
     * @return
     */
    @GetMapping("/ide/get")
    @WebOperationLog(resourceType = ResourceType.SCRIPT_MANAGEMENT,
        operationType = OperationTypeEnum.SELECT,
        operationDetails = OperationDetails.GET_IDE_SCRIPT)
    public CommonResult createIdeScript(@RequestParam("script_id") Integer scriptId) {
        return selectScript(scriptId);
    }

    @PostMapping("/execComplete")
    public CommonResult execComplete(@RequestBody ExecResult execResult) {
        if (execResult.getDetailId() == 0) {
            return CommonResult.failed("detailId is valid. ");
        }
        return execService.execComplete(execResult);
    }
}
