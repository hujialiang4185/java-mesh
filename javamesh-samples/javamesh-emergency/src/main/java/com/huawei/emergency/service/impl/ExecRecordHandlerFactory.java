/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.emergency.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.huawei.common.api.CommonResult;
import com.huawei.common.config.Config;
import com.huawei.common.constant.RecordStatus;
import com.huawei.common.constant.ValidEnum;
import com.huawei.common.exception.ApiException;
import com.huawei.common.ngrinder.service.NgrinderSceneService;
import com.huawei.common.ngrinder.service.NgrinderTestService;
import com.huawei.common.util.PasswordUtil;
import com.huawei.common.ws.WebSocketServer;
import com.huawei.emergency.entity.EmergencyExecRecord;
import com.huawei.emergency.entity.EmergencyExecRecordDetail;
import com.huawei.emergency.entity.EmergencyExecRecordDetailExample;
import com.huawei.emergency.entity.EmergencyExecRecordExample;
import com.huawei.emergency.entity.EmergencyExecRecordWithBLOBs;
import com.huawei.emergency.entity.EmergencyServer;
import com.huawei.emergency.entity.EmergencyServerExample;
import com.huawei.emergency.mapper.EmergencyElementMapper;
import com.huawei.emergency.mapper.EmergencyExecRecordDetailMapper;
import com.huawei.emergency.mapper.EmergencyExecRecordMapper;
import com.huawei.emergency.mapper.EmergencyServerMapper;
import com.huawei.emergency.service.EmergencySceneService;
import com.huawei.emergency.service.EmergencyTaskService;
import com.huawei.script.exec.ExecResult;
import com.huawei.script.exec.executor.ScriptExecInfo;
import com.huawei.script.exec.log.LogMemoryStore;
import com.huawei.script.exec.session.ServerInfo;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 工厂类,用于提供脚本运行记录处理器
 *
 * @author y30010171
 * @since 2021-11-04
 **/
