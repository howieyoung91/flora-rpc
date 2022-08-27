/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */
package xyz.yanghaoyu.flora.rpc.client.transport.support;

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
import xyz.yanghaoyu.flora.rpc.base.transport.RpcRequestHandler;
import xyz.yanghaoyu.flora.rpc.base.transport.RpcResponseHandler;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.*;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageDecoder;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageEncoder;
import xyz.yanghaoyu.flora.rpc.base.util.ServiceUtil;
import xyz.yanghaoyu.flora.rpc.client.transport.ConfigurableLocalRequestHandlerRpcClient;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 一个 rpc 客户端的抽象类
 * <p>
 * 主要对 transport 做了支持
 */
public abstract class AbstractRpcClient
        implements ConfigurableLocalRequestHandlerRpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRpcClient.class);

    private final ClientConfig      config;
    private       NioEventLoopGroup group;
    private       Bootstrap         bootstrap;
    private final MessageEncoder    encoder;
    private       boolean           started = false;

    private final Map<String, CompletableFuture<RpcResponseBody>> waitingRequests = new ConcurrentHashMap<>();

    public AbstractRpcClient(ClientConfig config) {
        this.config = config;
        encoder = new MessageEncoder(config.serializerFactory(), config.defaultSerializer(),
                config.compressorFactory(), config.defaultCompressor());
    }

    // ========================================   public methods   =========================================

    @Override
    public synchronized void start() {
        if (started) {
            LOGGER.warn("fail to start rpc client. Cause: rpc client has already been started!");
            return;
        }
        started = true;
        group = new NioEventLoopGroup();
        bootstrap = buildBootstrap();
        LOGGER.info("rpc client started");
    }


    @Override
    public synchronized void close() {
        if (!started) {
            return;
        }
        started = false;
        group.shutdownGracefully();
        LOGGER.info("rpc client closed");
    }

    @Override
    public CompletableFuture<RpcResponseBody> send(RpcRequestConfig requestConfig, InetSocketAddress target) {
        Objects.requireNonNull(target, "fail to send rpc request. Cause: the target address is null. method -> " + requestConfig.getMethodName());

        // 服务在本地 直接在本地处理 减少网络开销
        if (canHandleRequestLocally(target)) {
            return handleRequestLocally(requestConfig);
        }
        // 连接到服务所在到服务器
        Channel channel = connectService(target);
        // 返回 CompletableFuture， 让调用线程去阻塞，提升客户端的吞吐量
        return doSend(requestConfig, channel);
    }

    @Override
    public RpcResponseConfig handleRequest(RpcRequestBody requestBody) {
        // 全部委托给另一个 RpcRequestHandler
        return getLocalRequestHandler().handleRequest(requestBody);
    }

    @Override
    public ClientConfig getConfig() {
        return config;
    }

    // ========================================   public methods   =========================================


    // -----------------------------------------------------------------------------------------------------
    // --------------------------------------    private methods    ----------------------------------------
    // -----------------------------------------------------------------------------------------------------

    private Bootstrap buildBootstrap() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
        return new Bootstrap().group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        pipeline.addLast(encoder);
                        pipeline.addLast(new MessageDecoder(getConfig().serializerFactory(), getConfig().compressorFactory()));
                        pipeline.addLast(new RpcResponseHandler(waitingRequests));
                    }
                });
    }

    private CompletableFuture<RpcResponseBody> doSend(RpcRequestConfig requestConfig, Channel channel) {
        // 返回一个 promise 用户线程从这个 promise 中拿到服务器返回的结果
        CompletableFuture<RpcResponseBody> promise = new CompletableFuture<>();
        waitingRequests.put(requestConfig.getId(), promise);
        // write
        RpcMessage message = ServiceUtil.buildMessage(requestConfig);
        channel.writeAndFlush(message).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                LOGGER.info("requested service [{}]", requestConfig.getServiceReferenceAttribute());
                return;
            }
            // error
            future.channel().close();
            promise.completeExceptionally(future.cause());
            LOGGER.error("rpc client fail to send. Cause:", future.cause());
        });
        return promise;
    }

    private Channel connectService(InetSocketAddress address) {
        try {
            return bootstrap.connect(address).sync().channel();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            throw new RpcClientException("fail to connect " + address);
        }
    }

    protected RpcRequestHandler getLocalRequestHandler() {
        return null;
    }

    protected CompletableFuture<RpcResponseBody> handleRequestLocally(RpcRequestConfig requestConfig) {
        return null;
    }

    protected boolean canHandleRequestLocally(InetSocketAddress target) {
        return false;
    }

    // -----------------------------------------------------------------------------------------------------
    // --------------------------------------    private methods    ----------------------------------------
    // -----------------------------------------------------------------------------------------------------
}