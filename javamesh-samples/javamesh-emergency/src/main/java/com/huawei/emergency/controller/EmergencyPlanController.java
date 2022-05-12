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
import com.huawei.common.constant.PlanStatus;
import com.huawei.common.constant.ScheduleType;
import com.huawei.emergency.dto.PlanQueryDto;
import com.huawei.emergency.dto.PlanQueryParams;
import com.huawei.emergency.dto.PlanSaveParams;
import com.huawei.emergency.dto.TaskNode;
import com.huawei.emergency.entity.EmergencyPlan;
import com.huawei.emergency.entity.JwtUser;
import com.huawei.emergency.entity.UserEntity;
import com.huawei.emergency.service.EmergencyPlanService;
import com.huawei.logaudit.aop.WebOperationLog;
import com.huawei.logaudit.constant.OperationDetails;
import com.huawei.logaudit.constant.OperationTypeEnum;
import com.huawei.logaudit.constant.ResourceType;

import io.swagger.annotations.Api;

import org.apache.commons.lang.StringUtils;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 项目管理controller
 *
 * @author y30010171
 * @since 2021-11-02
 **/
@Api(tags = "项目管理")
@RestController
@RequestMapping("/api")
public class EmergencyPlanController {
    @Autowired
    private EmergencyPlanService planService;

    /**
     * 修改项目下的拓扑任务信息
     *
     * @param authentication 登录信息
     * @param params {@link PlanSaveParams}
     * @return {@link CommonResult}
     */
    @PutMapping("/plan")
    @WebOperationLog(resourceType = ResourceType.PLAN_MANAGEMENT,
        operationType = OperationTypeEnum.UPDATE,
        operationDetails = OperationDetails.PLAN_SAVE)
    public CommonResult save(UsernamePasswordAuthenticationToken authentication, @RequestBody PlanSaveParams params) {
        return planService.save(params.getPlanId(), params.getExpand(),
            ((JwtUser) authentication.getPrincipal()).getUsername());
    }

    /**
     * 项目执行
     *
     * @param authentication 登录信息
     * @param plan {@link EmergencyPlan#getPlanId()} 项目ID
     * @return {@link CommonResult}
     */
    @PostMapping("/plan/run")
    @WebOperationLog(resourceType = ResourceType.PLAN_MANAGEMENT,
        operationType = OperationTypeEnum.EXECUTE,
        operationDetails = OperationDetails.PLAN_EXECUTE)
    public CommonResult run(UsernamePasswordAuthenticationToken authentication, @RequestBody EmergencyPlan plan) {
        if (plan.getPlanId() == null) {
            return CommonResult.failed("请选择需要运行的项目");
        }
        return planService.exec(plan.getPlanId(), ((JwtUser) authentication.getPrincipal()).getUsername());
    }

