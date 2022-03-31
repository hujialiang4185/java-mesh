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
import com.huawei.emergency.entity.EmergencyServer;
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
import org.ngrinder.model.User;
import org.ngrinder.perftest.repository.PerfTestRepository;
import org.ngrinder.perftest.service.PerfTestService;
import org.ngrinder.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import javax.annotation.Resource;

/**
 * 项目管理接口的实现类
 *
 * @author y30010171
 * @since 2021-11-02
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class EmergencyPlanServiceImpl implements EmergencyPlanService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmergencyPlanServiceImpl.class);
    private static final String MODE_TEST = "test";
    private static final String MODE_DEV = "dev";

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

    @Autowired
    private UserRepository userRepository;

    private static final String AUTH_ADMIN = "admin";
    private static final String AUTH_APPROVER = "approver";

    @Autowired
    private EmergencyServerMapper serverMapper;

    @Autowired
    private EmergencyScriptMapper scriptMapper;

    @Autowired
    private EmergencyScriptService scriptService;

    @Value("${mode}")
    private String mode;

    @Override
    public CommonResult add(EmergencyPlan emergencyPlan) {
        if (StringUtils.isEmpty(emergencyPlan.getPlanName())) {
            return CommonResult.failed("请填写项目名称");
        }
        EmergencyPlanExample isPlanNameExist = new EmergencyPlanExample();
        isPlanNameExist.createCriteria()
            .andPlanNameEqualTo(emergencyPlan.getPlanName())
            .andIsValidEqualTo(ValidEnum.VALID.getValue());
        if (planMapper.countByExample(isPlanNameExist) > 0) {
            return CommonResult.failed("已存在名称相同的项目");
        }
        EmergencyPlan insertPlan = new EmergencyPlan();
        insertPlan.setPlanName(emergencyPlan.getPlanName());
        insertPlan.setCreateUser(emergencyPlan.getCreateUser());
        insertPlan.setPlanGroup(emergencyPlan.getPlanGroup());
        insertPlan.setUpdateTime(new Date());
        updateStatusByMode(insertPlan);
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
            return CommonResult.failed("请选择正确的项目");
        }

        if (havePass(emergencyPlan.getPlanId())) {
            return CommonResult.failed("审核通过后不能删除");
        }

        // 是否正在执行
        if (haveRunning(emergencyPlan.getPlanId())) {
            return CommonResult.failed("当前项目正在执行中，无法删除。");
        }

        EmergencyPlan updatePlan = new EmergencyPlan();
        updatePlan.setIsValid(ValidEnum.IN_VALID.getValue());
        updatePlan.setPlanId(emergencyPlan.getPlanId());
        updatePlan.setUpdateTime(new Date());
        if (planMapper.updateByPrimaryKeySelective(updatePlan) == 0) {
            return CommonResult.failed("请选择正确的项目");
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
            return CommonResult.failed("请选择正确的项目");
        }

        // 是否审核通过
        if (havePass(emergencyPlan.getPlanId())) {
            return CommonResult.failed("当前项目已经审核通过，无法修改。");
        }

        EmergencyPlan updatePlan = new EmergencyPlan();
        updatePlan.setPlanId(emergencyPlan.getPlanId());
        updatePlan.setPlanNo(emergencyPlan.getPlanNo());
        updatePlan.setPlanName(emergencyPlan.getPlanName());
        if (planMapper.updateByPrimaryKeySelective(updatePlan) == 0) {
            return CommonResult.failed("请选择正确的项目");
        }
        return CommonResult.success();
    }

    @Override
    public CommonResult exec(int planId, String userName) {
        if (!havePass(planId)) {
            return CommonResult.failed("当前项目未审核，无法执行。");
        }

        // 是否正在执行
        if (haveRunning(planId)) {
            return CommonResult.failed("当前项目正在执行中，请先完成之前的执行。");
        }

        // 添加项目执行记录
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
            /*if (record.getPerfTestId() != null) { // 如果是自定义脚本压测，根据此压测模板生成压测任务
                PerfTest perfTest = copyPerfTestByTestId(record.getPerfTestId());
                record.setPerfTestId(perfTest.getId().intValue());
            }*/
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
    public PerfTest copyPerfTestByTestId(Integer perfTestId) {
        if (perfTestId == null) {
            return new PerfTest();
        }
        PerfTest testTemplate = perfTestService.getOne(perfTestId.longValue());
        PerfTest newTest = new PerfTest();
        BeanUtils.copyProperties(testTemplate, newTest);
        if (newTest.getMonitoringHosts() != null) {
            Set<MonitoringHost> newHosts = new HashSet<>();
            newHosts.addAll(newTest.getMonitoringHosts());
            newTest.setMonitoringHosts(newHosts);
        }
        newTest.setId(null);
        newTest.setCreatedDate(new Date());
        newTest.setCreatedUser(testTemplate.getCreatedUser());
        newTest.setStatus(Status.READY);
        PerfTest perfTest = perfTestController.saveOne(testTemplate.getCreatedUser(), newTest);
        LOGGER.info("create perfTest {}", perfTest.getId());
        return perfTest;
    }

    @Override
    public User findNgrinderUserByUserId(String userId) {
        return userRepository.findOneByUserId(userId);
    }

    @Override
    public Optional<User> findNgrinderUserById(Long id) {
        return userRepository.findById(id);
    }

    public String generateSelectAgentIds(List<EmergencyServer> serverIds) {
        if (serverIds == null || serverIds.size() == 0) {
            return null;
        }
        List<String> allServerIds = serverIds.stream()
            .filter(server -> server.getServerId() != null)
            .map(server -> server.getServerId().toString())
            .collect(Collectors.toList());
        List<Integer> agentIds = serverMapper.selectAgentIdsByServerIds(allServerIds);
        if (agentIds.size() > 0) {
            return StringUtils.join(agentIds, ",");
        }
        LOGGER.warn("Can't found special agent to run. serverIds is {}", allServerIds);
        return null;
    }

    @Override
    public CommonResult start(EmergencyPlan plan, String userName) {
        if (plan.getPlanId() == null || StringUtils.isEmpty(plan.getScheduleType())) {
            return CommonResult.failed("请选择正确的项目");
        }
        EmergencyPlan planInfo = planMapper.selectByPrimaryKey(plan.getPlanId());
        if (planInfo == null || ValidEnum.IN_VALID.getValue().equals(planInfo.getIsValid())) {
            return CommonResult.failed("请选择正确的项目");
        }
        if (UN_PASSED_STATUS.contains(planInfo.getStatus())) {
            return CommonResult.failed("请选择已审核通过的项目");
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
                    return CommonResult.failed("启动时间不得早于当前时间");
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
            return CommonResult.failed("请选择正确的项目");
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
            return CommonResult.failed("请选择正确的项目");
        }
        EmergencyPlan planInfo = planMapper.selectByPrimaryKey(plan.getPlanId());
        if (planInfo == null || !PlanStatus.APPROVING.getValue().equals(planInfo.getStatus())) {
            return CommonResult.failed("请选择待审核的项目");
        }

        // 是否正在执行
        if (haveRunning(plan.getPlanId())) {
            return CommonResult.failed("当前项目正在执行中，无法修改审核结果。");
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
                TaskTypeEnum subTaskTypeEnum = TaskTypeEnum.matchByValue(task.getTaskType());
                if (subTaskTypeEnum != null) {
                    task.setTaskType(subTaskTypeEnum.getDesc());
                }
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
        if (taskType == null || taskType == TaskTypeEnum.FLOW_RECORD) {
            return CommonResult.failed("不支持的任务类型");
        }
        EmergencyTask task = new EmergencyTask();
        if (taskType == TaskTypeEnum.SCENE) {
            task.setTaskName(taskNode.getTaskName());
            task.setTaskType(taskType.getValue());
            task.setCreateUser(taskNode.getCreateUser());
        } else {
            if (StringUtils.isEmpty(taskNode.getScriptName())) {
                return CommonResult.failed("请选择脚本");
            }
            task.setTaskName(taskNode.getTaskName());
            task.setScriptId(taskNode.getScriptId());
            task.setScriptName(StringUtils.isNotEmpty(taskNode.getScriptName()) ? taskNode.getScriptName()
                : taskNode.getGuiScriptName());
            task.setChannelType(taskNode.getChannelType());
            if (taskNode.getServiceId() != null) {
                task.setServerId(StringUtils.join(taskNode.getServiceId().stream()
                    .filter(server -> server.getServerId() != null)
                    .map(EmergencyServer::getServerId)
                    .collect(Collectors.toList()), ","));
            }
            task.setCreateUser(taskNode.getCreateUser());
            task.setTaskType(taskType.getValue());
            if (taskType == TaskTypeEnum.CUSTOM) { // 创建自定义脚本压测任务
                PerfTest perfTest = taskNode.parse();
                perfTest.setScriptName(task.getScriptName());
                EmergencyScriptExample scriptExample = new EmergencyScriptExample();
                scriptExample.createCriteria().andScriptNameEqualTo(task.getScriptName());
                final List<EmergencyScript> emergencyScripts = scriptMapper.selectByExample(scriptExample);
                if (emergencyScripts.size() == 0) {
                    return CommonResult.failed("请选择正确的脚本");
                }
                User ngrinderUser = findNgrinderUserByUserId(emergencyScripts.get(0).getScriptUser());
                perfTest.setCreatedUser(ngrinderUser);
                perfTest.setCreatedDate(new Date());
                perfTest.setScriptName(scriptService.grinderPath(emergencyScripts.get(0)));
                PerfTest insertPerfTest = perfTestController.saveOne(ngrinderUser, perfTest);
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
                    planDetail.setTagString(perfTest.getTagString());
                    planDetail.setTps(perfTest.getTps());
                    planDetail.setMeanTestTime(perfTest.getMeanTestTime());
                    long testCount =
                        (perfTest.getTests() == null ? 0L : perfTest.getTests()) + (perfTest.getErrors() == null ? 0L
                            : perfTest.getErrors());
                    if (testCount == 0) {
                        planDetail.setErrorRate(0D);
                    } else {
                        planDetail.setErrorRate(
                            (perfTest.getErrors() == null ? 0D : perfTest.getErrors().doubleValue()) / testCount);
                    }
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
        updateStatusByMode(updatePlan);
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
            if ("异步".equals(scene.getSync())) {
                insertDetail.setSync("异步");
            } else {
                insertDetail.setPreSceneId(preSceneId);
                preSceneId = scene.getKey();
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
            return CommonResult.failed("项目不存在");
        }
        if (!PlanStatus.NEW.getValue().equals(plan.getStatus())
            && !PlanStatus.REJECT.getValue().equals(plan.getStatus())) {
            return CommonResult.failed("项目不为待提审或驳回状态");
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
            return CommonResult.failed("请选择要克隆的项目");
        }
        EmergencyPlan oldPlan = planMapper.selectByPrimaryKey(emergencyPlan.getPlanId());
        emergencyPlan.setPlanGroup(oldPlan.getPlanGroup());
        CommonResult<EmergencyPlan> addResult = add(emergencyPlan);
        if (StringUtils.isNotEmpty(addResult.getMsg())) {
            return addResult;
        }
        EmergencyPlan plan = addResult.getData();

        //复制之前项目下的拓扑关系，重新生成任务号
        CommonResult<List<TaskNode>> queryResult = query(emergencyPlan.getPlanId());
        List<TaskNode> allTasks = queryResult.getData();
        copyTaskNodes(allTasks, emergencyPlan.getCreateUser());
        save(plan.getPlanId(), allTasks, emergencyPlan.getCreateUser());
        return addResult;
    }

    @Override
    public CommonResult updateTask(TaskNode taskNode) {
        if (taskNode == null || taskNode.getKey() == null) {
            return CommonResult.failed("请选择要操作的任务");
        }
        List<EmergencyPlan> taskPlans = detailMapper.selectPlanByTaskId(taskNode.getKey());
        for (EmergencyPlan taskPlan : taskPlans) {
            if (haveRunning(taskPlan.getPlanId())) {
                return CommonResult.failed(String.format(Locale.ROOT, "存在项目[%s]处于执行态，无法修改。", taskPlan.getPlanName()));
            }
        }
        EmergencyTask originTask = taskMapper.selectByPrimaryKey(taskNode.getKey());
        if (originTask == null) {
            return CommonResult.failed("请选择正确的任务");
        }
        TaskTypeEnum taskTypeEnum = TaskTypeEnum.matchByDesc(taskNode.getTaskType());
        if (taskTypeEnum == null || !taskTypeEnum.getValue().equals(originTask.getTaskType())) {
            return CommonResult.failed("任务类型不可修改");
        }
        if (taskTypeEnum == TaskTypeEnum.SCENE) {
            EmergencyTask updateTask = new EmergencyTask();
            updateTask.setTaskId(originTask.getTaskId());
            updateTask.setTaskName(taskNode.getTaskName());
            updateTask.setTaskDesc(StringUtils.isEmpty(taskNode.getScenaDesc()) ? "" : taskNode.getScenaDesc());
            taskMapper.updateByPrimaryKeySelective(updateTask);
            return CommonResult.success(taskNode);
        }
        EmergencyTask updateTask = new EmergencyTask();
        updateTask.setTaskId(originTask.getTaskId());
        updateTask.setTaskName(taskNode.getTaskName());
        updateTask.setScriptId(taskNode.getScriptId());
        updateTask.setScriptName(
            StringUtils.isNotEmpty(taskNode.getScriptName()) ? taskNode.getScriptName() : taskNode.getGuiScriptName());
        updateTask.setChannelType(taskNode.getChannelType());
        if (taskNode.getServiceId() != null) {
            updateTask.setServerId(StringUtils.join(taskNode.getServiceId().stream()
                .filter(server -> server.getServerId() != null)
                .map(EmergencyServer::getServerId)
                .collect(Collectors.toList()), ","));
        } else {
            updateTask.setServerId("");
        }
        EmergencyScriptExample scriptExample = new EmergencyScriptExample();
        scriptExample.createCriteria().andScriptNameEqualTo(updateTask.getScriptName());
        List<EmergencyScript> emergencyScripts = scriptMapper.selectByExample(scriptExample);
        if (emergencyScripts.size() > 0) {
            updateTask.setScriptId(emergencyScripts.get(0).getScriptId());
            updateTask.setSubmitInfo(emergencyScripts.get(0).getSubmitInfo());
            updateTask.setScriptName(emergencyScripts.get(0).getScriptName());
        }
        if (originTask.getPerfTestId() != null) {
            PerfTest perfTest = taskNode.parse();
            if (emergencyScripts.get(0) != null) {
                perfTest.setScriptName(scriptService.grinderPath(emergencyScripts.get(0)));
            }
            perfTest.setId(originTask.getPerfTestId().longValue());
            perfTestRepository.saveAndFlush(perfTest);
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
            if (taskNode.getPerfTestId() != null) {
                PerfTest perfTest = copyPerfTestByTestId(taskNode.getPerfTestId());
                newTask.setPerfTestId(perfTest.getId().intValue());
            }
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
            TaskTypeEnum taskTypeEnum = TaskTypeEnum.matchByValue(detail.getTaskType());
            if (taskTypeEnum != null) {
                detail.setTaskType(taskTypeEnum.getDesc());
            }
            queryPerfTest(detail);
            List<TaskNode> children = getChildren(detail.getPlanId(), detail.getSceneId(), detail.getTaskId());
            if (children.size() > 0) {
                detail.setChildren(children);
            }
        });
        return result;
    }

    /**
     * 项目是否正在运行
     *
     * @param planId 项目ID
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
     * 项目是否通过审核
     *
     * @param planId 项目ID
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

    public void updateStatusByMode(EmergencyPlan plan) {
        // 如果是dev或test则不需要提审审核脚本
        if (MODE_DEV.equals(mode) || MODE_TEST.equals(mode)) {
            plan.setStatus(PlanStatus.APPROVED.getValue());
        } else {
            plan.setStatus(PlanStatus.NEW.getValue());
        }
    }
}
