/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.transport.support;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.config.ServerConfig;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.base.transport.DefaultRpcRequestHandler;
import xyz.yanghaoyu.flora.rpc.base.transport.interceptor.ServiceInterceptor;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageDecoder;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageEncoder;
import xyz.yanghaoyu.flora.rpc.server.transport.ConfigurableRpcServer;

import java.util.TreeSet;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractConfigurableRpcServer implements ConfigurableRpcServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfigurableRpcServer.class);

    private final    ServerConfig                config;
    private final    ServiceHandler              handler;
    private final    MessageEncoder              encoder;
    private          TreeSet<ServiceInterceptor> interceptors;
    private          NioEventLoopGroup           bosses;
    private          NioEventLoopGroup           works;
    private          DefaultEventLoopGroup       handleRequestGroup;
    private volatile DefaultRpcRequestHandler    requestHandler;

    private          AtomicBoolean started     = new AtomicBoolean(false);
    private volatile boolean       initialized = false;

    public AbstractConfigurableRpcServer(ServerConfig config, ServiceHandler handler) {
        this.config = config;
        this.handler = handler;
        this.encoder = new MessageEncoder(
                config.serializerFactory(), config.defaultSerializer(),
                config.compressorFactory(), config.defaultCompressor());
    }

    @Override
    public final void start() {
        if (!canStart()) {
            LOGGER.warn("Fail to start rpc server. Cause: rpc server has already been started!");
            return;
        }
        initializeIfNecessary();
        ServerBootstrap bootstrap = buildBootstrap();
        doStart(bootstrap);
        LOGGER.warn("rpc server started");
    }

    private boolean canStart() {
        // 服务器没有开启并且 cas 成功才能让该线程开启服务器
        return !started.get() && started.compareAndSet(false, true);
    }

    private ServerBootstrap buildBootstrap() {
        return new ServerBootstrap().group(bosses, works)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 256)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                        pipeline.addLast(encoder);
                        pipeline.addLast(new MessageDecoder(config.serializerFactory(), config.compressorFactory()));
                        pipeline.addLast(handleRequestGroup, requestHandler);
                    }
                });
    }

    private void doStart(ServerBootstrap bootstrap) {
        try {
            beforeStart(bootstrap);
            ChannelFuture future = bootstrap.bind(config.port()).sync();
            future.addListener(this::afterStart);
            future.channel().closeFuture().sync();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            close();
        }
    }

    @Override
    public final void close() {
        if (canClose()) {
            beforeClose();
            bosses.shutdownGracefully();
            works.shutdownGracefully();
            handleRequestGroup.shutdownGracefully();
            started.getAndSet(false);
            afterClose();
            LOGGER.warn("rpc server closed");
        }
    }

    private boolean canClose() {
        return started.get() && started.compareAndSet(true, false);
    }

    private void initializeIfNecessary() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    doInitialize();
                }
            }
        }
    }

    protected void doInitialize() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
        bosses = new NioEventLoopGroup();
        works = new NioEventLoopGroup();
        handleRequestGroup = new DefaultEventLoopGroup();
        initialized = true;
    }

    public DefaultRpcRequestHandler getRequestHandler() {
        if (requestHandler == null) {
            synchronized (this) {
                if (requestHandler == null) {
                    requestHandler = new DefaultRpcRequestHandler(handler, interceptors);
                }
            }
        }
        return requestHandler;
    }

    void setInterceptors(TreeSet<ServiceInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    @Override
    public ServerConfig getConfiguration() {
        return config;
    }

    protected void beforeStart(ServerBootstrap bootstrap) {
    }

    protected void afterStart(Future<? super Void> future) {
    }

    protected void beforeClose() {
    }

    protected void afterClose() {
    }
}