    /**
     * 项目启动,开始调度
     *
     * @param authentication 登录信息
     * @param param {@link PlanQueryDto#getPlanId()} 项目ID {@link PlanQueryDto#getStartTime()} ()} 执行时间
     * @return {@link CommonResult}
     */
    @PostMapping("/plan/schedule")
    @WebOperationLog(resourceType = ResourceType.PLAN_MANAGEMENT,
        operationType = OperationTypeEnum.EXECUTE,
        operationDetails = OperationDetails.PLAN_START)
    public CommonResult start(UsernamePasswordAuthenticationToken authentication, @RequestBody PlanQueryDto param) {
        if (param.getPlanId() == null) {
            return CommonResult.failed("请选择需要启动的项目");
        }
        EmergencyPlan plan = new EmergencyPlan();
        plan.setPlanId(param.getPlanId());
        if (StringUtils.isNotEmpty(param.getStartTime())) {
            plan.setScheduleType(ScheduleType.ONCE.getValue());
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                plan.setScheduleConf(String.valueOf(format.parse(param.getStartTime()).getTime()));
            } catch (ParseException e) {
                return CommonResult.failed("启动时间设置错误");
            }
        } else {
            plan.setScheduleType(ScheduleType.NONE.getValue());
        }
        return planService.start(plan, ((JwtUser) authentication.getPrincipal()).getUsername());
    }

    /**
     * 项目停止,停止调度
     *
     * @param authentication 登录信息
     * @param plan {@link EmergencyPlan#getPlanId()} 项目ID
     * @return {@link CommonResult}
     */
    @PostMapping("/plan/cancel")
    @WebOperationLog(resourceType = ResourceType.PLAN_MANAGEMENT,
        operationType = OperationTypeEnum.DISCONTINUE,
        operationDetails = OperationDetails.PLAN_STOP)
    public CommonResult stop(UsernamePasswordAuthenticationToken authentication, @RequestBody EmergencyPlan plan) {
        if (plan.getPlanId() == null) {
            return CommonResult.failed("请选择需要停止的项目");
        }
        return planService.stop(plan.getPlanId(), ((JwtUser) authentication.getPrincipal()).getUsername());
    }

    /**
     * 查询项目的编号与名称
     *
     * @param planId 项目ID
     * @return {@link CommonResult}
     */
    @GetMapping("/plan/get")
    @WebOperationLog(resourceType = ResourceType.PLAN_MANAGEMENT,
        operationType = OperationTypeEnum.SELECT,
        operationDetails = OperationDetails.GET_PLAN_INFO)
    public CommonResult get(@RequestParam("plan_id") int planId) {
        CommonResult<EmergencyPlan> queryData = planService.get(planId);
        EmergencyPlan plan = queryData.getData();
        EmergencyPlan returnPlan = new EmergencyPlan();
        if (plan != null) {
            returnPlan.setPlanId(plan.getPlanId());
            returnPlan.setPlanNo(plan.getPlanNo());
            returnPlan.setPlanName(plan.getPlanName());
        }
        return CommonResult.success(returnPlan);
    }

    /**
     * 查询项目以及项目下的任务信息
     *
     * @param authentication 登录信息
     * @param planName 项目名称或编号
     * @param sceneName 场景名称或编号
     * @param taskName 任务名称或编号
     * @param scriptName 脚本名称
     * @param statusLabel 项目状态
     * @param pageSize 分页大小
     * @param current 当前页码
     * @param sorter 排序字段
     * @param order 排序方式
     * @return {@link CommonResult}
     */
    @GetMapping("/plan")
    @WebOperationLog(resourceType = ResourceType.PLAN_MANAGEMENT,
        operationType = OperationTypeEnum.SELECT,
        operationDetails = OperationDetails.QUERY_PLAN_LIST)
    public CommonResult queryPlan(UsernamePasswordAuthenticationToken authentication,
        @RequestParam(value = "plan_name_no", required = false) String planName,
        @RequestParam(value = "scena_name_no", required = false) String sceneName,
        @RequestParam(value = "task_name_no", required = false) String taskName,
        @RequestParam(value = "script_name", required = false) String scriptName,
        @RequestParam(value = "status_label", required = false) String statusLabel,
        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
        @RequestParam(value = "current", defaultValue = "1") int current,
        @RequestParam(value = "sorter", defaultValue = "create_time") String sorter,
        @RequestParam(value = "order", defaultValue = "DESC") String order) {
        CommonPage<PlanQueryParams> params = new CommonPage<>();
        params.setPageSize(pageSize);
        params.setPageIndex(current);
        params.setSortField(sorter);
        if ("ascend".equals(order)) {
            params.setSortType("ASC");
        } else if ("descend".equals(order)) {
            params.setSortType("DESC");
        }
        PlanQueryParams planParams = new PlanQueryParams();
        planParams.setPlanName(planName);
        planParams.setSceneName(sceneName);
        planParams.setTaskName(taskName);
        planParams.setScriptName(scriptName);
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        planParams.setPlanGroup(jwtUser.getGroupName());
        if (StringUtils.isNotEmpty(statusLabel)) {
            planParams.setStatus(PlanStatus.matchByLabel(statusLabel, PlanStatus.NEW).getValue());
        }
        params.setObject(planParams);
        return planService.plan(params, jwtUser);
    }

    /**
     * 获取项目下的拓扑任务信息
     *
     * @param planId 项目ID
     * @return {@link CommonResult}
     */
    @GetMapping("/plan/task")
    @WebOperationLog(resourceType = ResourceType.PLAN_MANAGEMENT,
        operationType = OperationTypeEnum.SELECT,
        operationDetails = OperationDetails.QUERY_PLAN_TASK_INFO)
    public CommonResult query(@RequestParam("plan_id") int planId) {
        return planService.query(planId);
    }

    /**
     * 新增一个任务
     *
     * @param authentication 登录信息
     * @param taskNode 任务信息
     * @return {@link CommonResult}
     */
    @PostMapping("/plan/task")
    @WebOperationLog(resourceType = ResourceType.PLAN_MANAGEMENT,
        operationType = OperationTypeEnum.CREATE,
        operationDetails = OperationDetails.ADD_TASK)
    public CommonResult addTask(UsernamePasswordAuthenticationToken authentication, @RequestBody TaskNode taskNode) {
        taskNode.setCreateUser(((JwtUser) authentication.getPrincipal()).getUsername());
        return planService.addTask(taskNode);
    }

    /**
     * 更新任务
     *
     * @param authentication 登录信息
     * @param taskNode 任务信息
     * @return {@link CommonResult}
     */
    @PutMapping("/plan/task")
    @WebOperationLog(resourceType = ResourceType.PLAN_MANAGEMENT,
        operationType = OperationTypeEnum.UPDATE,
        operationDetails = OperationDetails.UPDATE_TASK)
    public CommonResult updateTask(UsernamePasswordAuthenticationToken authentication, @RequestBody TaskNode taskNode) {
        taskNode.setCreateUser(((JwtUser) authentication.getPrincipal()).getUsername());
        return planService.updateTask(taskNode);
    }

    /**
     * 新增一个项目
     *
     * @param authentication 登录信息
     * @param emergencyPlan {@link EmergencyPlan#getPlanName()} 项目名称
     * @return {@link CommonResult}
     */
    @PostMapping("/plan")
    @WebOperationLog(resourceType = ResourceType.PLAN_MANAGEMENT,
        operationType = OperationTypeEnum.CREATE,
        operationDetails = OperationDetails.ADD_PLAN)
    public CommonResult addPlan(UsernamePasswordAuthenticationToken authentication,
        @RequestBody EmergencyPlan emergencyPlan) {
        UserEntity userEntity = ((JwtUser) authentication.getPrincipal()).getUserEntity();
        emergencyPlan.setPlanGroup(userEntity.getGroup());
        emergencyPlan.setCreateUser(userEntity.getUserName());
        return planService.add(emergencyPlan);
    }

    /**
     * 删除一个项目
     *
     * @param authentication 登录信息
     * @param planId 项目ID
     * @return {@link CommonResult}
     */
    @DeleteMapping("/plan")
    @WebOperationLog(resourceType = ResourceType.PLAN_MANAGEMENT,
        operationType = OperationTypeEnum.DELETE,
        operationDetails = OperationDetails.DELETE_PLAN)
    public CommonResult deletePlan(UsernamePasswordAuthenticationToken authentication,
        @RequestParam("plan_id") int planId) {
        UserEntity userEntity = ((JwtUser) authentication.getPrincipal()).getUserEntity();
        EmergencyPlan plan = new EmergencyPlan();
        plan.setPlanId(planId);
        plan.setUpdateUser(userEntity.getUserName());
        return planService.delete(plan);
    }

    /**
     * 项目提交审核
     *
     * @param emergencyPlan {@link EmergencyPlan#getPlanId()} 项目ID
     * @return {@link CommonResult}
     */
    @PostMapping("plan/submitReview")
    @WebOperationLog(resourceType = ResourceType.PLAN_MANAGEMENT,
        operationType = OperationTypeEnum.SUBMIT_REVIEW,
        operationDetails = OperationDetails.SUBMIT_PLAN_REVIEW)
    public CommonResult submitReview(@RequestBody EmergencyPlan emergencyPlan) {
        return planService.submit(emergencyPlan.getPlanId(), emergencyPlan.getApprover());
    }

    /**
     * 项目审核
     *
     * @param authentication 登录信息
     * @param planQueryDto {@link PlanQueryDto#getPlanId()} 项目ID, {@link PlanQueryDto#getCheckResult()} 审核结果 {@link
     * PlanQueryDto#getCheckResult()} 审核意见
     * @return {@link CommonResult}
     */
    @PostMapping("/plan/approve")
    @WebOperationLog(resourceType = ResourceType.PLAN_MANAGEMENT,
        operationType = OperationTypeEnum.AUDIT,
        operationDetails = OperationDetails.AUDIT_PLAN)
    public CommonResult approve(UsernamePasswordAuthenticationToken authentication,
        @RequestBody PlanQueryDto planQueryDto) {
        EmergencyPlan plan = new EmergencyPlan();
        plan.setPlanId(planQueryDto.getPlanId());
        plan.setStatus(parseCheckResult(planQueryDto.getApprove()));
        plan.setCheckRemark(planQueryDto.getComment());
        return planService.approve(plan, ((JwtUser) authentication.getPrincipal()).getUsername());
    }

    private String parseCheckResult(String checkResult) {
        if ("通过".equals(checkResult)) {
            return PlanStatus.APPROVED.getValue();
        }
        if ("驳回".equals(checkResult)) {
            return PlanStatus.REJECT.getValue();
        }
        return checkResult;
    }

    @GetMapping("/plan/search/status_label")
    public CommonResult planStatus() {
        return CommonResult.success(
            Arrays.stream(PlanStatus.values())
                .map(PlanStatus::getStatusLabel)
                .collect(Collectors.toList()).toArray()
        );
    }

    @PostMapping("/plan/copy")
    @WebOperationLog(resourceType = ResourceType.PLAN_MANAGEMENT,
        operationType = OperationTypeEnum.CREATE,
        operationDetails = OperationDetails.COPY_PLAN)
    public CommonResult copyPlan(UsernamePasswordAuthenticationToken authentication,
        @RequestBody EmergencyPlan emergencyPlan) {
        emergencyPlan.setCreateUser(((JwtUser) authentication.getPrincipal()).getUsername());
        emergencyPlan.setPlanGroup(((JwtUser) authentication.getPrincipal()).getGroupName());
        return planService.copy(emergencyPlan);
    }

    @PostMapping("/plan/stop")
    @WebOperationLog(resourceType = ResourceType.PLAN_MANAGEMENT,
        operationType = OperationTypeEnum.DISCONTINUE,
        operationDetails = OperationDetails.PLAN_CANCEL)
    public CommonResult stopPlan(@RequestBody EmergencyPlan emergencyPlan) {
        return planService.stopPlan(emergencyPlan);
    }
}
