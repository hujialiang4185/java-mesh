package com.huawei.agent.util;

import com.huawei.agent.entity.RemoteServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RemoteServerCacheUtil {
    public static final List<RemoteServer> serverInfo = new ArrayList<>();

    public static void addServerInfo(String host,String port){
        RemoteServer remoteServer = new RemoteServer(host, port);
        serverInfo.add(remoteServer);
    }

    public static String getServerInfo(){
        RemoteServer remoteServer = serverInfo.get(0);
        return String.format(Locale.ROOT,"http://%s:%s",remoteServer.getHost(),remoteServer.getPort());
    }

    public static void clean(){
        serverInfo.clear();
    }
}
