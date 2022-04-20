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

package com.huawei.heartbeat.handler;

import com.huawei.agent.util.RemoteServerCacheUtil;
import com.huawei.heartbeat.client.HeartbeatClient;
import com.huawei.heartbeat.entity.Message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 心跳处理
 *
 * @author h3009881
 * @since 2021-12-17
 **/
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<Message.HeartbeatMessage> {
    private HeartbeatClient client;

    public ClientHandler(HeartbeatClient heartbeatClient) {
        this.client = heartbeatClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message.HeartbeatMessage msg) throws Exception {
        int type = msg.getMessageTypeValue();
        switch (type) {
            case Message.HeartbeatMessage.MessageType.HEARTBEAT_PONG_VALUE:
                log.debug("Client received pong message from server.");
                break;
            case Message.HeartbeatMessage.MessageType.REGISTER_VALUE:
                InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
                String address = inetSocketAddress.getAddress().getHostAddress();
                String port = msg.getRegister().toStringUtf8();
                RemoteServerCacheUtil.addServerInfo(address, port);
                break;
        }
    }

    /**
     * 发送PING心跳
     *
     * @param ctx 上下文对象
     */
    protected void sendPingMsg(ChannelHandlerContext ctx) {
        Message.HeartbeatMessage msg = Message.HeartbeatMessage.newBuilder()
            .setMessageType(Message.HeartbeatMessage.MessageType.HEARTBEAT_PING)
            .setHeartBeat(Message.HeartBeat.newBuilder().build())
            .build();
        Channel channel = ctx.channel();
        channel.writeAndFlush(msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent stateEvent = (IdleStateEvent) evt;
        switch (stateEvent.state()) {
            case READER_IDLE:
                handlerReaderIdle(ctx);
                break;
            case WRITER_IDLE:
                handlerWriterIdle(ctx);
                break;
            default:
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Exception occurs. Exception info {}", cause.getMessage());
        RemoteServerCacheUtil.clean();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Client exit, will reconnect...");
        RemoteServerCacheUtil.clean();
        client.doConnect();
    }

    /**
     * 超过时间未读到数据触发方法
     *
     * @param ctx 上下文对象
     */
    private void handlerReaderIdle(ChannelHandlerContext ctx) {
        log.warn("No response from the server is received for more than one minute. ");
    }

    /**
     * 超过时间未写出数据触发方法
     *
     * @param ctx 上下文对象
     */
    private void handlerWriterIdle(ChannelHandlerContext ctx) {
        sendPingMsg(ctx);
    }
}
