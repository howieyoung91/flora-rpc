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
import xyz.yanghaoyu.flora.rpc.server.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.server.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.server.service.Service;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageDecoder;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageEncoder;
import xyz.yanghaoyu.flora.rpc.server.config.ServerConfig;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public final class RpcServer {
    private final ServerConfig      config;
    private final ServiceRegistry   registry;
    private final ServiceHandler    handler;
    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    private final NioEventLoopGroup workGroup = new NioEventLoopGroup();
    private final MessageEncoder    encoder;

    public RpcServer(ServerConfig serverConfig, ServiceRegistry registry, ServiceHandler serviceHandler) {
        this.config = serverConfig;
        this.registry = registry;
        this.handler = serviceHandler;
        this.encoder = new MessageEncoder(
                serverConfig.serializerFactory(),
                serverConfig.defaultSerializer(),
                serverConfig.compressorFactory(),
                serverConfig.defaultCompressor()
        );
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
                        pipeline.addLast(new RpcRequestHandler(handler));
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
        try {
            registry.register(new InetSocketAddress(Inet4Address.getLocalHost().getHostAddress(), config.port()), service);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
