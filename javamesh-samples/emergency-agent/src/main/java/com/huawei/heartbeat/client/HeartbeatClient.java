/*
 * Copyright (C) Ltd. 2021-2021. Huawei Technologies Co., All rights reserved
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

package com.huawei.heartbeat.client;

import com.huawei.agent.entity.EmergencyAgent;
import com.huawei.heartbeat.entity.Message;
import com.huawei.heartbeat.entity.Message.HeartbeatMessage;
import com.huawei.heartbeat.entity.Message.HeartbeatMessage.MessageType;
import com.huawei.heartbeat.handler.ClientHandler;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

/**
 * 心跳维持客户端
 *
 * @author h3009881
 * @since 2021-12-17
 **/
@Component
@Slf4j
public class HeartbeatClient {
    private Channel channel;

    private Bootstrap bootstrap;

    @Value("${heartbeat.connectTimeoutMillis}")
    private int connectTimeout;

    @Value("${heartbeat.writeIdleSeconds}")
    private int writeIdleTime;

    @Value("${heartbeat.server.ip}")
    private String ip;

    @Value("${heartbeat.server.port}")
    private int port;

    @Value("${heartbeat.reconnectInterval}")
    private int reconnectInterval;

    @Value("${server.port}")
    private int serverPort;

    @Value("${agent.name}")
    private String agentName;

    @PostConstruct
    public void start() {
        EventLoopGroup eventExecutors = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventExecutors)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
            .handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new IdleStateHandler(0, writeIdleTime, 0))
                        .addLast(new ProtobufDecoder(Message.HeartbeatMessage.getDefaultInstance()))
                        .addLast(new ProtobufEncoder())
                        .addLast(new ClientHandler(HeartbeatClient.this));
                }
            });
        doConnect();
    }

    public void doConnect() {
        log.info("Connect to server. ");
        if (channel != null && channel.isActive()) {
            return;
        }
        ChannelFuture connect = bootstrap.connect(ip, port);

        // 添加连接监听
        connect.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                channel = channelFuture.channel();
                EmergencyAgent agent = new EmergencyAgent();
                agent.setAgentName(agentName);
                agent.setAgentPort(serverPort);
                Message.HeartbeatMessage message = HeartbeatMessage.newBuilder()
                    .setMessageType(MessageType.REGISTER)
                    .setRegister(ByteString.copyFrom(JSONObject.toJSONString(agent).getBytes(StandardCharsets.UTF_8)))
                    .build();
                channel.writeAndFlush(message);
            } else {
                // 失败则在X秒后重试连接
                log.info("Failed to connect,try reconnecting after {} seconds...", reconnectInterval);
                channelFuture.channel().eventLoop().schedule(this::doConnect, reconnectInterval, TimeUnit.SECONDS);
            }
        });
    }
}
