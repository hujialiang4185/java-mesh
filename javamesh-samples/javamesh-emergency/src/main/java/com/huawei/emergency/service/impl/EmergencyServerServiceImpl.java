/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.emergency.service.impl;

import com.huawei.common.api.CommonPage;
import com.huawei.common.api.CommonResult;
import com.huawei.common.constant.AgentStatusEnum;
import com.huawei.common.constant.ValidEnum;
import com.huawei.common.util.PasswordUtil;
import com.huawei.common.ws.WebSocketServer;
import com.huawei.emergency.dto.ServerAgentInfoDTO;
import com.huawei.emergency.entity.EmergencyAgentConfig;
import com.huawei.emergency.entity.EmergencyAgentExample;
import com.huawei.emergency.entity.EmergencyAgentExample.Criteria;
import com.huawei.emergency.entity.EmergencyServer;
import com.huawei.emergency.entity.EmergencyServerExample;
import com.huawei.emergency.mapper.EmergencyAgentConfigMapper;
import com.huawei.emergency.mapper.EmergencyAgentMapper;
import com.huawei.emergency.mapper.EmergencyServerMapper;
import com.huawei.emergency.service.EmergencyServerService;
import com.huawei.script.exec.ExecResult;
import com.huawei.script.exec.executor.RemoteScriptExecutor;
import com.huawei.script.exec.session.ServerInfo;
import com.huawei.script.exec.session.ServerSessionFactory;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import org.apache.commons.lang.StringUtils;
import org.ngrinder.agent.service.AgentManagerService;
import org.ngrinder.agent.service.AgentPackageService;
import org.ngrinder.infra.config.Config;
import org.ngrinder.model.AgentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import javax.annotation.Resource;

