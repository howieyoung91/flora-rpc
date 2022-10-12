/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.transport.interceptor;

import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcMessage;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestBody;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseBody;

public interface ResponseAwareServiceInterceptor extends ServiceInterceptor {
    default void beforeResponse(RpcMessage<RpcResponseBody> message) {
    }

    default void afterResponse(RpcRequestBody requestBody, RpcMessage<RpcResponseBody> message) {
    }

    default void onErrorResponse(RpcResponseBody responseBody) {
    }
}
