package xyz.yanghaoyu.flora.rpc.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import xyz.yanghaoyu.flora.rpc.transport.dto.Request;

public class ServerHandler extends SimpleChannelInboundHandler<Request> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) {
        // todo 在这里调用真实到方法
    }
}
