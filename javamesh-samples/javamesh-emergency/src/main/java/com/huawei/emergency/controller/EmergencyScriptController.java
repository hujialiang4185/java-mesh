/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.emergency.controller;

import com.huawei.common.api.CommonResult;
import com.huawei.common.constant.FailedInfo;
import com.huawei.common.constant.ResultCode;
import com.huawei.emergency.dto.ArgusScript;
import com.huawei.emergency.entity.EmergencyExecRecord;
import com.huawei.emergency.entity.EmergencyScript;
import com.huawei.emergency.layout.TreeResponse;
import com.huawei.emergency.service.EmergencyScriptService;
import com.huawei.script.exec.log.LogResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 脚本管理controller
 *
 * @author h30009881
 * @since 2021-10-14
 */
@RestController
@RequestMapping("/api/script")
public class EmergencyScriptController {
    @Autowired
    private EmergencyScriptService service;

    private static final String SUCCESS = "success";

    @GetMapping
    public CommonResult<List<EmergencyScript>> listScript(
        HttpServletRequest request,
        @RequestParam(value = "script_name", required = false) String scriptName,
        @RequestParam(value = "owner", required = false) String scriptUser,
        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
        @RequestParam(value = "current", defaultValue = "1") int current,
        @RequestParam(value = "sorter", required = false) String sorter,
        @RequestParam(value = "order", required = false) String order,
        @RequestParam(value = "status", required = false) String status) {
        return service.listScript(request, scriptName, scriptUser, pageSize, current, sorter, order, status);
    }

    /**
     * 脚本删除
     *
     * @param scriptId
     * @return {@link CommonResult}
     */
    @DeleteMapping
    public CommonResult deleteScript(@RequestParam(value = "script_id") int[] scriptId) {
        int size = scriptId.length;
        int count = service.deleteScripts(scriptId);
        if (count <= 0) {
            return CommonResult.failed(FailedInfo.DELETE_FAILED);
        } else if (size != count) {
            return CommonResult.failed(FailedInfo.DELETE_NOT_SUCCESS_ALL);
        } else {
            return CommonResult.success(count);
        }
    }

    /**
     * 脚本下载
     *
     * @param scriptId
     * @param response
     */
    @GetMapping("/download")
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
    public CommonResult uploadScript(HttpServletRequest request,
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
        CommonResult<EmergencyScript> uploadResult = service.uploadScript(request, script, file);
        if (uploadResult.isSuccess()) {
            return CommonResult.success(uploadResult.getData().getScriptId());
        }
        return uploadResult;
    }

    /**
     * 获取脚本实例
     *
     * @param scriptId
     * @return CommonResult 脚本信息
     */
    @GetMapping("/get")
    public CommonResult<EmergencyScript> selectScript(@RequestParam(value = "script_id") int scriptId) {
        EmergencyScript script = service.selectScript(scriptId);
        if (script == null) {
            return CommonResult.failed(FailedInfo.SCRIPT_NOT_EXISTS);
        }
        return CommonResult.success(script);
    }

    /**
     * 新增脚本
     *
     * @param request
     * @param script  {@link EmergencyScript}
     * @return {@link CommonResult}
     */
    @PostMapping
    public CommonResult insertScript(HttpServletRequest request, @RequestBody EmergencyScript script) {
        CommonResult<EmergencyScript> insertResult = service.insertScript(request, script);
        if (insertResult.isSuccess()) {
            return CommonResult.success(insertResult.getData().getScriptId());
        }
        return insertResult;
    }

    /**
     * 更新脚本内容
     *
     * @param request 请求
     * @param script  {@link EmergencyScript}
     * @return {@link CommonResult}
     */
    @PutMapping
    public CommonResult updateScript(HttpServletRequest request, @RequestBody EmergencyScript script) {
        CommonResult<EmergencyScript> updateResult = service.updateScript(request, script);
        if (updateResult.isSuccess()) {
            return CommonResult.success(updateResult.getData().getScriptId());
        }
        return updateResult;
    }

