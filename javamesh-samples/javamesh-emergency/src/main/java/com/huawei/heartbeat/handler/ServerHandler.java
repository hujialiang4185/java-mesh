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

package com.huawei.heartbeat.handler;

import com.huawei.emergency.entity.EmergencyAgent;
import com.huawei.emergency.service.EmergencyAgentService;
import com.huawei.heartbeat.entity.Message;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * 心跳处理
 *
 * @author h3009881
 * @since 2021-12-17
 **/
@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<Message.HeartbeatMessage> {
    private EmergencyAgentService service;

    private String serverPort;

    public ServerHandler(EmergencyAgentService service, String serverPort) {
        this.service = service;
        this.serverPort = serverPort;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message.HeartbeatMessage msg) {
        int type = msg.getMessageTypeValue();
        switch (type) {
            case Message.HeartbeatMessage.MessageType.HEARTBEAT_PING_VALUE:
                log.debug("Heartbeat data received from the client");
                sendPongMsg(ctx, msg);
                break;
            case Message.HeartbeatMessage.MessageType.REGISTER_VALUE:
                EmergencyAgent agent = JSONObject.parseObject(msg.getRegister().toStringUtf8(), EmergencyAgent.class);
                agent.setAgentIp(getAddress(ctx));
                service.addAgent(agent);
                Message.HeartbeatMessage message = Message.HeartbeatMessage.newBuilder()
                    .setMessageType(Message.HeartbeatMessage.MessageType.REGISTER)
                    .setRegister(ByteString.copyFrom(serverPort.getBytes(StandardCharsets.UTF_8)))
                    .build();
                ctx.channel().writeAndFlush(message);
                break;
            default:
                break;
        }
    }

    private void sendPongMsg(ChannelHandlerContext ctx, Message.HeartbeatMessage msg) {
        Message.HeartbeatMessage message = msg.newBuilderForType()
            .setMessageType(Message.HeartbeatMessage.MessageType.HEARTBEAT_PONG)
            .setHeartBeat(Message.HeartBeat.newBuilder().build())
            .build();
        Channel channel = ctx.channel();
        channel.writeAndFlush(message);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        IdleStateEvent stateEvent = (IdleStateEvent) evt;
        switch (stateEvent.state()) {
            case READER_IDLE:
                handlerReaderIdle(ctx);
                break;
            default:
                break;
        }
    }

    private void handlerReaderIdle(ChannelHandlerContext ctx) {
        log.info("Client timeOut, close it");
        service.removeAgent(getAddress(ctx));
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("Close channelHandlerContext");
        service.removeAgent(getAddress(ctx));
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception occurs. Exception info: {}", cause);
        service.removeAgent(getAddress(ctx));
        ctx.close();
    }

    private String getAddress(ChannelHandlerContext ctx) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        return inetSocketAddress.getAddress().getHostAddress();
    }
}
