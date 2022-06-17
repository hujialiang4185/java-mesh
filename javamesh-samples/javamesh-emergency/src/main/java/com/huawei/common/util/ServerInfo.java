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

package com.huawei.common.util;

import lombok.Data;

import org.apache.commons.lang.StringUtils;

import java.util.Objects;

/**
 * 远程执行时所需要的服务器信息
 *
 * @author y30010171
 * @since 2021-10-20
 **/
@Data
public class ServerInfo {
    private static final String DEFAULT_USER = "root";
    private static final int DEFAULT_PORT = 22;
    private String serverIp;
    private String serverUser;
    private String serverPassword;
    private int serverPort;

    public ServerInfo(String serverIp, String serverUser) {
        this(serverIp, serverUser, "", DEFAULT_PORT);
    }

    public ServerInfo(String serverIp, int serverPort) {
        this(serverIp, DEFAULT_USER, "", serverPort);
    }

    public ServerInfo(String serverIp, String serverUser, int serverPort) {
        this(serverIp, serverUser, "", serverPort);
    }

    public ServerInfo(String serverIp, String serverUser, String serverPassword, int serverPort) {
        this.serverIp = serverIp;
        if (StringUtils.isEmpty(serverUser)) {
            this.serverUser = DEFAULT_USER;
        } else {
            this.serverUser = serverUser;
        }
        this.serverPassword = serverPassword;
        this.serverPort = serverPort;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ServerInfo that = (ServerInfo) object;
        return serverPort == that.serverPort && serverIp.equals(that.serverIp) && serverUser.equals(that.serverUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverIp, serverUser, serverPort);
    }
}
