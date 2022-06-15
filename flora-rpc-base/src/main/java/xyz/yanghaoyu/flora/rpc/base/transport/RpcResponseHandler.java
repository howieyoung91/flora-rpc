/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.transport;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.compress.support.NoCompressSmartCompressor;
import xyz.yanghaoyu.flora.rpc.base.serialize.support.KryoSmartSerializer;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcMessage;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseBody;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RpcResponseHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RpcResponseHandler.class);

    private final Map<String, CompletableFuture<RpcResponseBody>> waitingRequests;

    public RpcResponseHandler(Map<String, CompletableFuture<RpcResponseBody>> waitingRequests) {
        this.waitingRequests = waitingRequests;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof RpcResponseBody) {
                RpcResponseBody   response = (RpcResponseBody) msg;
                CompletableFuture promise  = waitingRequests.get(response.getRequestId());

                if (promise != null) {
                    promise.complete(response);
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 心跳检测
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            // 长时间未写
            // 发送 ping 包
            if (state == IdleState.WRITER_IDLE) {
                RpcMessage<String> pingMessage = RpcMessage.of(RpcMessage.HEARTBEAT_REQUEST_MESSAGE_TYPE, null);
                pingMessage.setSerializer(KryoSmartSerializer.NAME);
                pingMessage.setCompressor(NoCompressSmartCompressor.NAME);
                Channel channel = ctx.channel();
                if (channel.isWritable()) {
                    ctx.channel().writeAndFlush(pingMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 捕捉异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
