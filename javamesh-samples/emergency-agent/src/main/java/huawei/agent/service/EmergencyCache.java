package com.huawei.agent.service;

import com.huawei.agent.entity.RemoteServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface EmergencyCache {
    Map<Integer,String> cache = new HashMap<>();
    Map<Integer, String> requestCache = new HashMap<>();
    List<RemoteServer> serverInfo = new ArrayList<>();
}