    /**
     * 查询脚本
     *
     * @param request
     * @param scriptName
     * @param status
     * @return
     */
    @GetMapping("/search")
    public CommonResult searchScript(HttpServletRequest request,
                                     @RequestParam(value = "value", required = false) String scriptName,
                                     @RequestParam(value = "status", required = false) String status) {
        List<String> scriptNames = service.searchScript(request, scriptName, status);
        return CommonResult.success(scriptNames);
    }

    /**
     * 通过名称查询脚本
     *
     * @param scriptName 脚本名称
     * @return {@link CommonResult}
     */
    @GetMapping("/getByName")
    public CommonResult getScriptEntityByName(@RequestParam(value = "name") String scriptName) {
        EmergencyScript script = service.getScriptByName(scriptName);
        return CommonResult.success(script);
    }

    /**
     * 提交审核
     *
     * @param request 请求
     * @param script  {@link EmergencyScript} 待提审的脚本
     * @return {@link CommonResult}
     */
    @PostMapping("/submitReview")
    public CommonResult submitReview(HttpServletRequest request, @RequestBody EmergencyScript script) {
        String result = service.submitReview(request, script);
        if (result.equals(SUCCESS)) {
            return CommonResult.success(result);
        } else {
            return CommonResult.failed(result);
        }
    }

    /**
     * 审核脚本
     *
     * @param map 脚本ID及审核结果
     * @return {@link CommonResult}
     */
    @PostMapping("/approve")
    public CommonResult approve(@RequestBody Map<String, Object> map) {
        int count = service.approve(map);
        if (count == 0) {
            return CommonResult.failed(FailedInfo.APPROVE_FAIL);
        } else {
            return CommonResult.success(SUCCESS);
        }
    }

    /**
     * 脚本调试
     *
     * @param param 脚本内容及服务器名称
     * @return {@link CommonResult}
     */
    @PostMapping("/debug")
    public CommonResult debugScriptBeforeSave(@RequestBody Map<String, String> param) {
        return service.debugScriptBeforeSave(param.get("content"), param.get("server_name"));
    }

    /**
     * 停止调试
     *
     * @param param {@link EmergencyExecRecord} 调试ID
     * @return
     */
    @PostMapping("/debugStop")
    public CommonResult debugStop(@RequestBody EmergencyExecRecord param) {
        return service.debugScriptStop(param.getDebugId());
    }

    /**
     * 获取调试日志
     * <p> {@link LogResponse#getLine()} == null 代表没有日志 </p>
     *
     * @param id      调试ID
     * @param lineNum 日志行号
     * @return {@link LogResponse}
     */
    @GetMapping("/debugLog")
    public LogResponse debugLog(@RequestParam(value = "debug_id") int id,
                                @RequestParam(value = "line", defaultValue = "1") int lineNum) {
        int lineIndex = lineNum;
        if (lineIndex <= 0) {
            lineIndex = 1;
        }
        return service.debugLog(id, lineIndex);
    }

    /**
     * 创建编排脚本
     *
     * @param script {@link EmergencyScript} 脚本信息
     * @return {@link CommonResult}
     */
    @PostMapping("/orchestrate")
    public CommonResult createOrchestrate(@RequestBody EmergencyScript script) {
        return service.createOrchestrate(script);
    }

    /**
     * 更新编排脚本
     *
     * @param treeResponse {@link TreeResponse} 编排hashTree
     * @return {@link CommonResult}
     */
    @PutMapping("/orchestrate")
    public CommonResult updateOrchestrate(@RequestBody TreeResponse treeResponse) {
        return service.updateOrchestrate(treeResponse);
    }

    /**
     * 查询编排脚本的hashTree
     *
     * @param scriptId 脚本ID
     * @return {@link CommonResult}
     */
    @GetMapping("/orchestrate/get")
    public CommonResult orchestrate(@RequestParam("script_id") int scriptId) {
        return service.queryOrchestrate(scriptId);
    }


    @GetMapping("/script/exec")
    public void exec(HttpServletRequest request) {
        service.exec(request);
    }

    @PostMapping("/script/execComplete")
    public CommonResult execComplete(@RequestBody Map<String, String> map) {
        if (map.get("recordId").equals("0")) {
            return CommonResult.failed("recordId is valid. ");
        }
        return CommonResult.success();
    }
}
