/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

import com.huawei.common.api.CommonPage;
import com.huawei.common.api.CommonResult;
import com.huawei.emergency.dto.LogResponse;
import com.huawei.emergency.dto.PlanQueryDto;
import com.huawei.emergency.entity.EmergencyExecRecord;
import com.huawei.emergency.entity.EmergencyExecRecordDetail;
import com.huawei.emergency.entity.EmergencyPlan;
import com.huawei.emergency.entity.JwtUser;
import com.huawei.emergency.service.EmergencyExecService;
import com.huawei.logaudit.aop.WebOperationLog;
import com.huawei.logaudit.constant.OperationDetails;
import com.huawei.logaudit.constant.OperationTypeEnum;
import com.huawei.logaudit.constant.ResourceType;

import io.swagger.annotations.Api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 执行记录管理，包括脚本调试记录，脚本调试日志，预案执行记录。
 *
 * @author y30010171
 * @since 2021-11-09
 **/
@Api(tags = "执行记录管理")
@RestController
@RequestMapping("/api/history")
public class EmergencyExecController {
    @Autowired
    private EmergencyExecService execService;

    /**
     * 重新执行某条失败的执行记录
     *
     * @param authentication 登录信息
     * @param params {@link PlanQueryDto#getKey()} 执行记录ID
     * @return {@link CommonResult}
     */
    @PostMapping("/scenario/task/runAgain")
    @WebOperationLog(resourceType = ResourceType.EXECUTION_RECORD,
        operationType = OperationTypeEnum.EXECUTE,
        operationDetails = OperationDetails.RE_EXECUTE)
    public CommonResult reExec(UsernamePasswordAuthenticationToken authentication, @RequestBody PlanQueryDto params) {
        if (params.getKey() == null) {
            return CommonResult.failed("请选择执行记录");
        }
        return execService.reExec(params.getKey(), ((JwtUser) authentication.getPrincipal()).getUsername());
    }

    /**
     * 人工确认某条执行记录是否成功
     *
     * @param authentication 登录信息
     * @param params {@link PlanQueryDto#getKey()} 执行记录ID {@link PlanQueryDto#getConfirm()}} 确认结果
     * @return {@link CommonResult}
     */
    @PostMapping("/scenario/task/ensure")
    @WebOperationLog(resourceType = ResourceType.EXECUTION_RECORD,
        operationType = OperationTypeEnum.MANUAL_CONFIRMATION,
        operationDetails = OperationDetails.MANUAL_CONFIRMATION)
    public CommonResult success(UsernamePasswordAuthenticationToken authentication, @RequestBody PlanQueryDto params) {
        if (params.getKey() == null) {
            return CommonResult.failed("请选择正确的执行记录");
        }
        if ("成功".equals(params.getConfirm())) {
            return execService.ensure(params.getKey(), "5", ((JwtUser) authentication.getPrincipal()).getUsername());
        }
        if ("失败".equals(params.getConfirm())) {
            return execService.ensure(params.getKey(), "6", ((JwtUser) authentication.getPrincipal()).getUsername());
        }
        return CommonResult.failed("请选择确认成功或者失败");
    }

    /**
     * 查询预案的执行记录
     *
     * @param authentication 登录信息
     * @param planName 预案名称
     * @param pageSize 分页大小
     * @param current 页码
     * @param sorter 排序字段
     * @param order 排序方式
     * @param creators 用于过滤的创建人
     * @param planNames 用于过滤的预案编号
     * @return {@link CommonResult}
     */
    @GetMapping()
    @WebOperationLog(resourceType = ResourceType.EXECUTION_RECORD,
        operationType = OperationTypeEnum.SELECT,
        operationDetails = OperationDetails.QUERY_EXEC_LIST)
    public CommonResult allPlanExecRecords(UsernamePasswordAuthenticationToken authentication,
        @RequestParam(value = "keywords", required = false) String planName,
        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
        @RequestParam(value = "current", defaultValue = "1") int current,
        @RequestParam(value = "sorter", defaultValue = "execute_time") String sorter,
        @RequestParam(value = "order", defaultValue = "") String order,
        @RequestParam(value = "creator[]", required = false) String[] creators,
        @RequestParam(value = "plan_name[]", required = false) String[] planNames) {
        CommonPage<EmergencyPlan> params = new CommonPage<>();
        params.setPageSize(pageSize);
        params.setPageIndex(current);
        params.setSortField(sorter);
        if ("ascend".equals(order)) {
            params.setSortType("ASC");
        } else if ("descend".equals(order)) {
            params.setSortType("DESC");
        }
        EmergencyPlan plan = new EmergencyPlan();
        plan.setPlanName(planName);
        params.setObject(plan);
        return execService.allPlanExecRecords(((JwtUser) authentication.getPrincipal()).getGroupName(), params,
            planNames, creators);
    }

