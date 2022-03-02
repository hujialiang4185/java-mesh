/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
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

import io.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * 预案管理controller
 *
 * @author y30010171
 * @since 2021-11-02
 **/
@Api(tags = "预案管理")
@RestController
@RequestMapping("/api")
public class EmergencyPlanController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmergencyPlanController.class);

    @Autowired
    private EmergencyPlanService planService;


    /**
     * 修改预案下的拓扑任务信息
     *
     * @param params {@link PlanSaveParams}
     * @return {@link CommonResult}
     */
    @PutMapping("/plan")
    public CommonResult save(UsernamePasswordAuthenticationToken authentication, @RequestBody PlanSaveParams params) {
        return planService.save(params.getPlanId(), params.getExpand(), ((JwtUser) authentication.getPrincipal()).getUsername());
    }

    /**
     * 预案执行
     *
     * @param plan {@link EmergencyPlan#getPlanId()} 预案ID
     * @return {@link CommonResult}
     */
    @PostMapping("/plan/run")
    public CommonResult run(UsernamePasswordAuthenticationToken authentication, @RequestBody EmergencyPlan plan) {
        if (plan.getPlanId() == null) {
            return CommonResult.failed("请选择需要运行的预案");
        }
        return planService.exec(plan.getPlanId(), ((JwtUser) authentication.getPrincipal()).getUsername());
    }

    /**
     * 预案启动,开始调度
     *
     * @param param {@link PlanQueryDto#getPlanId()} 预案ID {@link PlanQueryDto#getStartTime()} ()} 执行时间
     * @return {@link CommonResult}
     */
    @PostMapping("/plan/schedule")
    public CommonResult start(UsernamePasswordAuthenticationToken authentication, @RequestBody PlanQueryDto param) {
        if (param.getPlanId() == null) {
            return CommonResult.failed("请选择需要启动的预案");
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
     * 预案停止,停止调度
     *
     * @param plan {@link EmergencyPlan#getPlanId()} 预案ID
     * @return {@link CommonResult}
     */
    @PostMapping("/plan/cancel")
    public CommonResult stop(UsernamePasswordAuthenticationToken authentication, @RequestBody EmergencyPlan plan) {
        if (plan.getPlanId() == null) {
            return CommonResult.failed("请选择需要停止的预案");
        }
        return planService.stop(plan.getPlanId(), ((JwtUser) authentication.getPrincipal()).getUsername());
    }

    /**
     * 查询预案的编号与名称
     *
     * @param planId 预案ID
     * @return {@link CommonResult}
     */
    @GetMapping("/plan/get")
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
     * 查询预案以及预案下的任务信息
     *
     * @param planName    预案名称或编号
     * @param sceneName   场景名称或编号
     * @param taskName    任务名称或编号
     * @param scriptName  脚本名称
     * @param statusLabel 预案状态
     * @param pageSize    分页大小
     * @param current     当前页码
     * @param sorter      排序字段
     * @param order       排序方式
     * @return {@link CommonResult}
     */
    @GetMapping("/plan")
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
        return planService.plan(params,jwtUser);
    }

    /**
     * 获取预案下的拓扑任务信息
     *
     * @param planId 预案ID
     * @return {@link CommonResult}
     */
    @GetMapping("/plan/task")
    public CommonResult query(@RequestParam("plan_id") int planId) {
        return planService.query(planId);
    }

    /**
     * 新增一个任务
     *
     * @param taskNode 任务信息
     * @return {@link CommonResult}
     */
    @PostMapping("/plan/task")
    public CommonResult addTask(@RequestBody TaskNode taskNode) {
        return planService.addTask(taskNode);
    }

    /**
     * 更新任务
     *
     * @param taskNode 任务信息
     * @return {@link CommonResult}
     */
    @PutMapping("/plan/task")
    public CommonResult updateTask(UsernamePasswordAuthenticationToken authentication,@RequestBody TaskNode taskNode) {
        taskNode.setCreateUser(((JwtUser) authentication.getPrincipal()).getUsername());
        return planService.updateTask(taskNode);
    }

    /**
     * 新增一个预案
     *
     * @param emergencyPlan {@link EmergencyPlan#getPlanName()} 预案名称
     * @return {@link CommonResult}
     */
    @PostMapping("/plan")
    public CommonResult addPlan(UsernamePasswordAuthenticationToken authentication, @RequestBody EmergencyPlan emergencyPlan) {
        UserEntity userEntity = ((JwtUser) authentication.getPrincipal()).getUserEntity();
        emergencyPlan.setPlanGroup(userEntity.getGroup());
        emergencyPlan.setCreateUser(userEntity.getUserName());
        return planService.add(emergencyPlan);
    }

    /**
     * 删除一个预案
     *
     * @param planId 预案ID
     * @return {@link CommonResult}
     */
    @DeleteMapping("/plan")
    public CommonResult deletePlan(@RequestParam("plan_id") int planId) {
        EmergencyPlan plan = new EmergencyPlan();
        plan.setPlanId(planId);
        return planService.delete(plan);
    }

    /**
     * 预案提交审核
     *
     * @param emergencyPlan {@link EmergencyPlan#getPlanId()} 预案ID
     * @return {@link CommonResult}
     */
    @PostMapping("plan/submitReview")
    public CommonResult submitReview(@RequestBody EmergencyPlan emergencyPlan) {
        return planService.submit(emergencyPlan.getPlanId(), emergencyPlan.getApprover());
    }

    /**
     * 预案审核
     *
     * @param planQueryDto {@link PlanQueryDto#getPlanId()} 预案ID，
     *                     {@link PlanQueryDto#getCheckResult()} 审核结果，
     *                     {@link PlanQueryDto#getCheckResult()} 审核意见
     * @return {@link CommonResult}
     */
    @PostMapping("/plan/approve")
    public CommonResult approve(UsernamePasswordAuthenticationToken authentication,@RequestBody PlanQueryDto planQueryDto) {
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
    public CommonResult copyPlan(UsernamePasswordAuthenticationToken authentication, @RequestBody EmergencyPlan emergencyPlan) {
        emergencyPlan.setCreateUser(((JwtUser) authentication.getPrincipal()).getUsername());
        return planService.copy(emergencyPlan);
    }
}
