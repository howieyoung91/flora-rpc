/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.transport;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.core.OrderComparator;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcMessage;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestBody;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseBody;
import xyz.yanghaoyu.flora.rpc.server.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.server.transport.interceptor.ResponseAwareServiceInterceptor;
import xyz.yanghaoyu.flora.rpc.server.transport.interceptor.ServiceInterceptor;

import java.util.TreeSet;

@ChannelHandler.Sharable
public class RpcRequestHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);

    private ServiceHandler                           serviceHandler;
    private TreeSet<ServiceInterceptor>              interceptors;
    private TreeSet<ResponseAwareServiceInterceptor> responseAwareInterceptors;

    public RpcRequestHandler(ServiceHandler serviceHandler, TreeSet<ServiceInterceptor> interceptors) {
        this.serviceHandler = serviceHandler;
        this.interceptors = interceptors;
        this.responseAwareInterceptors = new TreeSet<>(OrderComparator.INSTANCE);
        if (interceptors != null) {
            interceptors.stream().filter(interceptor -> interceptor instanceof ResponseAwareServiceInterceptor)
                    .forEach(interceptor -> responseAwareInterceptors.add((ResponseAwareServiceInterceptor) interceptor));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object body) {
        if (body instanceof RpcRequestBody) {
            RpcRequestBody requestBody = (RpcRequestBody) body;

            RpcResponseConfig responseConfig = null;

            responseConfig = applyAdviseHandle(requestBody);

            if (responseConfig == null) {
                applyInterceptorsBeforeHandle(requestBody);
                responseConfig = serviceHandler.handle(requestBody);
                applyInterceptorsAfterHandle(requestBody, responseConfig);
            }


            RpcMessage<RpcResponseBody> message = buildRpcMessage(requestBody.getId(), responseConfig);

            applyInterceptorBeforeResponse(message);
            write(context, requestBody, message);
            // applyInterceptorAfterResponse();
        }
    }

    private void applyInterceptorBeforeResponse(RpcMessage<RpcResponseBody> message) {
        for (ResponseAwareServiceInterceptor interceptor : responseAwareInterceptors) {
            interceptor.beforeResponse(message);
        }
    }

    private void applyInterceptorsAfterHandle(RpcRequestBody requestBody, RpcResponseConfig responseConfig) {
        for (ServiceInterceptor interceptor : interceptors) {
            interceptor.afterHandle(requestBody, responseConfig);
        }
    }

    private RpcResponseConfig applyAdviseHandle(RpcRequestBody requestBody) {
        for (ServiceInterceptor interceptor : interceptors) {
            RpcResponseConfig advisedResponseConfig = interceptor.adviseHandle(requestBody);
            if (advisedResponseConfig != null) {
                return advisedResponseConfig;
            }
        }
        return null;
    }

    private void applyInterceptorsBeforeHandle(RpcRequestBody requestBody) {
        for (ServiceInterceptor interceptor : interceptors) {
            interceptor.beforeHandle(requestBody);
        }
    }


    private void write(ChannelHandlerContext context, RpcRequestBody requestBody, RpcMessage<RpcResponseBody> message) {
        // check active
        if (!isChannelOK(context)) {
            RpcResponseBody responseBody = buildRpcResponseBodyOnError(requestBody);
            message.setBody(responseBody);
            logger.error("message dropped. cause: channel is not writable");
        }

        // write out
        context.writeAndFlush(message)
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
                .addListener(future -> {
                    applyInterceptorAfterResponse(requestBody, message);
                });
    }

    private void applyInterceptorAfterResponse(RpcRequestBody requestBody, RpcMessage<RpcResponseBody> message) {
        for (ResponseAwareServiceInterceptor interceptor : responseAwareInterceptors) {
            interceptor.afterResponse(requestBody, message);
        }
    }

    private boolean isChannelOK(ChannelHandlerContext context) {
        return context.channel().isActive() && context.channel().isWritable();
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
