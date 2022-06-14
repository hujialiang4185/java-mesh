/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.emergency.service.impl;

import com.huawei.argus.restcontroller.RestPerfTestController;
import com.huawei.common.api.CommonResult;
import com.huawei.common.constant.AgentStatusEnum;
import com.huawei.common.constant.RecordStatus;
import com.huawei.common.constant.ValidEnum;
import com.huawei.common.exception.ApiException;
import com.huawei.common.util.PasswordUtil;
import com.huawei.common.ws.WebSocketServer;
import com.huawei.emergency.entity.EmergencyAgent;
import com.huawei.emergency.entity.EmergencyAgentExample;
import com.huawei.emergency.entity.EmergencyExecRecord;
import com.huawei.emergency.entity.EmergencyExecRecordDetail;
import com.huawei.emergency.entity.EmergencyExecRecordDetailExample;
import com.huawei.emergency.entity.EmergencyExecRecordExample;
import com.huawei.emergency.entity.EmergencyScript;
import com.huawei.emergency.entity.EmergencyServer;
import com.huawei.emergency.entity.EmergencyServerExample;
import com.huawei.emergency.mapper.EmergencyAgentMapper;
import com.huawei.emergency.mapper.EmergencyExecRecordDetailMapper;
import com.huawei.emergency.mapper.EmergencyExecRecordMapper;
import com.huawei.emergency.mapper.EmergencyScriptMapper;
import com.huawei.emergency.mapper.EmergencyServerMapper;
import com.huawei.emergency.service.EmergencyPlanService;
import com.huawei.emergency.service.EmergencySceneService;
import com.huawei.emergency.service.EmergencyTaskService;
import com.huawei.script.exec.ExecResult;
import com.huawei.script.exec.executor.ScriptExecInfo;
import com.huawei.script.exec.log.LogMemoryStore;
import com.huawei.script.exec.session.ServerInfo;

import org.apache.commons.lang.StringUtils;
import org.ngrinder.common.constant.WebConstants;
import org.ngrinder.model.PerfTest;
import org.ngrinder.model.Status;
import org.ngrinder.model.User;
import org.ngrinder.perftest.service.PerfTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

/**
 * 工厂类,用于提供脚本运行记录处理器
 *
 * @author y30010171
 * @since 2021-11-04
 **/
