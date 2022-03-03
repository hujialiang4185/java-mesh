/*
 * Copyright (C) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved
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

import static com.huawei.common.constant.PlanStatus.UN_PASSED_STATUS;

import com.huawei.argus.restcontroller.RestPerfTestController;
import com.huawei.common.api.CommonPage;
import com.huawei.common.api.CommonResult;
import com.huawei.common.constant.PlanStatus;
import com.huawei.common.constant.RecordStatus;
import com.huawei.common.constant.ScheduleType;
import com.huawei.common.constant.TaskTypeEnum;
import com.huawei.common.constant.ValidEnum;
import com.huawei.common.filter.JwtAuthenticationTokenFilter;
import com.huawei.common.ws.WebSocketServer;
import com.huawei.emergency.dto.PlanDetailQueryDto;
import com.huawei.emergency.dto.PlanQueryDto;
import com.huawei.emergency.dto.PlanQueryParams;
import com.huawei.emergency.dto.TaskNode;
import com.huawei.emergency.entity.EmergencyExec;
import com.huawei.emergency.entity.EmergencyExecRecord;
import com.huawei.emergency.entity.EmergencyExecRecordExample;
import com.huawei.emergency.entity.EmergencyPlan;
import com.huawei.emergency.entity.EmergencyPlanDetail;
import com.huawei.emergency.entity.EmergencyPlanDetailExample;
import com.huawei.emergency.entity.EmergencyPlanExample;
import com.huawei.emergency.entity.EmergencyScript;
import com.huawei.emergency.entity.EmergencyScriptExample;
import com.huawei.emergency.entity.EmergencyTask;
import com.huawei.emergency.entity.JwtUser;
import com.huawei.emergency.mapper.EmergencyExecMapper;
import com.huawei.emergency.mapper.EmergencyExecRecordDetailMapper;
import com.huawei.emergency.mapper.EmergencyExecRecordMapper;
import com.huawei.emergency.mapper.EmergencyPlanDetailMapper;
import com.huawei.emergency.mapper.EmergencyPlanMapper;
import com.huawei.emergency.mapper.EmergencyScriptMapper;
import com.huawei.emergency.mapper.EmergencyServerMapper;
import com.huawei.emergency.mapper.EmergencyTaskMapper;
import com.huawei.emergency.schedule.thread.TaskScheduleCenter;
import com.huawei.emergency.service.EmergencyPlanService;
import com.huawei.emergency.service.EmergencyScriptService;
import com.huawei.emergency.service.EmergencyTaskService;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import org.apache.commons.lang.StringUtils;
import org.ngrinder.model.MonitoringHost;
import org.ngrinder.model.PerfTest;
import org.ngrinder.model.RampUp;
import org.ngrinder.model.Status;
import org.ngrinder.perftest.repository.PerfTestRepository;
import org.ngrinder.perftest.service.PerfTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import javax.annotation.Resource;

/**
 * 预案管理接口的实现类
 *
 * @author y30010171
 * @since 2021-11-02
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class EmergencyPlanServiceImpl implements EmergencyPlanService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmergencyPlanServiceImpl.class);

    @Resource(name = "scriptExecThreadPool")
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private EmergencyPlanMapper planMapper;

    @Autowired
    private EmergencyPlanDetailMapper detailMapper;

    @Autowired
    private EmergencyExecMapper execMapper;

    @Autowired
    private EmergencyTaskMapper taskMapper;

    @Autowired
    private EmergencyExecRecordMapper recordMapper;

    @Autowired
    private EmergencyExecRecordDetailMapper recordDetailMapper;

    @Autowired
    private ExecRecordHandlerFactory handlerFactory;

    @Autowired
    private EmergencyTaskService taskService;

    @Autowired
    private TaskScheduleCenter scheduleCenter;

    @Autowired
    private RestPerfTestController perfTestController;

    @Autowired
    private PerfTestService perfTestService;

    @Autowired
    private PerfTestRepository perfTestRepository;

    private static final String AUTH_ADMIN = "admin";
    private static final String AUTH_APPROVER = "approver";

    @Autowired
    private EmergencyServerMapper serverMapper;

    @Autowired
    private EmergencyScriptMapper scriptMapper;

    @Autowired
    private EmergencyScriptService scriptService;

    @Override
    public CommonResult add(EmergencyPlan emergencyPlan) {
        if (StringUtils.isEmpty(emergencyPlan.getPlanName())) {
            return CommonResult.failed("请填写预案名称");
        }
        EmergencyPlanExample isPlanNameExist = new EmergencyPlanExample();
        isPlanNameExist.createCriteria()
            .andPlanNameEqualTo(emergencyPlan.getPlanName())
            .andIsValidEqualTo(ValidEnum.VALID.getValue());
        if (planMapper.countByExample(isPlanNameExist) > 0) {
            return CommonResult.failed("已存在预案名称相同的预案");
        }
        EmergencyPlan insertPlan = new EmergencyPlan();
        insertPlan.setPlanName(emergencyPlan.getPlanName());
        insertPlan.setCreateUser(emergencyPlan.getCreateUser());
        insertPlan.setPlanGroup(emergencyPlan.getPlanGroup());
        insertPlan.setUpdateTime(new Date());
        insertPlan.setStatus(PlanStatus.NEW.getValue());
        planMapper.insertSelective(insertPlan);
        EmergencyPlan updatePlanNo = new EmergencyPlan();
        updatePlanNo.setPlanId(insertPlan.getPlanId());
        updatePlanNo.setPlanNo(generatePlanNo(insertPlan.getPlanId()));
        planMapper.updateByPrimaryKeySelective(updatePlanNo);
        insertPlan.setPlanNo(updatePlanNo.getPlanNo());
        return CommonResult.success(insertPlan);
    }

    @Override
    public CommonResult delete(EmergencyPlan emergencyPlan) {
        if (emergencyPlan.getPlanId() == null) {
            return CommonResult.failed("请选择正确的预案");
        }

        if (havePass(emergencyPlan.getPlanId())) {
            return CommonResult.failed("审核通过后不能删除");
        }

        // 是否正在执行
        if (haveRunning(emergencyPlan.getPlanId())) {
            return CommonResult.failed("当前预案正在执行中，无法删除。");
        }

        EmergencyPlan updatePlan = new EmergencyPlan();
        updatePlan.setIsValid(ValidEnum.IN_VALID.getValue());
        updatePlan.setPlanId(emergencyPlan.getPlanId());
        updatePlan.setUpdateTime(new Date());
        if (planMapper.updateByPrimaryKeySelective(updatePlan) == 0) {
            return CommonResult.failed("请选择正确的预案");
        }

        EmergencyPlanDetail updatePlanDetail = new EmergencyPlanDetail();
        updatePlanDetail.setIsValid(ValidEnum.IN_VALID.getValue());
        EmergencyPlanDetailExample updatePlanDetailCondition = new EmergencyPlanDetailExample();
        updatePlanDetailCondition.createCriteria().andPlanIdEqualTo(emergencyPlan.getPlanId());
        detailMapper.updateByExampleSelective(updatePlanDetail, updatePlanDetailCondition);
        return CommonResult.success();
    }

    @Override
    public CommonResult update(EmergencyPlan emergencyPlan) {
        if (emergencyPlan.getPlanId() == 0) {
            return CommonResult.failed("请选择正确的预案");
        }

        // 是否审核通过
        if (havePass(emergencyPlan.getPlanId())) {
            return CommonResult.failed("当前预案已经审核通过，无法修改。");
        }

        EmergencyPlan updatePlan = new EmergencyPlan();
        updatePlan.setPlanId(emergencyPlan.getPlanId());
        updatePlan.setPlanNo(emergencyPlan.getPlanNo());
        updatePlan.setPlanName(emergencyPlan.getPlanName());
        if (planMapper.updateByPrimaryKeySelective(updatePlan) == 0) {
            return CommonResult.failed("请选择正确的预案");
        }
        return CommonResult.success();
    }

    @Override
    public CommonResult exec(int planId, String userName) {
        if (!havePass(planId)) {
            return CommonResult.failed("当前预案未审核，无法执行。");
        }

        // 是否正在执行
        if (haveRunning(planId)) {
            return CommonResult.failed("当前预案正在执行中，请先完成之前的执行。");
        }

        // 添加预案执行记录
        EmergencyExec emergencyExec = new EmergencyExec();
        emergencyExec.setPlanId(planId);
        emergencyExec.setCreateUser(userName);
        execMapper.insertSelective(emergencyExec);
        emergencyExec.setHistoryId(emergencyExec.getExecId());

        // 获取所有的拓扑关系，添加详细的执行记录
        List<EmergencyExecRecord> allExecRecords = recordMapper.selectAllPlanDetail(planId);
        allExecRecords.forEach(record -> {
            record.setCreateUser(userName);
            record.setExecId(emergencyExec.getExecId());
            if (record.getPerfTestId() != null) { // 如果是自定义脚本压测，根据此压测模板生成压测任务
                createPerfTestByTestId(record);
            }
            recordMapper.insertSelective(record);
        });
        EmergencyPlan updatePlan = new EmergencyPlan();
        updatePlan.setPlanId(planId);
        updatePlan.setUpdateTime(new Date());
        updatePlan.setStatus(PlanStatus.RUNNING.getValue());
        if (allExecRecords.size() == 0) {
            updatePlan.setStatus(PlanStatus.SUCCESS.getValue());
        }
        planMapper.updateByPrimaryKeySelective(updatePlan);

        // 开始执行不需要任何前置条件的场景
        allExecRecords.stream()
            .filter(record -> record.getTaskId() == null && record.getPreSceneId() == null)
            .forEach(record -> {
                LOGGER.debug("Submit record_id={}. exec_id={}, task_id={}.", record.getRecordId(), record.getExecId(),
                    record.getTaskId());
                threadPoolExecutor.execute(handlerFactory.handle(record));
            });
        LOGGER.debug("threadPoolExecutor = {} ", threadPoolExecutor);
        WebSocketServer.sendMessage("/plan/" + planId);
        return CommonResult.success(emergencyExec);
    }

    @Override
    public void createPerfTestByTestId(EmergencyExecRecord record) {
        PerfTest testTemplate = perfTestService.getOne(record.getPerfTestId().longValue());
        PerfTest newTest = new PerfTest();
        BeanUtils.copyProperties(testTemplate, newTest);
        if (newTest.getMonitoringHosts() != null) {
            Set<MonitoringHost> newHosts = new HashSet<>();
            newHosts.addAll(newTest.getMonitoringHosts());
            newTest.setMonitoringHosts(newHosts);
        }
        newTest.setId(null);
        newTest.setCreatedDate(new Date());
        newTest.setCreatedUser(JwtAuthenticationTokenFilter.currentGrinderUser());
        newTest.setStatus(Status.READY);
        if (StringUtils.isNotEmpty(record.getServerId())) {
            final List<String> allServerIds =
                Arrays.stream(record.getServerId().split(",")).collect(Collectors.toList());
            List<Integer> agentIds = serverMapper.selectAgentIdsByServerIds(allServerIds);
            if (agentIds.size() > 0) {
                newTest.setAgentIds(StringUtils.join(agentIds, ","));
            } else {
                LOGGER.warn("Can't found special agent to run. sceneId is {}, taskId is {}", record.getSceneId(), record.getTaskId());
            }
        }
        PerfTest perfTest = perfTestController.saveOne(JwtAuthenticationTokenFilter.currentGrinderUser(), newTest);
        LOGGER.info("create perfTest {}", perfTest.getId());
        record.setPerfTestId(perfTest.getId().intValue());
    }

    @Override
    public CommonResult start(EmergencyPlan plan, String userName) {
        if (plan.getPlanId() == null || StringUtils.isEmpty(plan.getScheduleType())) {
            return CommonResult.failed("请选择正确的预案");
        }
        EmergencyPlan planInfo = planMapper.selectByPrimaryKey(plan.getPlanId());
        if (planInfo == null || ValidEnum.IN_VALID.getValue().equals(planInfo.getIsValid())) {
            return CommonResult.failed("请选择正确的预案");
        }
        if (UN_PASSED_STATUS.contains(planInfo.getStatus())) {
            return CommonResult.failed("请选择已审核通过的预案");
        }
        ScheduleType scheduleType = ScheduleType.match(plan.getScheduleType(), null);
        if (scheduleType == null) {
            return CommonResult.failed("");
        }
        if (scheduleType == ScheduleType.NONE) {
            return exec(plan.getPlanId(), userName);
        }
        if (scheduleType == ScheduleType.CORN) {
            if (StringUtils.isEmpty(plan.getScheduleConf())
                || !CronSequenceGenerator.isValidExpression(plan.getScheduleConf())) {
                return CommonResult.failed("corn表达式不合法");
            }
        }
        if (scheduleType == ScheduleType.FIX_DATE) {
            if (StringUtils.isEmpty(plan.getScheduleConf())) {
                return CommonResult.failed("请设置间隔时间");
            }
            try {
                int fixSecond = Integer.valueOf(plan.getScheduleConf());
                if (fixSecond < 1) {
                    return CommonResult.failed("请设置间隔时间大于1s");
                }
            } catch (NumberFormatException e) {
                return CommonResult.failed("请设置间隔时间为数字");
            }
        }
        long nextTriggerTime = 0L;
        if (scheduleType == ScheduleType.ONCE) {
            try {
                nextTriggerTime = Long.valueOf(plan.getScheduleConf());
                if (System.currentTimeMillis() > nextTriggerTime) {
                    return CommonResult.failed("请设置正确的执行时间");
                }
            } catch (NumberFormatException e) {
                return CommonResult.failed("请设置正确的执行时间");
            }
        } else {
            nextTriggerTime =
                scheduleCenter.calculateNextTriggerTime(plan, new Date(System.currentTimeMillis())).getTime();
        }
        EmergencyPlan updatePlan = new EmergencyPlan();
        updatePlan.setPlanId(plan.getPlanId());
        updatePlan.setStatus(PlanStatus.SCHEDULED.getValue());
        updatePlan.setScheduleStatus(ValidEnum.VALID.getValue());
        updatePlan.setScheduleConf(plan.getScheduleConf());
        updatePlan.setScheduleType(scheduleType.getValue());
        updatePlan.setTriggerLastTime(0L);
        updatePlan.setTriggerNextTime(nextTriggerTime);
        updatePlan.setUpdateTime(new Date());
        updatePlan.setUpdateUser(userName);
        planMapper.updateByPrimaryKeySelective(updatePlan);
        return CommonResult.success();
    }

    @Override
    public CommonResult stop(int planId, String userName) {
        EmergencyPlan plan = planMapper.selectByPrimaryKey(planId);
        if (plan == null || ValidEnum.IN_VALID.getValue().equals(plan.getIsValid())) {
            return CommonResult.failed("请选择正确的预案");
        }
        EmergencyPlan updatePlan = new EmergencyPlan();
        updatePlan.setPlanId(planId);
        updatePlan.setScheduleStatus(ValidEnum.IN_VALID.getValue());
        updatePlan.setStatus(PlanStatus.APPROVED.getValue());
        updatePlan.setUpdateTime(new Date());
        planMapper.updateByPrimaryKeySelective(updatePlan);
        return CommonResult.success();
    }

    @Override
    public void onComplete(EmergencyExecRecord record) {
        LOGGER.debug("Plan exec_id={},plan_id={} is finished.", record.getExecId(), record.getPlanId());
        EmergencyPlan updatePlan = new EmergencyPlan();
        updatePlan.setPlanId(record.getPlanId());
        updatePlan.setStatus(PlanStatus.SUCCESS.getValue());
        WebSocketServer.sendMessage("/plan/" + record.getPlanId());
        planMapper.updateByPrimaryKeySelective(updatePlan);
    }

    @Override
    public CommonResult approve(EmergencyPlan plan, String userName) {
        if (plan.getPlanId() == null) {
            return CommonResult.failed("请选择正确的预案");
        }
        EmergencyPlan planInfo = planMapper.selectByPrimaryKey(plan.getPlanId());
        if (planInfo == null || !PlanStatus.APPROVING.getValue().equals(planInfo.getStatus())) {
            return CommonResult.failed("请选择待审核的预案");
        }

        // 是否正在执行
        if (haveRunning(plan.getPlanId())) {
            return CommonResult.failed("当前预案正在执行中，无法修改审核结果。");
        }
        if (StringUtils.isEmpty(plan.getStatus())) {
            return CommonResult.failed("审核结果不能为空");
        }
        if (!PlanStatus.APPROVED.getValue().equals(plan.getStatus())
            && !PlanStatus.REJECT.getValue().equals(plan.getStatus())) {
            return CommonResult.failed("审核结果不正确");
        }
        EmergencyPlan updatePlan = new EmergencyPlan();
        updatePlan.setPlanId(plan.getPlanId());
        updatePlan.setStatus(plan.getStatus());
        updatePlan.setCheckRemark(plan.getCheckRemark());
        updatePlan.setCheckTime(new Date());
        updatePlan.setCheckUser(userName);
        updatePlan.setUpdateTime(new Date());
        planMapper.updateByPrimaryKeySelective(updatePlan);
        return CommonResult.success();
    }

    @Override
    public CommonResult query(int planId) {
        List<TaskNode> taskNodes = detailMapper.selectSceneNodeByPlanId(planId);
        taskNodes.forEach(scene -> {
            TaskTypeEnum taskTypeEnum = TaskTypeEnum.matchByValue(scene.getTaskType());
            if (taskTypeEnum != null) {
                scene.setTaskType(taskTypeEnum.getDesc());
            }
            List<TaskNode> children = detailMapper.selectTaskNodeBySceneId(scene.getPlanId(), scene.getSceneId());
            if (children.size() > 0) {
                scene.setChildren(children);
            }

            //查找场景下的子任务
            children.forEach(task -> {
                queryPerfTest(task);
                // 查找子任务下的子任务
                List<TaskNode> taskChildren = getChildren(task.getPlanId(), task.getSceneId(), task.getTaskId());
                if (taskChildren.size() > 0) {
                    task.setChildren(taskChildren);
                }
            });
        });
        return CommonResult.success(taskNodes);
    }

    public void queryPerfTest(TaskNode node) {
        if (node.getPerfTestId() == null) {
            return;
        }
        PerfTest test = perfTestService.getOne(node.getPerfTestId().longValue());
        if (test == null) {
            return;
        }
        TaskTypeEnum taskTypeEnum = TaskTypeEnum.matchByValue(node.getTaskType());
        if (taskTypeEnum != null) {
            node.setTaskType(taskTypeEnum.getDesc());
        }
        node.setTestId(test.getId());
        node.setGuiScriptName(node.getScriptName());
        node.setAgent(test.getAgentCount());
        node.setTestName(test.getTestName());
        node.setVuser(test.getVuserPerAgent());
        if (StringUtils.isNotEmpty(test.getTagString())) {
            node.setLabel(Arrays.stream(StringUtils.split(test.getTagString(), ",")).collect(Collectors.toList()));
        }
        node.setDesc(test.getDescription());
        if (StringUtils.isNotEmpty(test.getTargetHosts())) {
            node.setHosts(new ArrayList<>());
            for (String hostStr : test.getTargetHosts().split(",")) {
                TaskNode.HostsDTO hostsDTO = new TaskNode.HostsDTO();
                if (hostStr.indexOf(":") > -1) {
                    hostsDTO.setDomain(hostStr.substring(0, hostStr.indexOf(":")));
                    hostsDTO.setIp(hostStr.substring(hostStr.indexOf(":")));
                } else {

                }
                node.getHosts().add(hostsDTO);
            }
        }
        node.setBasic("D".equals(test.getThreshold()) ? "by_time" : "by_count");
        node.setByCount(test.getRunCount());
        if (test.getDuration() != null) {
            long seconds = test.getDuration() / 1000;
            node.setByTimeH((int) (seconds / 3600));
            node.setByTimeM((int) (seconds % 3600) / 60);
            node.setByTimeS((int) (seconds % 3600) % 60);
        }
        node.setSamplingIgnore(test.getIgnoreSampleCount());
        node.setSamplingInterval(test.getSamplingInterval());
        node.setSafe(test.getSafeDistribution() == null ? false : test.getSafeDistribution());
        node.setTestParam(test.getParam());
        node.setIncreased(test.getUseRampUp() == null ? false : test.getUseRampUp());
        node.setConcurrency(RampUp.THREAD.equals(test.getRampUpType()) ? "线程" : "进程");
        node.setInitValue(test.getRampUpInitCount());
        node.setIncrement(test.getRampUpStep());
        node.setInitWait(test.getRampUpInitSleepTime());
        node.setGrowthInterval(test.getRampUpIncrementInterval());
    }

    @Override
    public CommonResult<EmergencyPlan> get(int planId) {
        EmergencyPlanExample queryExample = new EmergencyPlanExample();
        queryExample.createCriteria()
            .andPlanIdEqualTo(planId)
            .andIsValidEqualTo("1");
        List<EmergencyPlan> emergencyPlans = planMapper.selectByExample(queryExample);
        return CommonResult.success(emergencyPlans.size() > 0 ? emergencyPlans.get(0) : null);
    }

    @Override
    public CommonResult addTask(TaskNode taskNode) {
        TaskTypeEnum taskType = TaskTypeEnum.matchByDesc(taskNode.getTaskType());
        if (taskType == TaskTypeEnum.FLOW_RECORD) {
            return CommonResult.failed("不支持的任务类型");
        }
        EmergencyTask task = new EmergencyTask();
        if (taskType == null) {
            task.setTaskName(taskNode.getTaskName());
            task.setTaskType(TaskTypeEnum.SCENE.getValue());
        } else {
            task.setTaskName(taskNode.getTaskName());
            task.setScriptId(taskNode.getScriptId());
            task.setScriptName(StringUtils.isNotEmpty(taskNode.getScriptName()) ? taskNode.getScriptName()
                : taskNode.getGuiScriptName());
            task.setChannelType(taskNode.getChannelType());
            task.setServerId(StringUtils.join(taskNode.getServiceId(), ","));
            task.setCreateUser(taskNode.getCreateUser());
            task.setTaskType(taskType.getValue());
            if (taskType == TaskTypeEnum.CUSTOM) { // 创建自定义脚本压测任务
                PerfTest perfTest = taskNode.parse();
                perfTest.setCreatedUser(JwtAuthenticationTokenFilter.currentGrinderUser());
                perfTest.setCreatedDate(new Date());
                perfTest.setScriptName(task.getScriptName());
                EmergencyScriptExample scriptExample = new EmergencyScriptExample();
                scriptExample.createCriteria().andScriptNameEqualTo(task.getScriptName());
                final List<EmergencyScript> emergencyScripts = scriptMapper.selectByExample(scriptExample);
                if (emergencyScripts.size() > 0) {
                    perfTest.setScriptName(scriptService.grinderPath(emergencyScripts.get(0)));
                }
                PerfTest insertPerfTest = perfTestController.saveOne(JwtAuthenticationTokenFilter.currentGrinderUser(),
                    perfTest);
                if (insertPerfTest == null || insertPerfTest.getId() == null) {
                    return CommonResult.failed("创建压测任务失败");
                }
                task.setPerfTestId(insertPerfTest.getId().intValue());
            }
        }
        CommonResult<EmergencyTask> addResult = taskService.add(task);
        if (!addResult.isSuccess()) {
            return addResult;
        } else {
            EmergencyTask data = addResult.getData();
            taskNode.setKey(data.getTaskId());
            taskNode.setSubmitInfo(data.getSubmitInfo());
            return CommonResult.success(taskNode);
        }
    }

    @Override
    public CommonResult plan(CommonPage<PlanQueryParams> params, JwtUser jwtUser) {
        Page<PlanQueryDto> pageInfo = PageHelper
            .startPage(params.getPageIndex(), params.getPageSize(), StringUtils.isEmpty(params.getSortType()) ? "" :
                params.getSortField() + System.lineSeparator() + params.getSortType())
            .doSelectPage(() -> {
                planMapper.queryPlanDto(params.getObject());
            });
        List<PlanQueryDto> result = pageInfo.getResult();
        List<String> auth = jwtUser.getAuthList();
        String userName = jwtUser.getUsername();
        String group = jwtUser.getGroupName();
        // 查询明细
        result.forEach(planQueryDto -> {
            if ("approving".equals(planQueryDto.getStatus()) && ("admin".equals(userName) || (
                (auth.contains(AUTH_ADMIN) || (auth.contains(AUTH_APPROVER)
                    && userName.equals(planQueryDto.getCheckUser()))) && group.equals(planQueryDto.getGroupName())))) {
                planQueryDto.setAuditable(true);
            }
            List<PlanDetailQueryDto> planDetails = planMapper.queryPlanDetailDto(planQueryDto.getPlanId());
            planDetails.forEach(planDetail -> {
                if (planDetail.getPerfTestId() == null) {
                    return;
                }
                PerfTest perfTest = perfTestService.getOne(planDetail.getPerfTestId());
                if (perfTest != null) {
                    planDetail.setTestId(perfTest.getId());
                    planDetail.setStartTime(perfTest.getStartTime());
                    planDetail.setDuration(perfTest.getDuration());
                    planDetail.setUserId(perfTest.getUserId());
                    planDetail.setTagString(perfTest.getTagString());
                    planDetail.setTps(perfTest.getTps());
                    planDetail.setMeanTestTime(perfTest.getMeanTestTime());
                    planDetail.setStatus(perfTest.getStatus().getIconName());
                }
            });
            planQueryDto.setExpand(planDetails);
        });
        return CommonResult.success(result, (int) pageInfo.getTotal());
    }

    @Override
    public CommonResult save(int planId, List<TaskNode> listNodes, String userName) {
        if (listNodes == null) {
            return CommonResult.success();
        }
        if (haveRunning(planId)) {
            return CommonResult.failed("正在运行无法修改");
        }

        EmergencyPlan updatePlan = new EmergencyPlan();
        updatePlan.setPlanId(planId);
        updatePlan.setStatus(PlanStatus.NEW.getValue());
        updatePlan.setUpdateTime(new Date());
        planMapper.updateByPrimaryKeySelective(updatePlan);
        taskMapper.tryClearTaskNo(planId);
        EmergencyPlanDetail oldDetails = new EmergencyPlanDetail();
        oldDetails.setIsValid(ValidEnum.IN_VALID.getValue());
        EmergencyPlanDetailExample updateCondition = new EmergencyPlanDetailExample();
        updateCondition.createCriteria().andPlanIdEqualTo(planId);
        detailMapper.updateByExampleSelective(oldDetails, updateCondition);

        String planNO = generatePlanNo(planId);
        Integer preSceneId = null;
        for (int i = 0; i < listNodes.size(); i++) {
            TaskNode scene = listNodes.get(i);
            if (!taskService.isTaskExist(scene.getKey())) {
                continue;
            }

            // 增加关系
            EmergencyPlanDetail insertDetail = new EmergencyPlanDetail();
            insertDetail.setPlanId(planId);
            insertDetail.setSceneId(scene.getKey());
            insertDetail.setCreateUser(userName);
            if ("同步".equals(scene.getSync())) {
                insertDetail.setPreSceneId(preSceneId);
                preSceneId = scene.getKey();
            } else {
                insertDetail.setSync("异步");
            }
            detailMapper.insertSelective(insertDetail);
            EmergencyTask updateScene = new EmergencyTask();
            updateScene.setTaskId(scene.getKey());
            updateScene.setTaskNo(generateSceneNo(planNO, i + 1));
            taskMapper.updateByPrimaryKeySelective(updateScene);
            handleChildren(insertDetail, scene.getChildren(), updateScene.getTaskNo(), false);
        }
        return CommonResult.success();
    }

    @Override
    public CommonResult submit(int planId, String approver) {
        EmergencyPlan plan = planMapper.selectByPrimaryKey(planId);
        if (plan == null || ValidEnum.IN_VALID.equals(plan.getIsValid())) {
            return CommonResult.failed("预案不存在");
        }
        if (!PlanStatus.NEW.getValue().equals(plan.getStatus())
            && !PlanStatus.REJECT.getValue().equals(plan.getStatus())) {
            return CommonResult.failed("预案不为待提审或驳回状态");
        }

        EmergencyPlan updatePlan = new EmergencyPlan();
        updatePlan.setCheckUser(approver);
        updatePlan.setPlanId(planId);
        updatePlan.setStatus(PlanStatus.APPROVING.getValue());
        updatePlan.setUpdateTime(new Date());
        updatePlan.setCheckRemark("");
        planMapper.updateByPrimaryKeySelective(updatePlan);
        return CommonResult.success();
    }

    @Override
    public CommonResult copy(EmergencyPlan emergencyPlan) {
        if (emergencyPlan.getPlanId() == null) {
            return CommonResult.failed("请选择要克隆的预案");
        }
        EmergencyPlan oldPlan = planMapper.selectByPrimaryKey(emergencyPlan.getPlanId());
        emergencyPlan.setPlanGroup(oldPlan.getPlanGroup());
        CommonResult<EmergencyPlan> addResult = add(emergencyPlan);
        if (StringUtils.isNotEmpty(addResult.getMsg())) {
            return addResult;
        }
        EmergencyPlan plan = addResult.getData();

        //复制之前预案下的拓扑关系，重新生成任务号
        CommonResult<List<TaskNode>> queryResult = query(emergencyPlan.getPlanId());
        List<TaskNode> allTasks = queryResult.getData();
        copyTaskNodes(allTasks, emergencyPlan.getCreateUser());
        save(plan.getPlanId(), allTasks, emergencyPlan.getCreateUser());
        return addResult;
    }

    @Override
    public CommonResult updateTask(TaskNode taskNode) {
        if (taskNode == null || taskNode.getKey() == null ) {
            return CommonResult.failed("请选择要操作的任务");
        }
        EmergencyTask originTask = taskMapper.selectByPrimaryKey(taskNode.getKey());
        if (originTask == null) {
            return CommonResult.failed("请选择正确的任务");
        }
        TaskTypeEnum taskTypeEnum = TaskTypeEnum.matchByDesc(taskNode.getTaskType());
        if (taskTypeEnum == null || !taskTypeEnum.getValue().equals(originTask.getTaskType())) {
            return CommonResult.failed("任务类型不可修改");
        }
        EmergencyTask updateTask = new EmergencyTask();
        updateTask.setTaskId(originTask.getTaskId());
        updateTask.setTaskName(taskNode.getTaskName());
        updateTask.setScriptId(taskNode.getScriptId());
        updateTask.setScriptName(
            StringUtils.isNotEmpty(taskNode.getScriptName()) ? taskNode.getScriptName() : taskNode.getGuiScriptName());
        updateTask.setChannelType(taskNode.getChannelType());
        updateTask.setServerId(StringUtils.join(taskNode.getServiceId(), ","));
        EmergencyScriptExample scriptExample = new EmergencyScriptExample();
        scriptExample.createCriteria().andScriptNameEqualTo(updateTask.getScriptName());
        List<EmergencyScript> emergencyScripts = scriptMapper.selectByExample(scriptExample);
        if (emergencyScripts.size() > 0) {
            ScriptLanguageEnum scriptLanguageEnum =
                ScriptLanguageEnum.matchByValue(emergencyScripts.get(0).getScriptType());
            updateTask.setScriptId(emergencyScripts.get(0).getScriptId());
            updateTask.setSubmitInfo(emergencyScripts.get(0).getSubmitInfo());
            updateTask.setScriptName(emergencyScripts.get(0).getScriptName());
            if (originTask.getPerfTestId() != null) {
                PerfTest perfTest = taskNode.parse();
                perfTest.setScriptName(updateTask.getScriptName() + "." + scriptLanguageEnum.getLanguage());
                perfTest.setId(originTask.getPerfTestId().longValue());
                perfTestRepository.saveAndFlush(perfTest);
            }
        }
        taskMapper.updateByPrimaryKeySelective(updateTask);
        return CommonResult.success(taskNode);
    }

    /**
     * 对任务拓扑图中每个任务重新生成新任务
     *
     * @param originTaskNodes
     * @param userName
     */
    public void copyTaskNodes(List<TaskNode> originTaskNodes, String userName) {
        if (originTaskNodes == null) {
            return;
        }
        originTaskNodes.forEach(taskNode -> {
            EmergencyTask newTask = taskMapper.selectByPrimaryKey(taskNode.getKey());
            newTask.setTaskId(null);
            newTask.setTaskNo("");
            newTask.setCreateUser(userName);
            newTask.setCreateTime(new Date());
            newTask.setIsValid(ValidEnum.VALID.getValue());
            newTask.setPerfTestId(taskNode.getPerfTestId());
            taskMapper.insertSelective(newTask);
            taskNode.setKey(newTask.getTaskId());
            copyTaskNodes(taskNode.getChildren(), userName);
        });
    }

    /**
     * 生成子任务
     *
     * @param planDetail 父任务信息
     * @param childrenNode 子任务信息
     * @param parentNo 父任务编号
     * @param isSubTask 是否为子任务
     * @return
     */
    private void handleChildren(EmergencyPlanDetail planDetail, List<TaskNode> childrenNode, String parentNo,
        boolean isSubTask) {
        Integer preTaskId = null;
        if (childrenNode == null) {
            return;
        }
        for (int i = 0; i < childrenNode.size(); i++) {
            TaskNode task = childrenNode.get(i);
            // 任务是否存在
            if (!taskService.isTaskExist(task.getKey())) {
                continue;
            }

            EmergencyPlanDetail insertTaskDetail = new EmergencyPlanDetail();
            insertTaskDetail.setPlanId(planDetail.getPlanId());
            insertTaskDetail.setSceneId(planDetail.getSceneId());
            insertTaskDetail.setTaskId(task.getKey());
            insertTaskDetail.setPreSceneId(planDetail.getPreSceneId());
            insertTaskDetail.setParentTaskId(
                planDetail.getTaskId() == null ? planDetail.getSceneId() : planDetail.getTaskId());
            if ("异步".equals(task.getSync())) {
                insertTaskDetail.setSync("异步");
            } else {
                insertTaskDetail.setPreTaskId(preTaskId);
                preTaskId = task.getKey();
            }
            insertTaskDetail.setCreateUser(planDetail.getCreateUser());
            detailMapper.insertSelective(insertTaskDetail);
            EmergencyTask updateTask = new EmergencyTask();
            updateTask.setTaskId(task.getKey());
            if (isSubTask) {
                updateTask.setTaskNo(generateSubTaskNo(parentNo));
            } else {
                updateTask.setTaskNo(generateTaskNo(parentNo, i + 1));
            }
            taskMapper.updateByPrimaryKeySelective(updateTask);
            handleChildren(insertTaskDetail, task.getChildren(), isSubTask ? parentNo : updateTask.getTaskNo(), true);
        }
    }

    /**
     * 迭代查找此任务的子任务
     *
     * @param taskId 任务ID
     * @return
     */
    public List<TaskNode> getChildren(int planId, int sceneId, int taskId) {
        List<TaskNode> result = detailMapper.selectTaskNodeByTaskId(planId, sceneId, taskId);
        result.forEach(detail -> {
            queryPerfTest(detail);
            List<TaskNode> children = getChildren(detail.getPlanId(), detail.getSceneId(), detail.getTaskId());
            if (children.size() > 0) {
                detail.setChildren(children);
            }
        });
        return result;
    }

    /**
     * 预案是否正在运行
     *
     * @param planId 预案ID
     * @return
     */
    private boolean haveRunning(int planId) {
        EmergencyExecRecordExample isRunningCondition = new EmergencyExecRecordExample();
        isRunningCondition.createCriteria()
            .andStatusIn(RecordStatus.HAS_RUNNING_STATUS)
            .andPlanIdEqualTo(planId)
            .andIsValidEqualTo(ValidEnum.VALID.getValue());
        return recordMapper.countByExample(isRunningCondition) > 0;
    }

    /**
     * 预案是否通过审核
     *
     * @param planId 预案ID
     * @return
     */
    private boolean havePass(int planId) {
        EmergencyPlanExample havePassCondition = new EmergencyPlanExample();
        havePassCondition.createCriteria()
            .andStatusNotIn(UN_PASSED_STATUS)
            .andIsValidEqualTo(ValidEnum.VALID.getValue())
            .andPlanIdEqualTo(planId);
        return planMapper.countByExample(havePassCondition) > 0;
    }

    public String generatePlanNo(int planId) {
        return String.format(Locale.ROOT, "P%06d", planId);
    }

    public String generateSceneNo(String planNo, int index) {
        return String.format(Locale.ROOT, "%sS%02d", planNo, validIndex(index));
    }

    public String generateTaskNo(String sceneNo, int index) {
        return String.format(Locale.ROOT, "%sT%02d", sceneNo, validIndex(index));
    }

    public String generateSubTaskNo(String taskNo) {
        String preFix = taskNo + "C";
        int index = taskMapper.selectMaxSubTaskNo(preFix);
        return String.format(Locale.ROOT, "%s%02d", preFix, validIndex(index + 1));
    }

    public int validIndex(int index) {
        int result = Math.abs(index);
        if (index > 99) {
            throw new RuntimeException("最大子任务数量不能超过99");
        }
        return result;
    }
}
