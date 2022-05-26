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
import xyz.yanghaoyu.flora.rpc.base.config.ClientConfig;
import xyz.yanghaoyu.flora.rpc.base.exception.RpcClientException;
import xyz.yanghaoyu.flora.rpc.base.transport.RpcRequestConfig;
import xyz.yanghaoyu.flora.rpc.base.transport.RpcRequestHandler;
import xyz.yanghaoyu.flora.rpc.base.transport.RpcResponseConfig;
import xyz.yanghaoyu.flora.rpc.base.transport.RpcResponseHandler;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcMessage;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestBody;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseBody;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageDecoder;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageEncoder;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class RpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private final ClientConfig      config;
    private final InetSocketAddress localhost;
    private final RpcRequestHandler requestHandler;
    private final NioEventLoopGroup group     = new NioEventLoopGroup();
    private final Bootstrap         bootstrap = buildBootstrap(group);
    private final MessageEncoder    encoder;

    private final Map<String, CompletableFuture<RpcResponseBody>> waitingRequests = new ConcurrentHashMap<>();

    public RpcClient(ClientConfig config, InetSocketAddress localServerAddress, RpcRequestHandler requestHandler) {
        this.config = config;
        this.localhost = localServerAddress;
        this.requestHandler = requestHandler;
        encoder = new MessageEncoder(
                config.serializerFactory(),
                config.defaultSerializer(),
                config.compressorFactory(),
                config.defaultCompressor()
        );
    }

    public void close() {
        LOGGER.info("rpc client close");
        group.shutdownGracefully();
    }

    public CompletableFuture<RpcResponseBody> send(RpcRequestConfig requestConfig, InetSocketAddress target) {
        if (target == null) {
            throw new NullPointerException("the target address is null. method: " + requestConfig.getMethodName());
        }

        // 服务在本地 直接在本地处理 减少网络开销
        if (requestHandler != null && target.equals(localhost)) {
            return handleRequestLocally(requestConfig);
        }

        // 连接到服务所在到服务器
        Channel channel = connectService(target);

        // 返回 CompletableFuture， 让调用线程去阻塞，提升客户端的吞吐量
        return doSend(requestConfig, channel);
    }

    private CompletableFuture<RpcResponseBody> handleRequestLocally(RpcRequestConfig requestConfig) {
        RpcRequestBody    requestBody    = buildRpcRequestBody(requestConfig);
        RpcResponseConfig responseConfig = requestHandler.handleRequestBody(requestBody);
        RpcResponseBody   responseBody   = requestHandler.buildResponseBody(requestConfig.getId(), responseConfig);

        CompletableFuture<RpcResponseBody> promise = new CompletableFuture<>();
        promise.complete(responseBody);
        return promise;
    }

    private CompletableFuture<RpcResponseBody> doSend(RpcRequestConfig requestConfig, Channel channel) {
        // 返回一个 promise 用户线程从这个 promise 中拿到服务器返回的结果
        CompletableFuture<RpcResponseBody> promise = new CompletableFuture<>();
        waitingRequests.put(requestConfig.getId(), promise);

        RpcMessage message = buildMessage(requestConfig);
        channel.writeAndFlush(message).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                LOGGER.info("request service [{}]", requestConfig.getServiceReferenceAttribute());
                return;
            }

            future.channel().close();
            promise.completeExceptionally(future.cause());
            LOGGER.error("client fail to send. cause:", future.cause());
        });
        return promise;
    }

    private RpcMessage buildMessage(RpcRequestConfig requestConfig) {
        RpcRequestBody requestBody = buildRpcRequestBody(requestConfig);
        RpcMessage     message     = new RpcMessage();
        message.setBody(requestBody);
        message.setSerializer(requestConfig.getSerializer());
        message.setCompressor(requestConfig.getCompressor());
        message.setType(RpcMessage.REQUEST_MESSAGE_TYPE);
        // todo set id
        // message.setId();

        return message;
    }

    private RpcRequestBody buildRpcRequestBody(RpcRequestConfig requestConfig) {
        RpcRequestBody requestBody = new RpcRequestBody();
        requestBody.setMethodName(requestConfig.getMethodName());
        requestBody.setId(requestConfig.getId());
        requestBody.setParamTypes(requestConfig.getParamTypes());
        requestBody.setServiceName(requestConfig.getServiceReferenceAttribute().getServiceName());
        requestBody.setParams(requestConfig.getParams());
        return requestBody;
    }

    private Channel connectService(InetSocketAddress address) {
        try {
            return bootstrap.connect(address).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RpcClientException("fail to connect " + address);
        }
    }

    private Bootstrap buildBootstrap(NioEventLoopGroup group) {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
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
                        pipeline.addLast(encoder);
                        pipeline.addLast(
                                new MessageDecoder(
                                        config.serializerFactory(),
                                        config.compressorFactory()
                                )
                        );
                        pipeline.addLast(new RpcResponseHandler(waitingRequests));
                    }
                });
    }

}