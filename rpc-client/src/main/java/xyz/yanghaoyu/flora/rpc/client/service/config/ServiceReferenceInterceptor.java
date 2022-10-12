/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.service.config;

import xyz.yanghaoyu.flora.framework.core.Ordered;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseBody;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.DefaultRpcRequestConfig;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public interface ServiceReferenceInterceptor extends Ordered {
    default void beforeRequest(InetSocketAddress target, DefaultRpcRequestConfig requestConfig) {
    }

    default void afterRequest(CompletableFuture<?> promise, DefaultRpcRequestConfig requestConfig) {
    }

    default void afterResponse(RpcResponseBody responseBody) {
    }

    boolean shouldIntercept(Object bean, String beanName);
}
