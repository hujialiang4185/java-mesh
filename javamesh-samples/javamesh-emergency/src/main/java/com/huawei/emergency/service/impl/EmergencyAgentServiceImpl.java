package com.huawei.emergency.service.impl;

import com.huawei.common.constant.ValidEnum;
import com.huawei.emergency.entity.EmergencyAgent;
import com.huawei.emergency.entity.EmergencyAgentExample;
import com.huawei.emergency.entity.EmergencyServerExample;
import com.huawei.emergency.mapper.EmergencyAgentMapper;
import com.huawei.emergency.mapper.EmergencyServerMapper;
import com.huawei.emergency.service.EmergencyAgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmergencyAgentServiceImpl implements EmergencyAgentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmergencyAgentServiceImpl.class);

    @Autowired
    private EmergencyAgentMapper mapper;

    @Autowired
    private EmergencyServerMapper serverMapper;

    @Override
    public void addAgent(String ip, String port) {
        try {
            EmergencyAgent agent = new EmergencyAgent();
            agent.setIp(ip);
            agent.setPort(port);
            agent.setStatus("READY");
            mapper.insert(agent);
        } catch (Exception e) {
            LOGGER.error("add agent error.", e);
        } finally {
            EmergencyServerExample serverExample = new EmergencyServerExample();
            serverExample.createCriteria()
                .andServerIpEqualTo(ip)
                .andIsValidEqualTo(ValidEnum.VALID.getValue());
            serverMapper.selectByExample(serverExample).forEach(server -> {
                server.setAgentPort(Integer.valueOf(port));
                serverMapper.updateByPrimaryKeySelective(server);
            });
        }
    }

    @Override
    public void removeAgent(String ip) {
        try {
            EmergencyAgentExample example = new EmergencyAgentExample();
            example.createCriteria().andIpEqualTo(ip);
            mapper.deleteByExample(example);
        } catch (Exception e) {
            LOGGER.error("remove agent error.", e);
        } finally {
            EmergencyServerExample serverExample = new EmergencyServerExample();
            serverExample.createCriteria().andServerIpEqualTo(ip)
                .andAgentPortIsNotNull()
                .andIsValidEqualTo(ValidEnum.VALID.getValue());
            serverMapper.selectByExample(serverExample).forEach(server -> {
                server.setAgentPort(null);
                serverMapper.updateByPrimaryKey(server);
            });
        }
    }
}
