/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.proxy;

import xyz.yanghaoyu.flora.rpc.base.service.config.ServiceConfig;
import xyz.yanghaoyu.flora.rpc.client.transport.RpcClient;

import java.lang.reflect.Proxy;

public class ServiceReferenceProxyFactory {
    private final RpcClient rpcClient;

    public ServiceReferenceProxyFactory(RpcClient client) {
        this.rpcClient = client;
    }

    public <T> T getProxy(Class<T> aInterface, ServiceConfig serviceConfig) {
        return (T) Proxy.newProxyInstance(
                aInterface.getClassLoader(),
                new Class[]{aInterface},
                new ServiceReferenceProxy(rpcClient, serviceConfig)
        );
    }
}
