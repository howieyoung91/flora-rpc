/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */
package xyz.yanghaoyu.flora.rpc.client.service.config;

import xyz.yanghaoyu.flora.rpc.base.service.ServiceReference;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestConfig;

import java.net.InetSocketAddress;

public interface DiscoveryAwareServiceReferenceInterceptor extends ServiceReferenceInterceptor {
    default InetSocketAddress adviseTargetService(RpcRequestConfig requestConfig) {
        return null;
    }

    default Object onNoServiceDiscovered(RpcRequestConfig requestConfig, ServiceReference reference) {
        return null;
    }

    default InetSocketAddress afterDiscoverService(InetSocketAddress target) {
        return target;
    }
}
