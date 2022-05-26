/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.service.config;

import xyz.yanghaoyu.flora.core.Ordered;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseBody;
import xyz.yanghaoyu.flora.rpc.base.transport.RpcRequestConfig;

import java.net.InetSocketAddress;

public interface ServiceReferenceInterceptor extends Ordered {
    default void beforeRequest(InetSocketAddress target, RpcRequestConfig requestConfig) {
    }

    default void afterRequest() {
    }

    default void afterResponse(RpcResponseBody responseBody) {
    }

    boolean shouldIntercept(Object bean, String beanName);
}