@Service
public class ExecRecordHandlerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecRecordHandlerFactory.class);
    private static final int RETRY_TIMES = 10;
    private static final long RETRY_SLEEP_TIME = 1000L;
    private static final String SPLIT_SIGN = ",";
    @Autowired
    private EmergencyTaskService taskService;

    @Autowired
    private EmergencySceneService sceneService;

    @Autowired
    private EmergencyExecRecordMapper recordMapper;

    @Autowired
    private EmergencyExecRecordDetailMapper recordDetailMapper;

    @Autowired
    private EmergencyServerMapper serverMapper;

    @Resource(name = "passwordRestTemplate")
    private RestTemplate restTemplate;

    @Value("${script.timeOut}")
    private long timeOut;

    @Value("${ngrinder.auto.execute.test}")
    private boolean autoTest;

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private RestPerfTestController perfTestController;

    @Autowired
    private PerfTestService perfTestService;

    @Autowired
    private EmergencyPlanService planService;

    @Autowired
    private EmergencyScriptMapper scriptMapper;

    @Autowired
    private EmergencyAgentMapper agentMapper;

    /**
     * 获取一个执行器实例
     *
     * @param currentRecord 需要执行的脚本记录
     * @return Runnable
     */
    public Runnable handle(EmergencyExecRecord currentRecord) {
        return new ExecRecordHandler(currentRecord);
    }

    public Runnable handleDetail(EmergencyExecRecord record, EmergencyExecRecordDetail recordDetail) {
        return new ExecRecordDetailHandler(record, recordDetail);
    }

    /**
     * 执行任务
     *
     * @param record 任务执行记录
     * @param recordDetail 任务执行记录明细
     */
    @Transactional(rollbackFor = Exception.class)
    public void exec(EmergencyExecRecord record, EmergencyExecRecordDetail recordDetail) {
        EmergencyExecRecordDetail updateRecordDetail = new EmergencyExecRecordDetail();
        updateRecordDetail.setDetailId(recordDetail.getDetailId());
        updateRecordDetail.setStartTime(new Date());
        updateRecordDetail.setStatus(RecordStatus.RUNNING.getValue());
        recordDetailMapper.updateByPrimaryKeySelective(updateRecordDetail); // 更新recordDetail的开始时间和状态
        recordMapper.tryUpdateStartTime(record.getRecordId(), updateRecordDetail.getStartTime()); // 更新record的开始时间
        recordMapper.tryUpdateStatus(record.getRecordId()); // 更新record的状态
        ScriptExecInfo execInfo;
        try {
            execInfo = generateExecInfo(record, recordDetail); // 生成执行信息
            if (record.getScriptId() != null) {
                EmergencyScript script = scriptMapper.selectByPrimaryKey(record.getScriptId());
                if (script != null && !"2".equals(script.getScriptStatus())) {
                    complete(record, recordDetail, ExecResult.fail("脚本未审核通过."));
                    return;
                }
            }
            if (execInfo.getPerfTestId() != null) {
                if (!startPerfTest(execInfo.getPerfTestId())) { // 执行压测任务
                    complete(record, recordDetail, ExecResult.fail("启动压测任务失败."));
                    return;
                }
            } else {
                if (record.getScriptContent() == null) {
                    complete(record, recordDetail, ExecResult.success("脚本为空"));
                    return;
                }
                if (execInfo.getRemoteServerInfo() == null) {
                    complete(record, recordDetail, ExecResult.fail("无可用的agent"));
                    return;
                }
                ServerInfo remoteServerInfo = execInfo.getRemoteServerInfo();
                String url =
                    String.format(Locale.ROOT, "http://%s:%s/agent/execute", remoteServerInfo.getServerIp(),
                        remoteServerInfo.getServerPort());
                CommonResult result = restTemplate.postForObject(url, execInfo, CommonResult.class);
                if (StringUtils.isNotEmpty(result.getMsg())) {
                    LOGGER.error("Failed to exec script, {}", result.getMsg());
                    complete(record, recordDetail, ExecResult.fail(result.getMsg()));
                }
            }
        } catch (RestClientException e) {
            LOGGER.error("Failed to process script, {}", e.getMessage());
            complete(record, recordDetail, ExecResult.fail(e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("Failed to exec detailId={}.{}", recordDetail.getDetailId(), e.getMessage());
            complete(record, recordDetail, ExecResult.fail(e.getMessage()));
        }
    }

    /**
     * 执行完成之后
     *
     * @param record
     * @param recordDetail
     * @param execResult
     */
    @Transactional(rollbackFor = Exception.class)
    public void complete(EmergencyExecRecord record, EmergencyExecRecordDetail recordDetail, ExecResult execResult) {
        try {
            // 更新record,detail为执行完成，结束时间
            Date endTime = new Date();
            EmergencyExecRecordDetailExample whenRunning = new EmergencyExecRecordDetailExample();
            whenRunning.createCriteria()
                .andDetailIdEqualTo(recordDetail.getDetailId())
                .andIsValidEqualTo(ValidEnum.VALID.getValue())
                .andStatusEqualTo(RecordStatus.RUNNING.getValue());
            EmergencyExecRecordDetail updateRecordDetail = new EmergencyExecRecordDetail();
            updateRecordDetail.setDetailId(recordDetail.getDetailId());
            updateRecordDetail.setEndTime(endTime);
            updateRecordDetail.setLog(execResult.getMsg());
            if (execResult.isError()) {
                StringBuilder finalLog = new StringBuilder();
                for (String log : LogMemoryStore.removeLog(recordDetail.getDetailId())) {
                    finalLog.append(log).append(System.lineSeparator());
                }
                finalLog.append(execResult.getMsg());
                updateRecordDetail.setLog(finalLog.toString());
            }
            updateRecordDetail.setStatus(
                execResult.isSuccess() ? RecordStatus.SUCCESS.getValue() : RecordStatus.ENSURE_FAILED.getValue()
            );
            if (recordDetailMapper.updateByExampleSelective(updateRecordDetail, whenRunning)
                == 0) { // 做个状态判断，防止人为取消 也被标记为执行成功
                LOGGER.info("recordId={}, detailId={} was canceled", recordDetail.getRecordId(),
                    recordDetail.getDetailId());
            } else {
                recordMapper.tryUpdateEndTimeAndLog(record.getRecordId(), endTime, updateRecordDetail.getLog());
                recordMapper.tryUpdateStatus(record.getRecordId());

                // 执行成功 并且 当前record下所有的recordDetail都处于 执行成功 或者人工确认状态
                if (execResult.isSuccess() && isRecordFinished(record.getRecordId())) {
                    if (record.getTaskId() != null) {
                        taskService.onComplete(record);
                    } else {
                        sceneService.onComplete(record);
                    }
                }
            }

            // 清除实时日志的在内存中的日志残留
            LogMemoryStore.removeLog(recordDetail.getDetailId());
        } finally {
            notifySceneRefresh(record.getExecId(), record.getSceneId());
        }
    }

    /**
     * 压测任务完成
     *
     * @param record 执行记录
     * @param detail 执行记录明细
     * @param result 执行结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void completePerfTest(EmergencyExecRecord record, EmergencyExecRecordDetail detail, ExecResult result) {
        Date endTime = new Date();
        try {
            Thread.sleep(RETRY_SLEEP_TIME * 3); // 子任务可能出现与任务选择同一个agent，但是agent状态还处于busy未刷新
        } catch (InterruptedException e) {
            LOGGER.error("while waiting agent status refresh.", e);
        }
        try {
            // 更新record,detail为执行完成，结束时间
            EmergencyExecRecordDetailExample whenRunning = new EmergencyExecRecordDetailExample();
            whenRunning.createCriteria()
                .andDetailIdEqualTo(detail.getDetailId())
                .andIsValidEqualTo(ValidEnum.VALID.getValue())
                .andStatusEqualTo(RecordStatus.RUNNING.getValue());
            EmergencyExecRecordDetail updateRecordDetail = new EmergencyExecRecordDetail();
            updateRecordDetail.setEndTime(endTime);
            updateRecordDetail.setLog(result.getMsg());
            updateRecordDetail.setStatus(
                result.isSuccess() ? RecordStatus.SUCCESS.getValue() : RecordStatus.FAILED.getValue());
            if (recordDetailMapper.updateByExampleSelective(updateRecordDetail, whenRunning) > 0) { // 更新所有detail的状态
                EmergencyExecRecord updateRecord = new EmergencyExecRecord();
                updateRecord.setEndTime(endTime);
                updateRecord.setLog(result.getMsg());
                updateRecord.setRecordId(detail.getRecordId());
                recordMapper.updateByPrimaryKeySelective(updateRecord); // 更新record的信息
                recordMapper.tryUpdateStatus(detail.getRecordId()); // 执行成功 并且 当前record下所有的recordDetail都处于 执行成功 或者人工确认状态
                if (result.isSuccess() && isRecordFinished(detail.getRecordId())) {
                    if (record.getTaskId() != null) {
                        taskService.onComplete(record);
                    } else {
                        sceneService.onComplete(record);
                    }
                }
            }

            // 清除实时日志的在内存中的日志残留
            LogMemoryStore.removeLog(detail.getRecordId());
        } finally {
            notifySceneRefresh(detail.getExecId(), record.getSceneId());
        }
    }

    /**
     * 生成可执行的信息
     *
     * @param record 执行记录
     * @param recordDetail 执行记录明细
     * @return {@link ScriptExecInfo} 可执行信息
     */
    public ScriptExecInfo generateExecInfo(EmergencyExecRecord record,
        EmergencyExecRecordDetail recordDetail) {
        ScriptExecInfo execInfo = new ScriptExecInfo();
        execInfo.setDetailId(recordDetail.getDetailId());
        execInfo.setScriptName(record.getScriptName() + "-" + record.getRecordId());
        execInfo.setScriptType(record.getScriptType());
        execInfo.setScriptContent(record.getScriptContent());
        execInfo.setTimeOut(timeOut);
        if (StringUtils.isNotEmpty(record.getScriptParams())) {
            String[] split = record.getScriptParams().split(SPLIT_SIGN);
            execInfo.setParams(split);
        }
        if (recordDetail.getPerfTestId() == null) { // 非压测任务
            if (recordDetail.getAgentId() != null) {
                EmergencyAgent agent = agentMapper.selectByPrimaryKey(recordDetail.getAgentId());
                ServerInfo serverInfo = new ServerInfo(agent.getAgentIp(), agent.getAgentPort());
                execInfo.setRemoteServerInfo(serverInfo);
            } else if (recordDetail.getServerId() != null) { // 兼容之前只选择server的任务
                EmergencyServer server = serverMapper.selectByPrimaryKey(recordDetail.getServerId());
                ServerInfo serverInfo = new ServerInfo(server.getServerIp(), server.getServerUser(), 9095);
                execInfo.setRemoteServerInfo(serverInfo);
            }
        }
        execInfo.setPerfTestId(recordDetail.getPerfTestId());
        execInfo.setParam(record.getScriptParams());
        execInfo.setContent(execInfo.getScriptContent());
        return execInfo;
    }

    /**
     * 根据选择执行的服务器信息来生成任务分发明细
     *
     * @param record 执行记录
     * @return 执行记录明细
     */
    @Transactional(rollbackFor = Exception.class)
    public List<EmergencyExecRecordDetail> generateRecordDetail(EmergencyExecRecord record) {
        if (record.getTaskId() == null) { // 场景记录
            EmergencyExecRecordDetail recordDetail = new EmergencyExecRecordDetail();
            recordDetail.setExecId(record.getExecId());
            recordDetail.setRecordId(record.getRecordId());
            recordDetail.setStatus(RecordStatus.PENDING.getValue());
            recordDetailMapper.insertSelective(recordDetail);
            return Arrays.asList(recordDetail);
        }
        if (StringUtils.isEmpty(record.getServerId()) && StringUtils.isEmpty(record.getAgentIds())) {
            LOGGER.warn("The server list is empty. recordId={}", record.getRecordId());
            throw new ApiException("未选择执行的agent");
        }
        try {
            List<EmergencyServer> serverList = new ArrayList<>();
            if (StringUtils.isNotEmpty(record.getServerId())) {
                List<Integer> serverIdList = Arrays.stream(record.getServerId().split(SPLIT_SIGN))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
                EmergencyServerExample allServerExample = new EmergencyServerExample();
                allServerExample.createCriteria()
                    .andServerIdIn(serverIdList)
                    .andIsValidEqualTo(ValidEnum.VALID.getValue());
                serverList = filterServer(serverMapper.selectByExample(allServerExample));
            }
            return record.getPerfTestId() == null ? createNormalRecordDetail(record, serverList)
                : createPerfTestRecordDetail(record, serverList);
        } catch (NumberFormatException e) {
            LOGGER.error("parse record.serverId error,recordId={}. {}", record.getRecordId(), e.getMessage());
        }
        return Collections.emptyList();
    }

    private List<EmergencyExecRecordDetail> createNormalRecordDetail(EmergencyExecRecord record,
        List<EmergencyServer> serverList) {
        List<EmergencyExecRecordDetail> result = new ArrayList<>();
        if (StringUtils.isNotEmpty(record.getAgentIds())) { // 选择了agent,
            EmergencyAgentExample agentExample = new EmergencyAgentExample();
            agentExample.createCriteria()
                .andAgentIdIn(Arrays.stream(record.getAgentIds().split(SPLIT_SIGN)).map(Integer::valueOf).collect(
                    Collectors.toList()));
            agentMapper.selectByExample(agentExample).forEach(agent -> {
                if (ValidEnum.IN_VALID.equals(agent.getIsValid()) || AgentStatusEnum.INACTIVE.getValue()
                    .equals(agent.getAgentStatus())) {
                    LOGGER.warn("agent {} [{}:{}] is invalid or inactive.", agent.getAgentName(), agent.getAgentIp(),
                        agent.getAgentPort());
                    return;
                }
                EmergencyExecRecordDetail recordDetail = new EmergencyExecRecordDetail();
                recordDetail.setExecId(record.getExecId());
                recordDetail.setRecordId(record.getRecordId());
                recordDetail.setStatus(RecordStatus.PENDING.getValue());
                EmergencyServerExample serverExample = new EmergencyServerExample();
                serverExample.createCriteria().andServerIpEqualTo(agent.getAgentIp())
                    .andIsValidEqualTo(ValidEnum.VALID.getValue());
                List<EmergencyServer> servers = serverMapper.selectByExample(serverExample);
                if (servers.size() > 0) {
                    recordDetail.setServerId(servers.get(0).getServerId());
                }
                recordDetail.setAgentId(agent.getAgentId());
                recordDetail.setServerIp(agent.getAgentIp());
                recordDetailMapper.insertSelective(recordDetail);
                result.add(recordDetail);
            });
            return result;
        }
        for (EmergencyServer server : serverList) { // 兼容之前选择server的任务
            if (server == null) {
                continue;
            }
            EmergencyExecRecordDetail recordDetail = new EmergencyExecRecordDetail();
            recordDetail.setExecId(record.getExecId());
            recordDetail.setRecordId(record.getRecordId());
            recordDetail.setStatus(RecordStatus.PENDING.getValue());
            recordDetail.setServerId(server.getServerId());
            recordDetail.setServerIp(server.getServerIp());
            recordDetailMapper.insertSelective(recordDetail);
            result.add(recordDetail);
        }
        return result;
    }

    private List<EmergencyExecRecordDetail> createPerfTestRecordDetail(EmergencyExecRecord record,
        List<EmergencyServer> serverList) {
        EmergencyExecRecordDetail recordDetail = new EmergencyExecRecordDetail();
        recordDetail.setExecId(record.getExecId());
        recordDetail.setRecordId(record.getRecordId());
        recordDetail.setStatus(RecordStatus.PENDING.getValue());
        if (record.getPerfTestId() != null) { // 如果是自定义脚本压测，根据此压测模板生成压测任务
            PerfTest perfTest = planService.copyPerfTestByTestId(record.getPerfTestId());
            if (StringUtils.isNotEmpty(record.getAgentIds())) {
                perfTest.setAgentIds(record.getAgentIds());
            } else {
                List<Integer> agentIds = new ArrayList<>();
                for (EmergencyServer server : serverList) {
                    List<Integer> agent = serverMapper.selectAgentIdsByServerIds(
                        Arrays.asList(server.getServerId().toString()));
                    if (agent.size() > 0) {
                        agentIds.add(agent.get(0));
                        LOGGER.debug("found agent {} by ip {}.", agent.get(0), server.getServerIp());
                    } else {
                        LOGGER.warn("can't found agent by ip {}.", server.getServerIp());
                    }
                }
                if (agentIds.size() > 0) {
                    perfTest.setAgentIds(StringUtils.join(agentIds, SPLIT_SIGN));
                }
            }
            perfTestService.save(perfTest.getCreatedUser(), perfTest);
            recordDetail.setPerfTestId(perfTest.getId().intValue());
        }
        recordDetailMapper.insertSelective(recordDetail);
        return Arrays.asList(recordDetail);
    }

    /**
     * 过滤需要分发的服务器
     *
     * @param serverList 服务器列表
     * @return 过滤后的服务器列表
     */
    public List<EmergencyServer> filterServer(List<EmergencyServer> serverList) {
        return serverList;
    }

    /**
     * 解析密码
     *
     * @param mode 模式
     * @param source 密文
     * @return 解密后的密码
     */
    public String parsePassword(String mode, String source) {
        if ("0".equals(mode)) {
            try {
                return passwordUtil.decodePassword(source);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Decode password error.", e);
                return source;
            }
        }
        return "123456";
    }

    /**
     * 判断此任务记录record是否已经完成，即判断该record下是否存在未完成的record_detail
     *
     * @param recordId record主键
     * @return 是否完成
     */
    public boolean isRecordFinished(int recordId) {
        EmergencyExecRecordDetailExample isFinished = new EmergencyExecRecordDetailExample();
        isFinished.createCriteria()
            .andRecordIdEqualTo(recordId)
            .andIsValidEqualTo(ValidEnum.VALID.getValue())
            .andStatusIn(RecordStatus.HAS_RUNNING_STATUS);
        return recordDetailMapper.countByExample(isFinished) == 0;
    }

    /**
     * 向前端推送刷新执行记录下，场景页面的通知
     *
     * @param execId 执行Id
     * @param sceneId 场景ID
     */
    public void notifySceneRefresh(int execId, int sceneId) {
        EmergencyExecRecordExample sceneRecordCondition = new EmergencyExecRecordExample();
        sceneRecordCondition.createCriteria()
            .andIsValidEqualTo(ValidEnum.VALID.getValue())
            .andExecIdEqualTo(execId)
            .andSceneIdEqualTo(sceneId)
            .andTaskIdIsNull();
        final List<EmergencyExecRecord> emergencyExecRecords = recordMapper.selectByExample(sceneRecordCondition);
        if (emergencyExecRecords.size() > 0) {
            WebSocketServer.sendMessage("/scena/" + emergencyExecRecords.get(0).getRecordId());
        }
    }

    public boolean startPerfTest(Integer perfTestId) {
        if (perfTestId == null) {
            return false;
        }
        PerfTest perfTest = perfTestService.getOne(perfTestId.longValue());
        if (perfTest == null) {
            return false;
        }
        User user = new User();
        user.setUserId(perfTest.getCreatedUser().getUserId());
        if (autoTest) {
            perfTest.setStatus(Status.READY);
            perfTestService.save(user, perfTest); // 开启自动执行的情况下，将压测任务置为ready即可自动执行
            return true;
        } else {
            Map<String, Object> resultMap =
                perfTestController.startOne(user, perfTestId.longValue());
            return Boolean.parseBoolean(resultMap.getOrDefault(WebConstants.JSON_SUCCESS, "false").toString());
        }
    }

    /**
     * 停止运行
     *
     * @param execRecord 执行记录
     */
    @Transactional(rollbackFor = Exception.class)
    public CommonResult stopRecord(EmergencyExecRecord execRecord) {
        EmergencyExecRecordDetailExample recordDetailExample = new EmergencyExecRecordDetailExample();
        recordDetailExample.createCriteria()
            .andRecordIdEqualTo(execRecord.getRecordId())
            .andIsValidEqualTo(ValidEnum.VALID.getValue());
        List<EmergencyExecRecordDetail> recordDetails = recordDetailMapper.selectByExample(recordDetailExample);
        recordDetails.stream()
            .filter(recordDetail -> RecordStatus.PENDING.getValue().equals(recordDetail.getStatus()))
            .forEach(recordDetail -> {
                recordDetail.setStatus(RecordStatus.CANCEL.getValue());
                recordDetail.setEndTime(new Date());
                recordDetail.setLog("执行取消");
                recordDetailMapper.updateByPrimaryKeySelective(recordDetail);
            });
        recordDetails.stream()
            .filter(recordDetail -> RecordStatus.RUNNING.getValue().equals(recordDetail.getStatus()))
            .forEach(recordDetail -> {
                if (recordDetail.getPerfTestId() != null) {
                    PerfTest one = perfTestService.getOne(recordDetail.getPerfTestId().longValue());
                    perfTestService.stop(one.getCreatedUser(), one.getId());
                } else {
                    String ip = "";
                    Integer port = 0;
                    if (recordDetail.getAgentId() != null) {
                        EmergencyAgent agent = agentMapper.selectByPrimaryKey(recordDetail.getAgentId());
                        ip = agent.getAgentIp();
                        port = agent.getAgentPort();
                    } else {
                        EmergencyServer server = serverMapper.selectByPrimaryKey(recordDetail.getServerId());
                        ip = server.getServerIp();
                        port = 9095;
                    }
                    ScriptExecInfo execInfo = new ScriptExecInfo();
                    execInfo.setDetailId(recordDetail.getDetailId());
                    execInfo.setScriptType(execRecord.getScriptType());
                    String url =
                        String.format(Locale.ROOT, "http://%s:%s/agent/cancel", ip, port);
                    restTemplate.postForObject(url, execInfo, CommonResult.class);
                }
            });
        return CommonResult.success();
    }

    /**
     * 脚本执行器
     *
     * @author y30010171
     * @since 2021-11-04
     **/
    public class ExecRecordHandler implements Runnable {
        private final EmergencyExecRecord currentRecord;

        ExecRecordHandler(EmergencyExecRecord currentRecord) {
            this.currentRecord = currentRecord;
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public void run() {
            EmergencyExecRecord record = recordMapper.selectByPrimaryKey(currentRecord.getRecordId());
            int retryTimes = RETRY_TIMES;
            while (record == null && retryTimes > 0) { // 出现事务还未提交，此时查不到这条数据
                try {
                    Thread.sleep(RETRY_SLEEP_TIME);
                    record = recordMapper.selectByPrimaryKey(currentRecord.getRecordId());
                } catch (InterruptedException e) {
                    LOGGER.error("interrupted while wait for exec recordId={}", record.getRecordId());
                } finally {
                    retryTimes--;
                }
            }
            if (record == null) {
                LOGGER.error("record was not commit. {}", record.getRecordId());
                return;
            }
            try {
                List<EmergencyExecRecordDetail> emergencyExecRecordDetails = generateRecordDetail(record);
                if (emergencyExecRecordDetails.size() == 0) {
                    throw new ApiException("选择的agent不可用");
                }
                if (RecordStatus.CANCEL.getValue().equals(record.getStatus())) {
                    emergencyExecRecordDetails.forEach(recordDetail -> {
                        recordDetail.setStatus(RecordStatus.CANCEL.getValue());
                        recordDetail.setLog("执行取消");
                        recordDetailMapper.updateByPrimaryKeySelective(recordDetail);
                    });
                    return;
                }
                if (!RecordStatus.PENDING.getValue().equals(record.getStatus())) {
                    RecordStatus recordStatus = RecordStatus.matchByValue(record.getStatus());
                    LOGGER.error("record {} status is {}, can't exec. ", record.getRecordId(), recordStatus);
                    throw new ApiException("任务状态不为待执行。当前状态为 " + recordStatus);
                }
                EmergencyExecRecord finalRecord = record;
                emergencyExecRecordDetails.forEach(
                    recordDetail -> exec(finalRecord,
                        recordDetail));
            } catch (ApiException | IllegalArgumentException e) {
                LOGGER.error("failed to generateRecordDetail. {}.{}", record.getRecordId(), e.getMessage());
                EmergencyExecRecord errorRecord = new EmergencyExecRecord();
                errorRecord.setRecordId(record.getRecordId());
                errorRecord.setLog(e.getMessage());
                errorRecord.setStatus(RecordStatus.FAILED.getValue());
                recordMapper.updateByPrimaryKeySelective(errorRecord);
                notifySceneRefresh(record.getExecId(), record.getSceneId());
            }
        }
    }

    /**
     * 脚本任务分发执行器
     *
     * @author y30010171
     * @since 2021-11-04
     **/
    public class ExecRecordDetailHandler implements Runnable {
        private final EmergencyExecRecord record; // 任务信息
        private final EmergencyExecRecordDetail recordDetail; // 任务分发明细

        private ExecRecordDetailHandler(EmergencyExecRecord record, EmergencyExecRecordDetail recordDetail) {
            this.record = record;
            this.recordDetail = recordDetail;
        }

        @Override
        public void run() {
            exec(record, recordDetail);
        }
    }
}
