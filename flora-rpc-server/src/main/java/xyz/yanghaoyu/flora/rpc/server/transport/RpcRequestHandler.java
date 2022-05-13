/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.transport;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcMessage;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestBody;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseBody;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseConfig;

public class RpcRequestHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);

    private ServiceHandler serviceHandler;

    public RpcRequestHandler(ServiceHandler serviceHandler) {
        this.serviceHandler = serviceHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof RpcRequestBody) {
            RpcRequestBody reqBody = (RpcRequestBody) msg;

            RpcMessage<RpcResponseBody> message = new RpcMessage<>();

            RpcResponseConfig respConfig = serviceHandler.handle(reqBody);

            message.setSerializer(respConfig.getSerializer());
            message.setCompress((byte) 0);
            message.setType(RpcMessage.RESPONSE_MESSAGE_TYPE);

            message.setBody(buildResponseBody(reqBody.getId(), respConfig));

            if (!(ctx.channel().isActive() && ctx.channel().isWritable())) {
                RpcResponseBody respBody = new RpcResponseBody();
                respBody.setCode(400);
                respBody.setMessage("channel is not active or channel cannot write");

                message.setBody(respBody);
                respBody.setRequestId(reqBody.getId());
                logger.error("message dropped. cause: channel is not writable");
            }

            ctx.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();

            // 太长时间没收到客户端数据 关闭连接
            if (state == IdleState.READER_IDLE) {
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


    private RpcResponseBody buildResponseBody(String reqId, RpcResponseConfig response) {
        RpcResponseBody resp = new RpcResponseBody();
        resp.setMessage("ok");
        resp.setCode(200);
        resp.setRequestId(reqId);
        resp.setData(response.getData());
        return resp;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
