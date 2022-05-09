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
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.base.service.config.Service;
import xyz.yanghaoyu.flora.rpc.base.transport.handler.RpcRequestHandler;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageDecoder;
import xyz.yanghaoyu.flora.rpc.base.transport.protocol.MessageEncoder;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class RpcServer {
    private ServiceRegistry registry;
    private ServiceHandler  serviceHandler;

    // @Value(value = "${flora.rpc.server.port}")
    private int port;

    public void setPort(int port) {
        this.port = port;
    }

    public RpcServer(ServiceRegistry registry, ServiceHandler serviceHandler) {
        this.registry = registry;
        this.serviceHandler = serviceHandler;
    }

    public void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .group(new NioEventLoopGroup(), new NioEventLoopGroup())
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

                        pipeline.addLast(new MessageEncoder());
                        pipeline.addLast(new MessageDecoder());
                        pipeline.addLast(new RpcRequestHandler(serviceHandler));
                    }
                });

        try {
            ChannelFuture future = serverBootstrap.bind(port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void publishService(Service serviceConfig) {
        try {
            registry.register(new InetSocketAddress(Inet4Address.getLocalHost().getHostAddress(), port), serviceConfig);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
