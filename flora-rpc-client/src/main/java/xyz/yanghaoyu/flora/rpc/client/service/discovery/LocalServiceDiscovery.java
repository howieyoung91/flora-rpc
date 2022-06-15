/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.service.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestConfig;

import java.net.InetSocketAddress;

/**
 * 发现本地服务
 */
public class LocalServiceDiscovery implements ServiceDiscovery {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalServiceDiscovery.class);

    private ServiceRegistry   localRegistry;
    private InetSocketAddress localhost;

    public LocalServiceDiscovery(ServiceRegistry localRegistry, InetSocketAddress localhost) {
        this.localRegistry = localRegistry;
        this.localhost = localhost;
    }

    @Override
    public InetSocketAddress discover(RpcRequestConfig requestConfig) {
        if (requestConfig.isAlwaysRemote() || localRegistry == null) {
            return null;
        }
        String serviceName = requestConfig.getServiceReferenceAttribute().getServiceName();
        return doDiscover(serviceName);
    }

    private InetSocketAddress doDiscover(String serviceName) {
        if (localRegistry.contains(serviceName)) {
            LOGGER.info("discovered service [{}] at localhost", serviceName);
            return localhost;
        }
        return null;
    }
}
