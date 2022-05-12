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

package com.huawei.common.ws;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * websocket管理类
 *
 * @author h30009881
 * @since 2021-10-30
 */
@Slf4j
@ServerEndpoint("/ws")
@Component
public class WebSocketServer {
    private static final CopyOnWriteArraySet<WebSocketServer> WEB_SOCKET_SET = new CopyOnWriteArraySet<>();

    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        WEB_SOCKET_SET.add(this);
        log.info("new session add. ");
    }

    @OnClose
    public void onClose() {
        WEB_SOCKET_SET.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("Exception occurs. Exception info:{}", error.getMessage());
    }

    public static void sendMessage(String message) {
        for (WebSocketServer item : WEB_SOCKET_SET) {
            try {
                log.info("send msg");
                item.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("Exception occurs. Exception info:{}", e.getMessage());
            }
        }
    }
}