@Service
public class ExecRecordHandlerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecRecordHandlerFactory.class);

    @Autowired
    private EmergencyTaskService taskService;

    @Autowired
    private EmergencySceneService sceneService;

    @Autowired
    private EmergencyExecRecordMapper recordMapper;

    @Autowired
    private EmergencyExecRecordDetailMapper recordDetailMapper;

    @Autowired
    EmergencyServerMapper serverMapper;

    @Value("${argus.url}")
    private String argusUrl;

    @Resource(name = "passwordRestTemplate")
    private RestTemplate restTemplate;

    @Value("${script.timeOut}")
    private long timeOut;

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private NgrinderSceneService ngrinderSceneService;

    @Autowired
    private NgrinderTestService ngrinderTestService;

    @Autowired
    EmergencyElementMapper elementMapper;

    /**
     * 获取一个执行器实例
     *
     * @param currentRecord 需要执行的脚本记录
     * @return Runnable
     */
    public Runnable handle(EmergencyExecRecord currentRecord) {
        return new ExecRecordHandler(currentRecord);
    }

    public Runnable handleDetail(EmergencyExecRecordWithBLOBs record, EmergencyExecRecordDetail recordDetail) {
        return new ExecRecordDetailHandler(record, recordDetail);
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
        public void run() {
            EmergencyExecRecordWithBLOBs record = recordMapper.selectByPrimaryKey(currentRecord.getRecordId());

            // 出现事务还未提交，此时查不到这条数据
            int retryTimes = 10;
            while (record == null && retryTimes > 0) {
                record = recordMapper.selectByPrimaryKey(currentRecord.getRecordId());
                retryTimes--;
            }
            if (record == null || !RecordStatus.PENDING.getValue().equals(record.getStatus())) {
                return;
            }
            try {
                List<EmergencyExecRecordDetail> emergencyExecRecordDetails = generateRecordDetail(record);
                EmergencyExecRecordWithBLOBs finalRecord = record;
                emergencyExecRecordDetails.forEach(recordDetail -> {
                    handle(finalRecord, recordDetail);
                });
            } catch (ApiException e) {
                EmergencyExecRecordWithBLOBs errorRecord = new EmergencyExecRecordWithBLOBs();
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
        private final EmergencyExecRecordWithBLOBs record; // 任务信息
        private final EmergencyExecRecordDetail recordDetail; // 任务分发明细

        private ExecRecordDetailHandler(EmergencyExecRecordWithBLOBs record, EmergencyExecRecordDetail recordDetail) {
            this.record = record;
            this.recordDetail = recordDetail;
        }

        @Override
        public void run() {
            handle(record, recordDetail);
        }
    }

    public void handle(EmergencyExecRecordWithBLOBs record, EmergencyExecRecordDetail recordDetail) {
        EmergencyExecRecordDetail updateRecordDetail = new EmergencyExecRecordDetail();
        updateRecordDetail.setDetailId(recordDetail.getDetailId());
        updateRecordDetail.setStartTime(new Date());
        updateRecordDetail.setStatus(RecordStatus.RUNNING.getValue());
        recordDetailMapper.updateByPrimaryKeySelective(updateRecordDetail); // 更新recordDetail的开始时间和状态
        recordMapper.tryUpdateStartTime(record.getRecordId(), updateRecordDetail.getStartTime()); //更新record的开始时间
        recordMapper.tryUpdateStatus(record.getRecordId()); //更新record的状态
        ScriptExecInfo execInfo = generateExecInfo(record, recordDetail); // 生成执行信息
        if (execInfo.getRemoteServerInfo() == null) {
            complete(record, recordDetail, ExecResult.fail("无可用的agent"));
            return;
        }
        if (record.getScriptContent() == null) {
            complete(record, recordDetail, ExecResult.success(""));
            return;
        }
        ServerInfo remoteServerInfo = execInfo.getRemoteServerInfo();
        String url = String.format(Locale.ROOT, "http://%s:%s/agent/execute", remoteServerInfo.getServerIp(), remoteServerInfo.getServerPort());
        execInfo.setRemoteServerInfo(null);
        try {
            CommonResult result = restTemplate.postForObject(url, execInfo, CommonResult.class);
            if (!result.isSuccess()) {
                // todo 调用失败
                LOGGER.error("Failed to exec script, {}", result.getMsg());
                complete(record, recordDetail, ExecResult.fail(result.getMsg()));
            }
        } catch (RestClientException e) {
            LOGGER.error("Failed to process script, {}", e.getMessage());
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
    public void complete(EmergencyExecRecord record, EmergencyExecRecordDetail recordDetail, ExecResult execResult) {
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
            for (String s : LogMemoryStore.removeLog(recordDetail.getDetailId())) {
                finalLog.append(s).append(System.lineSeparator());
            }
            finalLog.append(execResult.getMsg());
            updateRecordDetail.setLog(finalLog.toString());
        }
        updateRecordDetail.setStatus(
            execResult.isSuccess() ? RecordStatus.SUCCESS.getValue() : RecordStatus.ENSURE_FAILED.getValue()
        );
        if (recordDetailMapper.updateByExampleSelective(updateRecordDetail, whenRunning) == 0) { // 做个状态判断，防止人为取消 也被标记为执行成功
            LOGGER.info("recordId={}, detailId={} was canceled", recordDetail.getRecordId(), recordDetail.getDetailId());
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
        notifySceneRefresh(record.getExecId(), record.getSceneId());
    }


    /**
     * 生成可执行的信息
     *
     * @param record
     * @param recordDetail
     * @return
     */
    public ScriptExecInfo generateExecInfo(EmergencyExecRecordWithBLOBs record, EmergencyExecRecordDetail recordDetail) {
        ScriptExecInfo execInfo = new ScriptExecInfo();
        execInfo.setId(recordDetail.getDetailId());
        execInfo.setScriptName(record.getScriptName() + "-" + record.getRecordId());
        execInfo.setScriptType(record.getScriptType());
        execInfo.setScriptContext(record.getScriptContent());
        execInfo.setTimeOut(timeOut);
        if (StringUtils.isNotEmpty(record.getScriptParams())) {
            String[] split = record.getScriptParams().split(",");
            execInfo.setParams(split);
        }
        if (recordDetail.getServerId() != null) {
            EmergencyServer server = serverMapper.selectByPrimaryKey(recordDetail.getServerId());
            if (server != null) {
                ServerInfo serverInfo = new ServerInfo(server.getServerIp(), server.getServerUser(), server.getAgentPort());
                execInfo.setRemoteServerInfo(serverInfo);
            }
        }
        /*if ("3".equals(execInfo.getScriptType())) {
            EmergencyElementExample elementExample = new EmergencyElementExample();
            elementExample.createCriteria()
                .andScriptIdEqualTo(record.getScriptId())
                .andIsValidEqualTo(ValidEnum.VALID.getValue())
                .andElementTypeEqualTo("Root");
            List<EmergencyElement> rootElements = elementMapper.selectByExample(elementExample); // 查找编排脚本root节点
            if (rootElements.size() == 0) {
                return execInfo;
            }
            Integer id = record.getSceneId() == null ? record.getTaskId() : record.getSceneId();
            String perfSceneName = "测试场景-" + id;
            createArgusScene(record.getScriptName(), perfSceneName); // 不存在则创建压测场景。
            String elementParams = rootElements.get(0).getElementParams();
            JSONObject jsonObject = JSONObject.parseObject(elementParams);
            jsonObject.put("scenario_name", perfSceneName);
            int argusTest = createArgusTest(record.getRecordId(), jsonObject);//创建压测任务

            // todo 保存压测任务
        }*/
        return execInfo;
    }

    /**
     * 生成任务分发明细
     *
     * @param record
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<EmergencyExecRecordDetail> generateRecordDetail(EmergencyExecRecord record) {
        List<EmergencyExecRecordDetail> result = new ArrayList<>();
        if (StringUtils.isNotEmpty(record.getServerId())) {
            try {
                List<Integer> serverIdList = Arrays.stream(record.getServerId().split(","))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
                EmergencyServerExample allServerExample = new EmergencyServerExample();
                allServerExample.createCriteria()
                    .andServerIdIn(serverIdList)
                    .andIsValidEqualTo(ValidEnum.VALID.getValue());
                List<EmergencyServer> serverList = filterServer(serverMapper.selectByExample(allServerExample));
                if (serverList.size() == 0) {
                    throw new ApiException("选择的agent服务器不可用");
                }
                for (EmergencyServer server : serverList) {
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
            } catch (NumberFormatException e) {
                LOGGER.error("parse record.serverId error,recordId={}. {}", record.getRecordId(), e.getMessage());
            }
        } else {
            EmergencyExecRecordDetail recordDetail = new EmergencyExecRecordDetail();
            recordDetail.setExecId(record.getExecId());
            recordDetail.setRecordId(record.getRecordId());
            recordDetail.setStatus(RecordStatus.PENDING.getValue());
            recordDetail.setServerIp(record.getServerIp());
            recordDetailMapper.insertSelective(recordDetail);
            result.add(recordDetail);
        }
        return result;
    }

    /**
     * 选择分发的服务器
     *
     * @return
     */
    public List<EmergencyServer> filterServer(List<EmergencyServer> serverList) {
        if (serverList == null || serverList.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        return serverList.stream()
            .filter(server -> server != null && server.getAgentPort() != null && ValidEnum.VALID.getValue().equals(server.getLicensed()))
            .collect(Collectors.toList());
    }

    /**
     * 解析密码
     *
     * @param mode   模式
     * @param source 密文
     * @return
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
     * @return
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
     * @param execId  执行Id
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

    public void freshArgus(int planId) {
    }

    public int createArgusTest(int recordId, JSONObject testParams) {
        String url = argusUrl + "/api/task";
        try {
            ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, testParams.toJSONString(), JSONObject.class);
            if (response.getStatusCodeValue() != HttpServletResponse.SC_OK) {
                LOGGER.error("failed to create grinder task {}. {}", recordId, response);
                return -1;
            }
            JSONObject jsonObject = response.getBody();
            if (Boolean.parseBoolean(jsonObject.getOrDefault("success", "true").toString())) {
                return Integer.valueOf(jsonObject.getOrDefault("id", "-1").toString());
            } else {
                LOGGER.error("failed to create grinder script {}. {}", recordId, jsonObject);
                return -1;
            }
        } catch (RestClientException e) {
            LOGGER.error("failed to create grinder script {}. {}", recordId, e.getMessage());
            return -1;
        }
    }

    public void createArgusScene(String scriptName, String sceneName) {
        JSONObject params = new JSONObject();
        params.put("app_name", "测试应用");
        params.put("scenario_name", sceneName);
        params.put("desc", "create at " + System.currentTimeMillis());
        params.put("script_path", Config.GRINDER_FOLDER + "/" + scriptName + ".groovy");
        params.put("label", "");
        params.put("scenario_type", "自定义脚本");
        LOGGER.info("create grinder scene. {}", ngrinderSceneService.create(params.toString()));
    }

}
