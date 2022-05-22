/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.service.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceException;
import xyz.yanghaoyu.flora.rpc.base.service.zookeeper.ZooKeeper;
import xyz.yanghaoyu.flora.rpc.base.util.ServiceUtil;
import xyz.yanghaoyu.flora.rpc.server.annotation.ServiceAttribute;
import xyz.yanghaoyu.flora.rpc.server.config.RegistryConfig;
import xyz.yanghaoyu.flora.rpc.server.service.Service;
import xyz.yanghaoyu.flora.rpc.server.service.ServiceRegistry;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZooKeeperServiceRegistry implements ServiceRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceRegistry.class);

    private final ZooKeeper            zooKeeper;
    private final Map<String, Service> registeredServices = new ConcurrentHashMap<>();
    private final RegistryConfig       config;

    public ZooKeeperServiceRegistry(RegistryConfig config, ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
        this.config = config;
    }

    @Override
    public void register(InetSocketAddress address, Service service) {
        ServiceAttribute attribute = service.getServiceAttribute();

        // namespace
        String namespace = attribute.getNamespace();
        // service name
        String serviceName = attribute.getServiceName();

        String path = ServiceUtil.buildServicePathWithAddress(namespace, serviceName, address);

        LOGGER.info("registry zookeeper node: [" + path + "]");
        zooKeeper.createPersistentNode(path);
        registeredServices.put(serviceName, service);
    }

    @Override
    public Service getService(String serviceName) {
        Service service = registeredServices.get(serviceName);
        if (service == null) {
            throw new ServiceException("unknown service: " + serviceName);
        }
        return service;
    }
}
