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
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcMessage;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestBody;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseBody;
import xyz.yanghaoyu.flora.rpc.server.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.server.transport.filter.FilterChain;

public class RpcRequestHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);

    private ServiceHandler serviceHandler;
    private FilterChain    filterChain;

    public RpcRequestHandler(ServiceHandler serviceHandler) {
        this.serviceHandler = serviceHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) {
        if (msg instanceof RpcRequestBody) {
            RpcRequestBody requestBody = (RpcRequestBody) msg;

            // handle request
            RpcResponseConfig responseConfig = serviceHandler.handle(requestBody);

            RpcMessage<RpcResponseBody> message = buildRpcMessage(requestBody.getId(), responseConfig);

            // check active
            if (!(context.channel().isActive() && context.channel().isWritable())) {
                RpcResponseBody responseBody = buildRpcResponseBodyOnError(requestBody);
                message.setBody(responseBody);
                logger.error("message dropped. cause: channel is not writable");
            }

            // write out
            context.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
    }

    private RpcResponseBody buildRpcResponseBodyOnError(RpcRequestBody requestBody) {
        RpcResponseBody responseBody = new RpcResponseBody();
        responseBody.setCode(400);
        responseBody.setMessage("channel is not active or channel cannot write");
        responseBody.setRequestId(requestBody.getId());
        return responseBody;
    }

    private RpcMessage<RpcResponseBody> buildRpcMessage(String id, RpcResponseConfig responseConfig) {
        RpcMessage<RpcResponseBody> message = RpcMessage.of(RpcMessage.RESPONSE_MESSAGE_TYPE, buildResponseBody(id, responseConfig));
        message.setSerializer(responseConfig.getSerializer());
        message.setCompressor(responseConfig.getCompressor());
        return message;
    }

    private RpcResponseBody buildResponseBody(String requestId, RpcResponseConfig responseConfig) {
        RpcResponseBody responseBody = new RpcResponseBody();
        responseBody.setMessage("ok");
        responseBody.setCode(200);
        responseBody.setRequestId(requestId);
        responseBody.setData(responseConfig.getData());
        return responseBody;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext context, Object event) throws Exception {
        if (event instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) event).state();

            // 太长时间没收到客户端数据 关闭连接
            if (state == IdleState.READER_IDLE) {
                context.close();
            }
        } else {
            super.userEventTriggered(context, event);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        cause.printStackTrace();
        context.close();
    }
}
