/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */
package xyz.yanghaoyu.flora.rpc.client.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.exception.RpcClientException;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcMessage;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequest;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponse;
import xyz.yanghaoyu.flora.rpc.base.transport.handler.RpcResponseHandler;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageDecoder;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageEncoder;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private final ServiceDiscovery                            discovery;
    private final Bootstrap                                   bootstrap;
    private final NioEventLoopGroup                           group;
    private final Map<String, CompletableFuture<RpcResponse>> waitingRequests
            = new ConcurrentHashMap<>();

    public RpcClient(ServiceDiscovery serviceDiscovery) {
        discovery = serviceDiscovery;
        group = new NioEventLoopGroup();
        bootstrap = buildBootstrap(group);
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    public CompletableFuture<RpcResponse> send(RpcRequest request) {
        InetSocketAddress serviceAddress = discoverService(request);
        Channel           channel        = connectService(serviceAddress);

        // 返回 CompletableFuture， 让调用线程去阻塞，提升客户端的吞吐量
        return doSend(request.getId(), request, channel);
    }

    private CompletableFuture<RpcResponse> doSend(String requestId, RpcRequest request, Channel channel) {

        CompletableFuture<RpcResponse> promise = new CompletableFuture<>();
        waitingRequests.put(requestId, promise);

        RpcMessage message = buildMessage(request);
        channel.writeAndFlush(message).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("request service [{}]", request.getServiceConfig());
                return;
            }

            future.channel().close();
            promise.completeExceptionally(future.cause());
            logger.error("client fail to send. cause:", future.cause());
        });
        return promise;
    }

    private RpcMessage buildMessage(RpcRequest request) {
        RpcMessage message = new RpcMessage();
        message.setData(request);
        message.setCodec((byte) 0);
        message.setCompress((byte) 0);
        message.setMessageType(RpcMessage.REQUEST_MESSAGE_TYPE);
        return message;
    }

    private Channel connectService(InetSocketAddress serviceAddress) {
        try {
            return bootstrap.connect(serviceAddress).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RpcClientException("fail to connect " + serviceAddress);
        }
    }

    private InetSocketAddress discoverService(RpcRequest request) {
        return discovery.discover(request.getServiceConfig());
    }

    private Bootstrap buildBootstrap(NioEventLoopGroup group) {
        return new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new MessageEncoder());
                        pipeline.addLast(new MessageDecoder());
                        pipeline.addLast(new RpcResponseHandler(waitingRequests));
                    }
                });
    }

    public void close() {
        logger.info("rpc client close");
        group.shutdownGracefully();
    }
}