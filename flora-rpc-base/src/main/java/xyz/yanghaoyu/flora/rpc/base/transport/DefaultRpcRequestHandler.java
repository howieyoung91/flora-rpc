/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.transport;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.framework.core.OrderComparator;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcMessage;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestBody;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseBody;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseConfig;
import xyz.yanghaoyu.flora.rpc.base.transport.interceptor.ResponseAwareServiceInterceptor;
import xyz.yanghaoyu.flora.rpc.base.transport.interceptor.ServiceInterceptor;

import java.util.TreeSet;

@ChannelHandler.Sharable
public class DefaultRpcRequestHandler extends ChannelInboundHandlerAdapter implements RpcRequestHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRpcRequestHandler.class);

    private ServiceHandler                           serviceHandler;
    private TreeSet<ServiceInterceptor>              interceptors;
    private TreeSet<ResponseAwareServiceInterceptor> responseAwareInterceptors;

    public DefaultRpcRequestHandler(ServiceHandler serviceHandler, TreeSet<ServiceInterceptor> interceptors) {
        this.serviceHandler = serviceHandler;
        this.interceptors = interceptors;
        this.responseAwareInterceptors = new TreeSet<>(OrderComparator.INSTANCE);
        if (interceptors != null) {
            interceptors.stream().filter(interceptor -> interceptor instanceof ResponseAwareServiceInterceptor)
                    .forEach(interceptor -> responseAwareInterceptors.add((ResponseAwareServiceInterceptor) interceptor));
        }
    }

    // ========================================   public methods   =========================================

    @Override
    public void channelRead(ChannelHandlerContext context, Object body) {
        if (body instanceof RpcRequestBody) {
            RpcRequestBody    requestBody    = (RpcRequestBody) body;
            RpcResponseConfig responseConfig = null;
            responseConfig = handleRequest(requestBody);
            RpcMessage<RpcResponseBody> message = buildRpcMessage(requestBody.getId(), responseConfig);
            write(context, requestBody, message);
        }
    }

    @Override
    public RpcResponseConfig handleRequest(RpcRequestBody requestBody) {
        RpcResponseConfig responseConfig = applyInterceptorsAdviseHandle(requestBody);
        if (responseConfig == null) {
            applyInterceptorsBeforeHandle(requestBody);
            try {
                responseConfig = serviceHandler.handle(requestBody);
            }
            catch (Exception e) {
                applyInterceptorsOnExceptions(requestBody, e);
            }
            applyInterceptorsAfterHandle(requestBody, responseConfig);
        }
        return responseConfig;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext context, Object event) throws Exception {
        if (event instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) event).state();
            // 太长时间没收到客户端数据 关闭连接
            if (state == IdleState.READER_IDLE) {
                context.close();
            }
        }
        else {
            super.userEventTriggered(context, event);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        cause.printStackTrace();
        context.close();
    }

    // ========================================   public methods   =========================================


    // -----------------------------------------------------------------------------------------------------
    // --------------------------------------    private methods    ----------------------------------------
    // -----------------------------------------------------------------------------------------------------

    private RpcResponseConfig applyInterceptorsAdviseHandle(RpcRequestBody requestBody) {
        for (ServiceInterceptor interceptor : interceptors) {
            RpcResponseConfig advisedResponseConfig = interceptor.adviseHandle(requestBody);
            if (advisedResponseConfig != null) {
                return advisedResponseConfig;
            }
        }
        return null;
    }

    private void applyInterceptorsOnExceptions(RpcRequestBody requestBody, Exception e) {
        for (ServiceInterceptor interceptor : interceptors) {
            interceptor.onExceptions(requestBody, e);
        }
    }

    private void applyInterceptorsBeforeHandle(RpcRequestBody requestBody) {
        for (ServiceInterceptor interceptor : interceptors) {
            interceptor.beforeHandle(requestBody);
        }
    }

    private void applyInterceptorsAfterHandle(RpcRequestBody requestBody, RpcResponseConfig responseConfig) {
        for (ServiceInterceptor interceptor : interceptors) {
            interceptor.afterHandle(requestBody, responseConfig);
        }
    }

    /**
     * 写出消息
     */
    private void write(ChannelHandlerContext context, RpcRequestBody requestBody, RpcMessage<RpcResponseBody> message) {
        // check active
        if (!isChannelOK(context)) {
            RpcResponseBody responseBody = buildRpcResponseBodyOnError(requestBody);
            applyInterceptorOnErrorResponse(responseBody);
            message.setBody(responseBody);
            LOGGER.error("message dropped. cause: channel is not writable");
        }

        applyInterceptorBeforeResponse(message);
        // write out
        context.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE).addListener(future -> applyInterceptorAfterResponse(requestBody, message));
    }


    private void applyInterceptorBeforeResponse(RpcMessage<RpcResponseBody> message) {
        for (ResponseAwareServiceInterceptor interceptor : responseAwareInterceptors) {
            interceptor.beforeResponse(message);
        }
    }

    private void applyInterceptorOnErrorResponse(RpcResponseBody responseBody) {
        for (ResponseAwareServiceInterceptor interceptor : responseAwareInterceptors) {
            interceptor.onErrorResponse(responseBody);
        }
    }

    private void applyInterceptorAfterResponse(RpcRequestBody requestBody, RpcMessage<RpcResponseBody> message) {
        for (ResponseAwareServiceInterceptor interceptor : responseAwareInterceptors) {
            interceptor.afterResponse(requestBody, message);
        }
    }

    // -----------------------------------------------------------------------------------------------------
    // --------------------------------------    private methods    ----------------------------------------
    // -----------------------------------------------------------------------------------------------------

    private static boolean isChannelOK(ChannelHandlerContext context) {
        return context.channel().isActive() && context.channel().isWritable();
    }

    private static RpcResponseBody buildRpcResponseBodyOnError(RpcRequestBody requestBody) {
        RpcResponseBody responseBody = new RpcResponseBody();
        responseBody.setCode(400);
        responseBody.setMessage("channel is not active or channel cannot write");
        responseBody.setRequestId(requestBody.getId());
        return responseBody;
    }

    private static RpcMessage<RpcResponseBody> buildRpcMessage(String id, RpcResponseConfig responseConfig) {
        RpcMessage<RpcResponseBody> message = RpcMessage.of(RpcMessage.RESPONSE_MESSAGE_TYPE, buildResponseBody(id, responseConfig));
        message.setSerializer(responseConfig.getSerializer());
        message.setCompressor(responseConfig.getCompressor());
        return message;
    }

    private static RpcResponseBody buildResponseBody(String requestId, RpcResponseConfig responseConfig) {
        RpcResponseBody responseBody = new RpcResponseBody();
        responseBody.setMessage("ok");
        responseBody.setCode(200);
        responseBody.setRequestId(requestId);
        responseBody.setData(responseConfig.getData());
        return responseBody;
    }
}
