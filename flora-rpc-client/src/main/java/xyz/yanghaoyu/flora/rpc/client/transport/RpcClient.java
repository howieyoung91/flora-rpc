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
import xyz.yanghaoyu.flora.rpc.client.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceNotFoundException;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcMessage;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestBody;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseBody;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageDecoder;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageEncoder;
import xyz.yanghaoyu.flora.rpc.client.config.ClientConfig;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private final ClientConfig      clientConfig;
    private final ServiceDiscovery  discovery;
    private final NioEventLoopGroup group     = new NioEventLoopGroup();
    private final Bootstrap         bootstrap = buildBootstrap(group);
    private final MessageEncoder    encoder;

    private final Map<String, CompletableFuture<RpcResponseBody>> waitingRequests
            = new ConcurrentHashMap<>();

    public RpcClient(ClientConfig clientConfig, ServiceDiscovery discovery) {
        this.discovery = discovery;
        this.clientConfig = clientConfig;
        encoder = new MessageEncoder(
                clientConfig.serializerFactory(),
                clientConfig.defaultSerializer(),
                clientConfig.compressorFactory(),
                clientConfig.defaultCompressor()
        );
    }

    public CompletableFuture<RpcResponseBody> send(RpcRequestConfig request) throws ServiceNotFoundException {
        InetSocketAddress serviceAddress = discoverService(request);

        // 连接到服务所在到服务器
        Channel channel = connectService(serviceAddress);

        // 返回 CompletableFuture， 让调用线程去阻塞，提升客户端的吞吐量
        return doSend(request.getId(), request, channel);
    }

    public void close() {
        logger.info("rpc client close");
        group.shutdownGracefully();
    }

    private CompletableFuture<RpcResponseBody> doSend(String requestId, RpcRequestConfig reqConfig, Channel channel) {
        // 返回一个 promise 用户线程从这个 promise 中拿到服务器返回的结果
        CompletableFuture<RpcResponseBody> promise = new CompletableFuture<>();
        waitingRequests.put(requestId, promise);

        RpcMessage message = buildMessage(reqConfig);
        channel.writeAndFlush(message).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("request service [{}]", reqConfig.getServiceRefAttr());
                return;
            }

            future.channel().close();
            promise.completeExceptionally(future.cause());
            logger.error("client fail to send. cause:", future.cause());
        });
        return promise;
    }

    private RpcMessage buildMessage(RpcRequestConfig reqAnnConfig) {
        RpcRequestBody reqBody = buildRpcRequestBody(reqAnnConfig);
        RpcMessage     message = new RpcMessage();
        message.setBody(reqBody);
        message.setSerializer(reqAnnConfig.getSerializer());
        message.setCompressor(reqAnnConfig.getCompressor());
        message.setType(RpcMessage.REQUEST_MESSAGE_TYPE);
        // todo set id
        // message.setId();

        return message;
    }

    private RpcRequestBody buildRpcRequestBody(RpcRequestConfig reqConfig) {
        RpcRequestBody reqBody = new RpcRequestBody();
        reqBody.setMethodName(reqConfig.getMethodName());
        reqBody.setId(reqConfig.getId());
        reqBody.setParamTypes(reqConfig.getParamTypes());
        reqBody.setServiceName(reqConfig.getServiceRefAttr().getServiceName());
        reqBody.setParams(reqConfig.getParams());
        return reqBody;
    }

    private Channel connectService(InetSocketAddress serviceAddress) {
        try {
            return bootstrap.connect(serviceAddress).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RpcClientException("fail to connect " + serviceAddress);
        }
    }

    private InetSocketAddress discoverService(RpcRequestConfig reqConfig) throws ServiceNotFoundException {
        return discovery.discover(reqConfig);
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
                                        clientConfig.serializerFactory(),
                                        clientConfig.compressorFactory()
                                )
                        );
                        pipeline.addLast(new RpcResponseHandler(waitingRequests));
                    }
                });
    }

}