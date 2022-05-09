/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.transport.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcMessage;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequest;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponse;

public class RpcRequestHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);

    private ServiceHandler serviceHandler;

    public RpcRequestHandler(ServiceHandler serviceHandler) {
        this.serviceHandler = serviceHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcMessage) {
            RpcMessage requestMessage  = (RpcMessage) msg;
            RpcMessage responseMessage = new RpcMessage();
            responseMessage.setCodec(((byte) 0));
            responseMessage.setCompress(((byte) 0));

            if (requestMessage.getMessageType() == RpcMessage.HEARTBEAT_REQUEST_MESSAGE_TYPE) {
                responseMessage.setMessageType(RpcMessage.HEARTBEAT_RESPONSE_MESSAGE_TYPE);
                responseMessage.setData("pong");
            } else {
                responseMessage.setMessageType(RpcMessage.RESPONSE_MESSAGE_TYPE);

                RpcRequest request = (RpcRequest) requestMessage.getData();
                Object     result  = serviceHandler.handle(request);

                RpcResponse rpcResponse = buildResponse(request, result);
                responseMessage.setData(rpcResponse);
            }

            if (!(ctx.channel().isActive() && ctx.channel().isWritable())) {
                RpcResponse response = new RpcResponse();
                response.setCode(400);
                response.setMessage("channel is not active or channel cannot write");
                requestMessage.setData(response);
            }

            ctx.writeAndFlush(responseMessage)
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
    }

    private RpcResponse buildResponse(RpcRequest request, Object result) {
        RpcResponse resp = new RpcResponse();
        resp.setMessage("ok");
        resp.setCode(200);
        resp.setRequestId(request.getId());
        resp.setData(result);
        return resp;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}
