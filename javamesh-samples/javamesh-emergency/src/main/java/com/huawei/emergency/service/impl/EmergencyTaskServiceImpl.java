/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.emergency.service.impl;

import com.huawei.common.api.CommonResult;
import com.huawei.common.constant.RecordStatus;
import com.huawei.common.constant.ValidEnum;
import com.huawei.emergency.dto.TaskCommonReport;
import com.huawei.emergency.entity.EmergencyExecRecord;
import com.huawei.emergency.entity.EmergencyExecRecordExample;
import com.huawei.emergency.entity.EmergencyScript;
import com.huawei.emergency.entity.EmergencyScriptExample;
import com.huawei.emergency.entity.EmergencyTask;
import com.huawei.emergency.entity.EmergencyTaskExample;
import com.huawei.emergency.mapper.EmergencyExecRecordMapper;
import com.huawei.emergency.mapper.EmergencyScriptMapper;
import com.huawei.emergency.mapper.EmergencyTaskMapper;
import com.huawei.emergency.service.EmergencySceneService;
import com.huawei.emergency.service.EmergencyTaskService;

import org.apache.commons.lang.StringUtils;
import org.ngrinder.model.PerfTest;
import org.ngrinder.perftest.service.PerfTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

/**
 * 任务管理接口实现类
 *
 * @author y30010171
 * @since 2021-11-04
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class EmergencyTaskServiceImpl implements EmergencyTaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmergencyTaskServiceImpl.class);

    @Resource(name = "scriptExecThreadPool")
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private EmergencyTaskMapper taskMapper;

    @Autowired
    private EmergencyExecRecordMapper execRecordMapper;

    @Autowired
    private EmergencyScriptMapper scriptMapper;

    @Autowired
    private EmergencySceneService sceneService;

    @Autowired
    private ExecRecordHandlerFactory handlerFactory;

    @Autowired
    private PerfTestService perfTestService;

    @Override
    public void onComplete(EmergencyExecRecord record) {
        LOGGER.debug("Task exec_id={},plan_id={},scene_id={},task_id={} is finished.", record.getExecId(),
            record.getPlanId(), record.getSceneId(), record.getTaskId());
        if (sceneService.isSceneFinished(record.getExecId(), record.getSceneId())) {
            sceneService.onComplete(record);
            return;
        }
        if (isTaskFinished(record)) {
            // 执行依赖此任务的任务
            EmergencyExecRecordExample needExecTaskCondition = new EmergencyExecRecordExample();
            needExecTaskCondition.createCriteria()
                .andExecIdEqualTo(record.getExecId())
                .andPreTaskIdEqualTo(record.getTaskId())
                .andIsValidEqualTo(ValidEnum.VALID.getValue())
                .andStatusEqualTo(RecordStatus.PENDING.getValue());
            List<EmergencyExecRecord> needExecTasks = execRecordMapper.selectByExample(needExecTaskCondition);
            needExecTasks.forEach(execRecord -> {
                LOGGER.debug("Submit record_id={}. exec_id={}, task_id={}.", execRecord.getRecordId(),
                    execRecord.getExecId(), execRecord.getTaskId());
                threadPoolExecutor.execute(handlerFactory.handle(execRecord));
            });
            if (needExecTasks.size() == 0) {
                if (record.getParentTaskId().equals(record.getSceneId())) {
                    EmergencyExecRecordExample parentRecordCondition = new EmergencyExecRecordExample();
                    parentRecordCondition.createCriteria()
                        .andExecIdEqualTo(record.getExecId())
                        .andIsValidEqualTo(ValidEnum.VALID.getValue())
                        .andSceneIdEqualTo(record.getSceneId())
                        .andTaskIdIsNull();
                    List<EmergencyExecRecord> parentRecords = execRecordMapper.selectByExample(parentRecordCondition);
                    parentRecords.forEach(sceneService::onComplete);
                } else {
                    EmergencyExecRecordExample parentRecordCondition = new EmergencyExecRecordExample();
                    parentRecordCondition.createCriteria()
                        .andExecIdEqualTo(record.getExecId())
                        .andIsValidEqualTo(ValidEnum.VALID.getValue())
                        .andTaskIdEqualTo(record.getParentTaskId());
                    List<EmergencyExecRecord> parentRecords = execRecordMapper.selectByExample(parentRecordCondition);
                    parentRecords.forEach(this::onComplete);
                }
            }
        } else {
            // 执行子任务
            EmergencyExecRecordExample subTask = new EmergencyExecRecordExample();
            subTask.createCriteria()
                .andExecIdEqualTo(record.getExecId())
                .andIsValidEqualTo(ValidEnum.VALID.getValue())
                .andStatusEqualTo(RecordStatus.PENDING.getValue())
                .andPreTaskIdIsNull()
                .andParentTaskIdEqualTo(record.getTaskId());
            List<EmergencyExecRecord> emergencyExecRecords = execRecordMapper.selectByExample(subTask);
            emergencyExecRecords.forEach(execRecord -> {
                LOGGER.debug("Submit record_id={}. exec_id={}, task_id={}, task_detail_id={}.",
                    execRecord.getRecordId(), execRecord.getExecId(), execRecord.getTaskId());
                threadPoolExecutor.execute(handlerFactory.handle(execRecord));
            });
        }
    }

    private boolean isTaskFinished(EmergencyExecRecord task) {
        EmergencyExecRecordExample finishedCondition = new EmergencyExecRecordExample();
        finishedCondition.createCriteria()
            .andExecIdEqualTo(task.getExecId())
            .andParentTaskIdEqualTo(task.getTaskId())
            .andIsValidEqualTo("1");
        List<EmergencyExecRecord> emergencyExecRecords = execRecordMapper.selectByExample(finishedCondition);
        long runningCount = emergencyExecRecords.stream()
            .filter(record -> RecordStatus.HAS_RUNNING_STATUS.contains(record.getStatus()))
            .count();
        if (runningCount > 0L) {
            return false;
        }
        for (EmergencyExecRecord subTask : emergencyExecRecords) {
            if (!isTaskFinished(subTask)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public CommonResult<EmergencyTask> add(EmergencyTask emergencyTask) {
        if (StringUtils.isEmpty(emergencyTask.getTaskName())) {
            return CommonResult.failed("请填写任务名称");
        }
        EmergencyTask insertTask = new EmergencyTask();
        if (StringUtils.isNotEmpty(emergencyTask.getScriptName())) {
            EmergencyScriptExample scriptExample = new EmergencyScriptExample();
            scriptExample.createCriteria().andScriptNameEqualTo(emergencyTask.getScriptName());
            final List<EmergencyScript> emergencyScripts = scriptMapper.selectByExample(scriptExample);
            if (emergencyScripts.size() > 0) {
                insertTask.setScriptId(emergencyScripts.get(0).getScriptId());
                insertTask.setSubmitInfo(emergencyScripts.get(0).getSubmitInfo());
                insertTask.setScriptName(emergencyScripts.get(0).getScriptName());
            }
        }
        insertTask.setServerId(emergencyTask.getServerId());
        insertTask.setTaskName(emergencyTask.getTaskName());
        insertTask.setChannelType(emergencyTask.getChannelType());
        insertTask.setCreateUser(emergencyTask.getCreateUser());
        insertTask.setPerfTestId(emergencyTask.getPerfTestId());
        insertTask.setTaskType(emergencyTask.getTaskType());
        if (emergencyTask.getScriptId() != null) {
            EmergencyScript script = scriptMapper.selectByPrimaryKey(emergencyTask.getScriptId());
            if (script != null) {
                insertTask.setSubmitInfo(script.getSubmitInfo());
                insertTask.setScriptName(script.getScriptName());
            }
        }
        taskMapper.insertSelective(insertTask);
        return CommonResult.success(insertTask);
    }

    @Override
    public CommonResult delete(EmergencyTask emergencyTask) {
        if (emergencyTask.getTaskId() == null) {
            return CommonResult.failed("请选择正确的任务");
        }

        EmergencyTask updateTask = new EmergencyTask();
        updateTask.setIsValid(ValidEnum.IN_VALID.getValue());
        updateTask.setTaskId(emergencyTask.getTaskId());
        if (taskMapper.updateByPrimaryKeySelective(updateTask) == 0) {
            return CommonResult.failed("删除失败");
        }
        return CommonResult.success();
    }

    @Override
    public CommonResult update(EmergencyTask emergencyTask) {
        if (emergencyTask.getTaskId() == null) {
            return CommonResult.failed("请选择正确的任务");
        }

        // 验证预案是否已经通过审核
        if (taskMapper.countPassedPlanByTaskId(emergencyTask.getTaskId()) > 0) {
            return CommonResult.failed("无法操作已经审核通过的预案");
        }

        EmergencyTask updateTask = new EmergencyTask();
        updateTask.setTaskNo(emergencyTask.getTaskNo());
        updateTask.setTaskName(emergencyTask.getTaskName());
        updateTask.setTaskId(emergencyTask.getTaskId());
        if (taskMapper.updateByPrimaryKeySelective(updateTask) == 0) {
            return CommonResult.failed("修改失败");
        }
        return CommonResult.success();
    }

    @Override
    public boolean isTaskExist(int taskId) {
        EmergencyTaskExample existCondition = new EmergencyTaskExample();
        existCondition.createCriteria()
            .andTaskIdEqualTo(taskId)
            .andIsValidEqualTo(ValidEnum.VALID.getValue());
        return taskMapper.countByExample(existCondition) > 0;
    }

    @Override
    public CommonResult getCommonReport(Long perfTestId) {
        if (perfTestId == null) {
            return CommonResult.failed("请选择压测任务");
        }
        final PerfTest perfTest = perfTestService.getOne(perfTestId);
        if (perfTest == null) {
            return CommonResult.success();
        }
        TaskCommonReport commonReport = TaskCommonReport.parse(perfTest);
        commonReport.setPlugins(perfTestService.getAvailableReportPlugins(perfTestId));
        return CommonResult.success(commonReport);
    }

    @Override
    public CommonResult getTaskReport(Integer recordId) {
        if (recordId == null) {
            return CommonResult.success();
        }
        List<TaskCommonReport> commonReportList = taskMapper.getTaskReportByRecordId(recordId);
        return CommonResult.success(commonReportList.toArray());
    }
}