    /**
     * 查询某条预案执行记录下的场景执行记录
     *
     * @param execId 执行ID
     * @return {@link CommonResult}
     */
    @GetMapping("/scenario")
    @WebOperationLog(resourceType = ResourceType.EXECUTION_RECORD,
        operationType = OperationTypeEnum.SELECT,
        operationDetails = OperationDetails.QUERY_SCENARIO_EXEC_LIST)
    public CommonResult allSceneExecRecords(@RequestParam("history_id") int execId) {
        CommonPage<EmergencyExecRecord> params = new CommonPage<>();
        EmergencyExecRecord record = new EmergencyExecRecord();
        record.setExecId(execId);
        params.setObject(record);
        return execService.allSceneExecRecords(params);
    }

    /**
     * 查询某条场景执行明细下的任务执行明细
     *
     * @param execId 执行ID
     * @param sceneId 场景ID
     * @return {@link CommonResult}
     */
    @GetMapping("/scenario/task")
    @WebOperationLog(resourceType = ResourceType.EXECUTION_RECORD,
        operationType = OperationTypeEnum.SELECT,
        operationDetails = OperationDetails.QUERY_TASK_EXEC_LIST)
    public CommonResult allTaskExecRecords(@RequestParam("history_id") int execId,
        @RequestParam("scena_id") int sceneId) {
        CommonPage<EmergencyExecRecord> params = new CommonPage<>();
        EmergencyExecRecord record = new EmergencyExecRecord();
        record.setExecId(execId);
        record.setSceneId(sceneId);
        params.setObject(record);
        return execService.allTaskExecRecords(params);
    }

    /**
     * 查询某条执行记录的日志
     *
     * @param recordId 记录ID
     * @param lineNum 日志行号
     * @return {@link LogResponse}
     */
    @GetMapping("/scenario/task/log")
    @WebOperationLog(resourceType = ResourceType.EXECUTION_RECORD,
        operationType = OperationTypeEnum.SELECT,
        operationDetails = OperationDetails.EXECUTE_LOG)
    public LogResponse getLog(@RequestParam("key") int recordId,
        @RequestParam(value = "line", defaultValue = "1") int lineNum) {
        int lineIndex = lineNum;
        if (lineIndex <= 0) {
            lineIndex = 1;
        }
        return execService.getRecordLog(recordId, lineIndex);
    }

    @PostMapping("/detail/stop")
    public CommonResult stopOneServer(UsernamePasswordAuthenticationToken authentication,
        @RequestBody EmergencyExecRecordDetail detail) {
        return execService.stopOneServer(detail.getDetailId(), ((JwtUser) authentication.getPrincipal()).getUsername());
    }

    @PostMapping("/start")
    public CommonResult startOneServer(UsernamePasswordAuthenticationToken authentication,
        @RequestBody EmergencyExecRecordDetail detail) {
        return execService.startOneServer(detail.getDetailId(),
            ((JwtUser) authentication.getPrincipal()).getUsername());
    }

    @PostMapping("/ensure")
    public CommonResult ensureOneServer(UsernamePasswordAuthenticationToken authentication,
        @RequestBody EmergencyExecRecordDetail detail) {
        if (detail.getDetailId() == null) {
            return CommonResult.failed("请选择正确的执行记录");
        }
        if ("成功".equals(detail.getStatus())) {
            return execService.ensure(detail.getDetailId(), "5",
                ((JwtUser) authentication.getPrincipal()).getUsername());
        }
        if ("失败".equals(detail.getStatus())) {
            return execService.ensure(detail.getDetailId(), "6",
                ((JwtUser) authentication.getPrincipal()).getUsername());
        }
        return CommonResult.failed("请选择确认成功或者失败");
    }

    /**
     * 查询执行日志
     *
     * @param detailId 脚本调试产生的debugId或者任务分发后的执行明细ID
     * @param lineNum 日志开始行数，从第几行开始读写
     * @return 日志信息
     */
    @GetMapping("/log")
    @WebOperationLog(resourceType = ResourceType.EXECUTION_RECORD,
        operationType = OperationTypeEnum.SELECT,
        operationDetails = OperationDetails.EXECUTE_LOG)
    public LogResponse logOneServer(@RequestParam("key") int detailId,
        @RequestParam(value = "line", defaultValue = "1") int lineNum) {
        int lineIndex = lineNum;
        if (lineIndex <= 0) {
            lineIndex = 1;
        }
        return execService.logOneServer(detailId, lineIndex);
    }

    @GetMapping("/get")
    public CommonResult getPlanInfo(@RequestParam(value = "history_id") int execId) {
        return execService.getPlanInfo(execId);
    }

    @DeleteMapping
    @WebOperationLog(resourceType = ResourceType.EXECUTION_RECORD,
        operationType = OperationTypeEnum.DELETE,
        operationDetails = OperationDetails.DELETE_EXECUTION)
    public CommonResult deleteExecRecord(@RequestParam("history_id[]") Integer[] historyIds) {
        return execService.deleteExecRecord(historyIds);
    }

    /**
     * 停止正在运行的执行记录
     *
     * @param record {@link EmergencyExecRecord} 记录信息
     * @return {@link CommonResult}
     */
    @PostMapping("/stop")
    public CommonResult stopExecRecord(@RequestBody EmergencyExecRecord record) {
        return execService.stopRecord(record);
    }
}
