/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.service.proxy;

import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceReference;
import xyz.yanghaoyu.flora.rpc.client.service.config.ServiceReferenceInterceptor;
import xyz.yanghaoyu.flora.rpc.client.transport.RpcClient;

import java.lang.reflect.Proxy;
import java.util.List;

public class ServiceReferenceProxyFactory {
    private final RpcClient client;

    public ServiceReferenceProxyFactory(RpcClient client) {
        this.client = client;
    }

    public <T> T getProxy(
            Class<T> aInterface,
            ServiceReference reference,
            ServiceDiscovery discovery,
            List<ServiceReferenceInterceptor> interceptors
    ) {
        ServiceReferenceProxy proxy = new ServiceReferenceProxy(client, reference, discovery);
        proxy.addServiceInterceptor(interceptors);
        return (T) Proxy.newProxyInstance(aInterface.getClassLoader(), new Class[]{aInterface}, proxy);
    }
}