/**
 * 服务器信息管理
 *
 * @author y30010171
 * @since 2021-11-29
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class EmergencyServerServiceImpl implements EmergencyServerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmergencyServerServiceImpl.class);

    @Value("${agent.uploadPath}")
    private String uploadPath;

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private EmergencyServerMapper serverMapper;

    @Autowired
    private RemoteScriptExecutor remoteExecutor;

    @Autowired
    private ServerSessionFactory sessionFactory;

    @Resource(name = "sendAgentThreadPool")
    ThreadPoolExecutor executor;

    @Autowired
    private AgentPackageService packageService;

    @Autowired
    private Config config;

    @Autowired
    private AgentManagerService agentManagerService;

    @Autowired
    private EmergencyAgentConfigMapper agentConfigMapper;

    @Autowired
    private EmergencyAgentMapper agentMapper;

    @Override
    public CommonResult<EmergencyServer> add(EmergencyServer server) {
        if (StringUtils.isEmpty(server.getServerIp()) || StringUtils.isEmpty(server.getServerName())) {
            return CommonResult.failed("请填写ip地址和名称");
        }

        EmergencyServerExample isServerNameExist = new EmergencyServerExample();
        isServerNameExist.createCriteria()
            .andServerNameEqualTo(server.getServerName())
            .andIsValidEqualTo(ValidEnum.VALID.getValue());
        isServerNameExist.or()
            .andServerIpEqualTo(server.getServerIp())
            .andIsValidEqualTo(ValidEnum.VALID.getValue());
        if (serverMapper.countByExample(isServerNameExist) > 0) {
            return CommonResult.failed("本组或其他组已存在服务器名称或服务器ip相同的主机");
        }

        CommonResult<EmergencyServer> serverResult = generateServer(server);
        if (StringUtils.isNotEmpty(serverResult.getMsg())) {
            return serverResult;
        }
        EmergencyServer insertServer = serverResult.getData();
        insertServer.setCreateUser(server.getCreateUser());
        insertServer.setGroupName(server.getGroupName());
        insertServer.setCreateTime(new Date());
        insertServer.setUpdateUser(server.getCreateUser());
        insertServer.setUpdateTime(insertServer.getCreateTime());
        serverMapper.insertSelective(insertServer);
        insertServer.setPassword(null);
        return CommonResult.success(insertServer);
    }

    @Override
    public CommonResult delete(EmergencyServer server) {
        if (server.getServerId() == null) {
            return CommonResult.failed("请选择正确的主机信息");
        }
        EmergencyServer updateServer = new EmergencyServer();
        updateServer.setServerId(server.getServerId());
        updateServer.setIsValid(ValidEnum.IN_VALID.getValue());
        updateServer.setUpdateUser(server.getUpdateUser());
        updateServer.setUpdateTime(new Date());
        serverMapper.updateByPrimaryKeySelective(updateServer);
        return CommonResult.success();
    }

    @Override
    public CommonResult update(EmergencyServer server) {
        if (server.getServerId() == null) {
            return CommonResult.failed("请选择服务器");
        }
        CommonResult<EmergencyServer> serverResult = generateServer(server);
        if (StringUtils.isNotEmpty(serverResult.getMsg())) {
            return serverResult;
        }
        EmergencyServer updateServer = serverResult.getData();
        updateServer.setUpdateUser(server.getUpdateUser());
        updateServer.setUpdateTime(new Date());
        serverMapper.updateByPrimaryKeySelective(updateServer);
        return CommonResult.success();
    }

    @Override
    public CommonResult allServerUser(String serverIp) {
        return CommonResult.success(new String[]{"root", "guest"});
    }

    @Override
    public CommonResult queryServerInfo(String groupName, CommonPage<EmergencyServer> params, String keyword,
        int[] excludeServerIds, int[] includeAgentIds, String agentType) {
        Page<ServerAgentInfoDTO> pageInfo = PageHelper
            .startPage(params.getPageIndex(), params.getPageSize(), StringUtils.isEmpty(params.getSortType()) ? ""
                : params.getSortField() + System.lineSeparator() + params.getSortType())
            .doSelectPage(() -> {
                serverMapper.selectByKeyword(groupName, params.getObject(), keyword, excludeServerIds,
                    includeAgentIds, agentType);
            });
        List<ServerAgentInfoDTO> result = pageInfo.getResult();
        return CommonResult.success(result, (int) pageInfo.getTotal());
    }

    @Override
    public CommonResult search(String groupName, String serverName) {
        EmergencyServer server = new EmergencyServer();
        server.setServerName(serverName);
        Object[] objects = serverMapper.selectByKeyword(groupName, server, null, null, null, "").stream()
            .map(EmergencyServer::getServerName)
            .toArray();
        return CommonResult.success(objects, objects.length);
    }

    @Override
    public CommonResult license(EmergencyServer server) {
        if (1 == 1) {
            return CommonResult.failed("许可修改暂未开放");
        }
        if (server.getServerId() == null || StringUtils.isEmpty(server.getLicensed())) {
            return CommonResult.failed("请选择主机和许可类型");
        }

        EmergencyServerExample isAgentOnline = new EmergencyServerExample();
        isAgentOnline.createCriteria()
            .andIsValidEqualTo(ValidEnum.VALID.getValue())
            .andAgentPortIsNotNull()
            .andServerIdEqualTo(server.getServerId());
        if (serverMapper.countByExample(isAgentOnline) == 0) {
            return CommonResult.failed("机器尚未安装agent,无需操作许可");
        }

        EmergencyServer updateServer = new EmergencyServer();
        updateServer.setServerId(server.getServerId());
        updateServer.setLicensed(!Boolean.parseBoolean(server.getLicensed()) ? "1" : "0");
        serverMapper.updateByPrimaryKeySelective(updateServer);
        return CommonResult.success();
    }

    @Override
    public CommonResult deleteServerList(String[] serverIds, String userName) {
        try {
            List<Integer> serverIdList = Arrays.stream(serverIds).map(Integer::parseInt).collect(Collectors.toList());
            EmergencyServerExample updateCondition = new EmergencyServerExample();
            updateCondition.createCriteria()
                .andServerIdIn(serverIdList);
            EmergencyServer updateServer = new EmergencyServer();
            updateServer.setIsValid(ValidEnum.IN_VALID.getValue());
            updateServer.setUpdateUser(userName);
            updateServer.setUpdateTime(new Date());
            serverMapper.updateByExampleSelective(updateServer, updateCondition);
        } catch (NumberFormatException e) {
            LOGGER.error("cast string to serverId error.", e);
            return CommonResult.failed("请选择正确的主机信息");
        }
        return CommonResult.success();
    }

    @Override
    public CommonResult install(List<Integer> serverIds) {
        if (serverIds == null || serverIds.size() == 0) {
            return CommonResult.success();
        }
        List<EmergencyServer> allServers = new ArrayList<>();
        for (Integer serverId : serverIds) {
            EmergencyServer server = serverMapper.selectByPrimaryKey(serverId);
            if (server == null) {
                return CommonResult.failed("选择正确的服务器。");
            }
            allServers.add(server);
        }
        allServers.forEach(server -> executor.submit(() -> {
            LOGGER.info("begin sending agent to serverId={},ip={}", server.getServerId(), server.getServerIp());
            CommonResult result = sendAgentToServer(server);
            if (result.isSuccess()) {
                LOGGER.info("success send agent to serverId={},ip={}", server.getServerId(), server.getServerIp());
            } else {
                LOGGER.error("error to send agent to serverId={},ip={}.{}", server.getServerId(), server.getServerIp(),
                    result.getMsg());
            }
        }));
        return CommonResult.success();
    }

    @Override
    public CommonResult saveAgentConfig(EmergencyAgentConfig config) {
        if (config == null || config.getAgentId() == null) {
            return CommonResult.failed("请选择agent");
        }
        EmergencyAgentConfig agentConfig = new EmergencyAgentConfig();
        agentConfig.setAgentId(config.getAgentId());
        agentConfig.setAgentConfig(config.getAgentConfig());
        if (agentConfigMapper.updateByPrimaryKey(agentConfig) == 0) { //
            agentConfigMapper.insertSelective(agentConfig);
        }
        agentManagerService.updateConfig(config.getAgentId().longValue(),
            JSONObject.parseObject(config.getAgentConfig(), Properties.class));
        return CommonResult.success();
    }

    @Override
    public CommonResult queryAgentConfig(int agentId) {
        return CommonResult.success(agentConfigMapper.selectByPrimaryKey(agentId));
    }

    @Override
    public CommonResult getActiveAgent(CommonPage params, String agentType, int[] excludeAgentIds, String agentName) {
        List<ServerAgentInfoDTO> agentList = new ArrayList<>();
        if ("normal".equals(agentType)) {
            agentList = getActiveShellAgent(agentName);
        }
        if ("gui".equals(agentType)) {
            agentList = getActiveGrinderAgent(agentName);
        }
        return CommonResult.success(
            EmergencyServerServiceImpl.rowBounds(params.getPageIndex(), params.getPageSize(), agentList),
            agentList.size());
    }

    private List<ServerAgentInfoDTO> getActiveGrinderAgent(String agentName) {
        List<AgentInfo> allActive = agentManagerService.getAllActive();
        return allActive.stream().
            filter(agent -> {
                if (StringUtils.isNotEmpty(agentName)) {
                    if (!agent.getName().contains(agentName)) {
                        return false;
                    }
                }
                return true;
            }).
            map(agent -> {
                ServerAgentInfoDTO agentInfo = new ServerAgentInfoDTO();
                agentInfo.setAgentId(agent.getId().intValue());
                agentInfo.setAgentName(agent.getName());
                agentInfo.setAgentIp(agent.getIp());
                agentInfo.setServerIp(agent.getIp());
                agentInfo.setAgentType("gui");
                return agentInfo;
            }).
            collect(Collectors.toList());
    }

    private List<ServerAgentInfoDTO> getActiveShellAgent(String agentName) {
        EmergencyAgentExample agentExample = new EmergencyAgentExample();
        Criteria criteria = agentExample.createCriteria()
            .andIsValidEqualTo(ValidEnum.VALID.getValue())
            .andAgentPortIsNotNull()
            .andAgentStatusNotEqualTo(AgentStatusEnum.INACTIVE.getValue());
        if (StringUtils.isNotEmpty(agentName)) {
            criteria.andAgentNameLike(agentName);
        }
        return agentMapper.selectByExample(agentExample).stream()
            .map(agent -> {
                ServerAgentInfoDTO agentInfo = new ServerAgentInfoDTO();
                agentInfo.setAgentId(agent.getAgentId());
                agentInfo.setAgentName(agent.getAgentName());
                agentInfo.setAgentIp(agent.getAgentIp());
                agentInfo.setServerIp(agent.getAgentIp());
                agentInfo.setAgentType("normal");
                return agentInfo;
            }).collect(Collectors.toList());
    }

    public CommonResult sendAgentToServer(EmergencyServer server) {
        if (server.getServerId() == null) {
            return CommonResult.failed("请选择主机");
        }
        EmergencyServer remoteServer = serverMapper.selectByPrimaryKey(server.getServerId());
        if (remoteServer == null) {
            return CommonResult.failed("请选择正确的主机");
        }
        ServerInfo serverInfo = new ServerInfo(remoteServer.getServerIp(), remoteServer.getServerUser());
        if ("1".equals(server.getHavePassword())) {
            try {
                serverInfo.setServerPassword(parsePassword(server));
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Failed to parse password, {}", e.getMessage());
                return CommonResult.failed("获取服务器信息失败");
            }
        }
        File agentPackage = packageService.createAgentPackage();
        Session session = null;
        try {
            session = sessionFactory.getSession(serverInfo);
            ExecResult execResult = remoteExecutor.uploadFile(session, uploadPath, agentPackage);
            if (!execResult.isSuccess()) {
                return CommonResult.failed("上传agent失败");
            }
            String tarCommand = String.format(Locale.ROOT, "cd %s && tar -xf %s", uploadPath, agentPackage.getName());
            execResult = remoteExecutor.exec(session, tarCommand, null, -1);
            if (!execResult.isSuccess()) {
                LOGGER.error("release agent failed. {}", execResult.getMsg());
                return CommonResult.failed("解压agent失败");
            }
            String agentHome = uploadPath + "ngrinder-agent";
            String createStartCommand = String.format(Locale.ROOT, "cd %s && echo \" sh ./run_agent_bg.sh -ah %s -ch "
                    + "%s -cp %s -r NONE \" > start.sh && chmod 777 start.sh", agentHome,
                agentHome, InetAddress.getLocalHost().getHostAddress(), config.getControllerPort());
            execResult = remoteExecutor.exec(session, createStartCommand, null, -1);
            if (!execResult.isSuccess()) {
                LOGGER.error("init agent failed. {}", execResult.getMsg());
                return CommonResult.failed("初始化agent失败");
            }
            String startCommand = String.format(Locale.ROOT, "source /etc/profile && cd %s && ./start.sh", agentHome);
            execResult = remoteExecutor.exec(session, startCommand, null, -1);
            if (!execResult.isSuccess()) {
                LOGGER.error("start agent failed. {}", execResult.getMsg());
                return CommonResult.failed("启动agent失败");
            }
        } catch (JSchException e) {
            LOGGER.error("Failed to connect  ip={}, {}", serverInfo.getServerIp(), e.getMessage());
            return CommonResult.failed("连接服务器失败");
        } catch (SftpException | IOException e) {
            LOGGER.error("Failed to operate ip={}, {}", serverInfo.getServerIp(), e.getMessage());
            return CommonResult.failed("操作远程服务器失败");
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
        WebSocketServer.sendMessage("/host/" + server.getServerId());
        return CommonResult.success();
    }

    public CommonResult<EmergencyServer> generateServer(EmergencyServer source) {
        EmergencyServer newServer = new EmergencyServer();
        newServer.setServerUser(source.getServerUser());
        newServer.setServerIp(source.getServerIp());
        newServer.setServerPort(source.getServerPort());
        newServer.setHavePassword(source.getHavePassword());
        newServer.setPasswordMode(source.getPasswordMode());
        newServer.setServerMemory(source.getServerMemory());
        newServer.setServerName(source.getServerName());
        if ("1".equals(source.getHavePassword())) {
            try {
                newServer.setPassword(passwordUtil.encodePassword(
                    "0".equals(source.getPasswordMode())
                        ? source.getPassword()
                        : getPassword(newServer.getServerIp(), newServer.getServerUser())
                ));
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("encode password error.", e);
                return CommonResult.failed("密码加密失败");
            }
        }
        if ("1".equals(source.getPasswordMode())) {
            newServer.setServerUser(source.getPasswordUri());
            newServer.setPasswordUri(source.getPasswordUri());
        }
        return CommonResult.success(newServer);
    }

    public String getPassword(String serverIp, String serverUser) {
        return "123456";
    }

    public String parsePassword(EmergencyServer server) throws UnsupportedEncodingException {
        return passwordUtil.decodePassword(
            "0".equals(server.getPasswordMode())
                ? server.getPassword()
                : getPassword(server.getServerIp(), server.getServerUser()));
    }

    public static List rowBounds(int pageNum, int pageSize, List list) {
        int startRow = 0;
        int endRow = 0;
        if (list == null || list.size() == 0) {
            return list;
        }
        int totalCount = list.size();
        startRow = pageNum > 0 ? pageNum * pageSize : 0;
        endRow = startRow + pageSize;
        endRow = Math.min(endRow, totalCount);
        while (startRow > endRow) {
            startRow -= pageSize;
        }
        return list.subList(startRow, endRow);
    }
}
