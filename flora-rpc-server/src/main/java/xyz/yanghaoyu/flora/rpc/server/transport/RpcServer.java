/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import xyz.yanghaoyu.flora.rpc.base.config.ServerConfig;
import xyz.yanghaoyu.flora.rpc.base.service.Service;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.base.transport.RpcRequestHandler;
import xyz.yanghaoyu.flora.rpc.base.transport.interceptor.ServiceInterceptor;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageDecoder;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageEncoder;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public final class RpcServer {
    private          InetSocketAddress           localhost;
    private final    ServerConfig                config;
    private final    ServiceRegistry             registry;
    private final    ServiceHandler              handler;
    private final    NioEventLoopGroup           bossGroup = new NioEventLoopGroup();
    private final    NioEventLoopGroup           workGroup = new NioEventLoopGroup();
    private final    MessageEncoder              encoder;
    private          TreeSet<ServiceInterceptor> interceptors;
    private volatile RpcRequestHandler           requestHandler;

    public RpcServer(ServerConfig config, ServiceRegistry registry, ServiceHandler handler) {
        this.config = config;
        this.registry = registry;
        this.handler = handler;
        this.encoder = new MessageEncoder(
                config.serializerFactory(),
                config.defaultSerializer(),
                config.compressorFactory(),
                config.defaultCompressor()
        );
        try {
            localhost = new InetSocketAddress(Inet4Address.getLocalHost().getHostAddress(), config.port());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));

        ServerBootstrap bootstrap = new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .group(bossGroup, workGroup)
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }


    public void publishService(Service service) {
        registry.register(localhost, service);
    }

    public RpcRequestHandler getRequestHandler() {
        if (requestHandler == null) {
            synchronized (this) {
                if (requestHandler == null) {
                    requestHandler = new RpcRequestHandler(handler, interceptors);
                }
            }
        }
        return requestHandler;
    }

    void setInterceptors(TreeSet<ServiceInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
