/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.service.discovery;

import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceReferenceAttribute;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceNotFoundException;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.base.transport.RpcRequestConfig;

import java.net.InetSocketAddress;

public class DefaultServiceDiscovery implements ServiceDiscovery {
    public static final DefaultServiceDiscovery INSTANCE = new DefaultServiceDiscovery();

    @Override
    public InetSocketAddress discover(RpcRequestConfig requestConfig) throws ServiceNotFoundException {
        ServiceReferenceAttribute attribute   = requestConfig.getServiceReferenceAttribute();
        String                    serviceName = attribute.getServiceName();
        String                    namespace   = attribute.getNamespace();
        throw new ServiceNotFoundException("discovered no service [" + serviceName + "] in namespace [" + namespace + "]");
    }
}
