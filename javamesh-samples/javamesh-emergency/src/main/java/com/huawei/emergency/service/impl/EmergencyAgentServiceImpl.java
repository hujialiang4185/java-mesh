package com.huawei.emergency.service.impl;

import com.huawei.common.constant.ValidEnum;
import com.huawei.emergency.entity.EmergencyAgent;
import com.huawei.emergency.entity.EmergencyAgentExample;
import com.huawei.emergency.entity.EmergencyServer;
import com.huawei.emergency.entity.EmergencyServerExample;
import com.huawei.emergency.mapper.EmergencyAgentMapper;
import com.huawei.emergency.mapper.EmergencyServerMapper;
import com.huawei.emergency.service.EmergencyAgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class EmergencyAgentServiceImpl implements EmergencyAgentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmergencyAgentServiceImpl.class);

    @Autowired
    private EmergencyServerMapper serverMapper;

    @Override
    public void addAgent(String ip, String port) {
        try {
            EmergencyServerExample serverExample = new EmergencyServerExample();
            serverExample.createCriteria()
                .andServerIpEqualTo(ip);
            List<EmergencyServer> serverList = serverMapper.selectByExample(serverExample);
            if (serverList.size() == 0) {
                EmergencyServer server = new EmergencyServer();
                server.setServerName(ip);
                server.setServerPort(Integer.valueOf(port));
                server.setServerUser("root");
                server.setCreateUser("admin");
                server.setHavePassword("0");
                server.setCreateTime(new Date());
                serverMapper.insert(server);
            }
            serverList.forEach(server -> {
                server.setIsValid(ValidEnum.VALID.getValue());
                server.setAgentPort(Integer.valueOf(port));
                serverMapper.updateByPrimaryKeySelective(server);
            });
        } catch (Exception e) {
            LOGGER.error("add agent error.", e);
        }
    }

    @Override
    public void removeAgent(String ip) {
        try {
            EmergencyServerExample serverExample = new EmergencyServerExample();
            serverExample.createCriteria().andServerIpEqualTo(ip)
                .andAgentPortIsNotNull();
            serverMapper.selectByExample(serverExample).forEach(server -> {
                server.setAgentPort(null);
                serverMapper.updateByPrimaryKey(server);
            });
        } catch (Exception e) {
            LOGGER.error("remove agent error.", e);
        }
    }
}
