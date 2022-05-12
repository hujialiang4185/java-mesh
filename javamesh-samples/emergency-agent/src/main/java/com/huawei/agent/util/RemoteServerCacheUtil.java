/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.agent.util;

import com.huawei.agent.entity.RemoteServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 脚本执行时服务器信息缓存
 *
 * @author h3009881
 * @since 2021-12-17
 **/
public class RemoteServerCacheUtil {
    /**
     * 远程服务器缓存
     */
    public static final List<RemoteServer> SERVER_INFO = new ArrayList<>();

    private RemoteServerCacheUtil() {
    }

    public static void addServerInfo(String host, String port) {
        RemoteServer remoteServer = new RemoteServer(host, port);
        SERVER_INFO.add(remoteServer);
    }

    public static String getServerInfo() {
        RemoteServer remoteServer = SERVER_INFO.get(0);
        return String.format(Locale.ROOT, "http://%s:%s", remoteServer.getHost(), remoteServer.getPort());
    }

    public static void clean() {
        SERVER_INFO.clear();
    }
}
