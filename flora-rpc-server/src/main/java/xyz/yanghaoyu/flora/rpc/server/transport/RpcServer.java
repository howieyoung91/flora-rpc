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
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.server.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.server.config.Service;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageDecoder;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageEncoder;
import xyz.yanghaoyu.flora.rpc.server.config.ServerConfig;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class RpcServer {
    private final ServerConfig      serverConfig;
    private final ServiceRegistry   registry;
    private final ServiceHandler    serviceHandler;
    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    private final NioEventLoopGroup workGroup = new NioEventLoopGroup();

    public RpcServer(ServerConfig serverConfig, ServiceRegistry registry, ServiceHandler serviceHandler) {
        this.serverConfig = serverConfig;
        this.registry = registry;
        this.serviceHandler = serviceHandler;
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));

        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .group(bossGroup, workGroup)
                .option(ChannelOption.SO_BACKLOG, 256)
                .option(ChannelOption.SO_KEEPALIVE, true) // 开启心跳检测
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new IdleStateHandler(
                                30, 0, 0, TimeUnit.SECONDS)
                        );
                        pipeline.addLast(new MessageEncoder(serverConfig.serializerFactory(), serverConfig.defaultSerializer()));
                        pipeline.addLast(new MessageDecoder(serverConfig.serializerFactory()));
                        // todo 可以调用服务交给另一个线程完成
                        pipeline.addLast(new RpcRequestHandler(serviceHandler));
                    }
                });

        try {
            ChannelFuture future = serverBootstrap.bind(serverConfig.port());
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void publishService(Service service) {
        try {
            registry.register(new InetSocketAddress(Inet4Address.getLocalHost().getHostAddress(), serverConfig.port()), service);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
