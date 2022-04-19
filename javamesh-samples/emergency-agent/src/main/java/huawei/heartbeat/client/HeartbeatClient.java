package com.huawei.heartbeat.client;

import com.google.protobuf.ByteString;
import com.huawei.agent.service.EmergencyAgentService;
import com.huawei.heartbeat.entity.Message;
import com.huawei.heartbeat.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

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
    private String serverPort;

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
                Message.HeartbeatMessage message = Message.HeartbeatMessage.newBuilder()
                        .setMessageType(Message.HeartbeatMessage.MessageType.REGISTER)
                        .setRegister(ByteString.copyFrom(serverPort.getBytes(StandardCharsets.UTF_8)))
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
