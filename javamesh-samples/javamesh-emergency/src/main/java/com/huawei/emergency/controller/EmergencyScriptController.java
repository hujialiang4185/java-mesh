/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.emergency.controller;

import com.huawei.common.api.CommonResult;
import com.huawei.common.constant.FailedInfo;
import com.huawei.common.constant.ResultCode;
import com.huawei.emergency.dto.ArgusScript;
import com.huawei.emergency.dto.ScriptManageDto;
import com.huawei.emergency.entity.EmergencyExecRecord;
import com.huawei.emergency.entity.EmergencyScript;
import com.huawei.emergency.entity.JwtUser;
import com.huawei.emergency.entity.UserEntity;
import com.huawei.emergency.layout.TreeResponse;
import com.huawei.emergency.service.EmergencyExecService;
import com.huawei.emergency.service.EmergencyScriptService;
import com.huawei.script.exec.ExecResult;
import com.huawei.script.exec.log.LogResponse;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    public CommonResult<EmergencyScript> selectScript(@RequestParam(value = "script_id") int scriptId) {
        EmergencyScript script = service.selectScript(scriptId);
        if (script == null) {
            return CommonResult.failed(FailedInfo.SCRIPT_NOT_EXISTS);
        }
        return CommonResult.success(script);
    }

    @PostMapping
    public CommonResult insertScript(UsernamePasswordAuthenticationToken authentication, @RequestBody EmergencyScript script) {
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
    public CommonResult updateScript(@RequestBody EmergencyScript script) {
        int count = service.updateScript(script);
        if (count == 1) {
            return CommonResult.success(count);
        } else if (count == ResultCode.SCRIPT_NAME_EXISTS) {
            return CommonResult.failed(FailedInfo.SCRIPT_NAME_EXISTS);
        } else {
            return CommonResult.failed("文件修改失败");
        }
    }

    @GetMapping("/search")
    public CommonResult searchScript(UsernamePasswordAuthenticationToken authentication,
                                     @RequestParam(value = "value", required = false) String scriptName,
                                     @RequestParam(value = "status", required = false) String status,
                                     @RequestParam(value = "type", required = false) String scriptType) {
        List<String> scriptNames = service.searchScript((JwtUser) authentication.getPrincipal(), scriptName, status, scriptType);
        return CommonResult.success(scriptNames);
    }

    @GetMapping("/getByName")
    public CommonResult getScriptEntityByName(@RequestParam(value = "name") String scriptName) {
        EmergencyScript script = service.getScriptByName(scriptName);
        return CommonResult.success(script);
    }

    @PostMapping("/submitReview")
    public CommonResult submitReview(UsernamePasswordAuthenticationToken authentication, @RequestBody EmergencyScript script) {
        String result = service.submitReview((JwtUser) authentication.getPrincipal(), script);
        if (result.equals(SUCCESS)) {
            return CommonResult.success(result);
        } else {
            return CommonResult.failed(result);
        }
    }

    @PostMapping("/approve")
    public CommonResult approve(UsernamePasswordAuthenticationToken authentication, @RequestBody Map<String, Object> map) {
        int count = service.approve(((JwtUser) authentication.getPrincipal()).getUsername(),map);
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
    public CommonResult debugScriptBeforeSave(@RequestBody Map<String, String> param) {
        return service.debugScriptBeforeSave(param.get("content"), param.get("server_name"));
    }

    @PostMapping("/debugStop")
    public CommonResult debugStop(@RequestBody EmergencyExecRecord param) {
        return service.debugScriptStop(param.getDebugId());
    }

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
     * 创建GUI脚本
     *
     * @param script {@link EmergencyScript script}
     * @return
     */
    @PostMapping("/orchestrate")
    public CommonResult createGuiScript(UsernamePasswordAuthenticationToken authentication, @RequestBody EmergencyScript script) {
        return service.createGuiScript(((JwtUser) authentication.getPrincipal()).getUserEntity(), script);
    }

    /**
     * 修改GUI脚本
     *
     * @param treeResponse {@link TreeResponse} 编排树
     * @return {@link CommonResult}
     */
    @PutMapping("/orchestrate")
    public CommonResult updateGuiScript(UsernamePasswordAuthenticationToken authentication,@RequestBody TreeResponse treeResponse) {
        return service.updateGuiScript(((JwtUser) authentication.getPrincipal()).getUsername(),treeResponse);
    }

    /**
     * 查询GUI脚本的编排树
     *
     * @param scriptId 脚本ID
     * @return {@link CommonResult}
     */
    @GetMapping("/orchestrate/get")
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
    public CommonResult createIdeScript(UsernamePasswordAuthenticationToken authentication,@RequestBody ScriptManageDto scriptManageDto) {
        return service.createIdeScript(((JwtUser) authentication.getPrincipal()).getUserEntity(),scriptManageDto);
    }

    /**
     * 修改IDE脚本
     *
     * @param scriptManageDto {@link ScriptManageDto} 脚本信息
     * @return
     */
    @PutMapping("/ide")
    public CommonResult updateIdeScript(@RequestBody ScriptManageDto scriptManageDto) {
        EmergencyScript script = new EmergencyScript();
        script.setScriptId(scriptManageDto.getScriptId());
        script.setScriptName(scriptManageDto.getScriptName());
        script.setContent(scriptManageDto.getContent());
        return updateScript(script);
    }

    /**
     * 获取IDE脚本
     *
     * @param scriptId 脚本ID
     * @return
     */
    @GetMapping("/ide/get")
    public CommonResult createIdeScript(@RequestParam("script_id") Integer scriptId) {
        return selectScript(scriptId);
    }

    @GetMapping("/exec")
    public void exec(HttpServletRequest request) {
        service.exec(request);
    }

    @PostMapping("/execComplete")
    public CommonResult execComplete(@RequestBody ExecResult execResult) {
        if (execResult.getRecordId() == 0) {
            return CommonResult.failed("recordId is valid. ");
        }
        return execService.execComplete(execResult);
    }
}
