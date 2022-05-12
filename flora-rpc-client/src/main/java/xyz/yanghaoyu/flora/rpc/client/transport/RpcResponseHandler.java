/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
