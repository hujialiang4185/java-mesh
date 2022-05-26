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

package com.huawei.heartbeat.server;

import com.huawei.emergency.service.EmergencyAgentService;
import com.huawei.heartbeat.entity.Message;
import com.huawei.heartbeat.handler.ServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 心跳服务端
 *
 * @author h3009881
 * @since 2021-12-17
 **/
@Slf4j
@Component
public class HeartbeatServer {
    // 最大的连接等待数量
    private static final int CONNECTION_SIZE = 1024;

    // 读等待时间
    @Value("${heartbeat.readWaitTime}")
    private int readWaitTime;

    // 网关端口
    @Value("${heartbeat.port}")
    private int port;

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private EmergencyAgentService service;

    @PostConstruct
    public void start() {
        log.info("Starting heartbeat server...");

        // 处理连接的线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        // 处理数据的线程组
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, CONNECTION_SIZE)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) {
                        ChannelPipeline pipeline = channel.pipeline();

                        // 如果超过读等待时间还是没有收到对应客户端，触发读等待事件
                        pipeline.addLast(new IdleStateHandler(readWaitTime, 0, 0));
                        pipeline.addLast(new ProtobufDecoder(Message.HeartbeatMessage.getDefaultInstance()));
                        pipeline.addLast(new ProtobufEncoder());
                        pipeline.addLast(new ServerHandler(service, serverPort));
                    }
                });

            // 同步阻塞等待服务启动
            serverBootstrap.bind(port).sync();
            log.info("Heartbeat server start. ");
        } catch (InterruptedException e) {
            log.error("Exception occurs when start heartbeat server, exception message : {}", e);
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
