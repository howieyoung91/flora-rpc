/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import xyz.yanghaoyu.flora.rpc.config.Configuration;
import xyz.yanghaoyu.flora.rpc.transport.handler.MessageDecoder;
import xyz.yanghaoyu.flora.rpc.transport.handler.MessageEncoder;
import xyz.yanghaoyu.flora.rpc.transport.handler.ServerHandler;

import java.util.concurrent.TimeUnit;


public class Server {
    private static final int DEFAULT_PORT = 1913;

    private final Configuration configuration;
    private final int           port;


    public Server(Configuration configuration) {
        this.configuration = configuration;
        Integer port = configuration.serverPort();
        if (port == null) {
            port = DEFAULT_PORT;
        }
        this.port = port;
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

                        pipeline.addLast(new MessageEncoder()); // out
                        pipeline.addLast(new MessageDecoder()); // in
                        pipeline.addLast(new ServerHandler());  // in
                    }
                });

        try {
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
