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
import xyz.yanghaoyu.flora.rpc.base.config.ServerConfig;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.base.transport.DefaultRpcRequestHandler;
import xyz.yanghaoyu.flora.rpc.base.transport.interceptor.ServiceInterceptor;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageDecoder;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageEncoder;
import xyz.yanghaoyu.flora.rpc.server.transport.ConfigurableRpcServer;
import xyz.yanghaoyu.flora.rpc.server.transport.ServicePublisher;

import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public abstract class AbstractRpcServer implements ConfigurableRpcServer, ServicePublisher {
    private final    ServerConfig                config;
    private final    ServiceHandler              handler;
    private final    NioEventLoopGroup           bossGroup = new NioEventLoopGroup();
    private final    NioEventLoopGroup           workGroup = new NioEventLoopGroup();
    private final    MessageEncoder              encoder;
    private          TreeSet<ServiceInterceptor> interceptors;
    private volatile DefaultRpcRequestHandler    requestHandler;

    public AbstractRpcServer(ServerConfig config, ServiceHandler handler) {
        this.config = config;
        this.handler = handler;
        this.encoder = new MessageEncoder(
                config.serializerFactory(), config.defaultSerializer(),
                config.compressorFactory(), config.defaultCompressor());
    }

    @Override
    public void start() {
        registerShutdownHook();
        doStart();
    }

    @Override
    public void close() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private void doStart() {
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 256)
                .option(ChannelOption.SO_KEEPALIVE, true) // 开启心跳检测
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                        pipeline.addLast(encoder);
                        pipeline.addLast(
                                new MessageDecoder(
                                        config.serializerFactory(),
                                        config.compressorFactory()
                                )
                        );
                        // todo 可以调用服务交给另一个线程完成
                        pipeline.addLast(requestHandler);
                    }
                });

        try {
            ChannelFuture future = bootstrap.bind(config.port());
            future.channel().closeFuture().sync();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            close();
        }
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
    public ServerConfig getConfig() {
        return config;
    }
}
