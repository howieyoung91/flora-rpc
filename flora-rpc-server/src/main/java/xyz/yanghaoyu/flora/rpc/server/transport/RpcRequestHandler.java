/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.transport;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.*;

public class RpcRequestHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);

    private ServiceHandler serviceHandler;

    public RpcRequestHandler(ServiceHandler serviceHandler) {
        this.serviceHandler = serviceHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof RpcRequestBody) {
            RpcRequestBody requestBody = (RpcRequestBody) msg;

            RpcMessage<RpcResponseBody> message = new RpcMessage<>();

            RpcResponseConfig respConfig = serviceHandler.handle(requestBody);

            message.setSerializer(respConfig.getSerializer());
            message.setCompress((byte) 0);
            message.setMessageType(RpcMessage.RESPONSE_MESSAGE_TYPE);

            message.setBody(buildResponseBody(requestBody, respConfig));

            if (!(ctx.channel().isActive() && ctx.channel().isWritable())) {
                RpcResponseBody responseBody = new RpcResponseBody();
                responseBody.setCode(400);
                responseBody.setMessage("channel is not active or channel cannot write");

                respConfig.setBody(responseBody);
            }

            ctx.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
    }


    private RpcResponseBody buildResponseBody(RpcRequestBody requestData, RpcResponseConfig response) {
        RpcResponseBody resp = new RpcResponseBody();
        resp.setMessage("ok");
        resp.setCode(200);
        resp.setRequestId(requestData.getId());
        resp.setData(response.getData());
        return resp;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}
