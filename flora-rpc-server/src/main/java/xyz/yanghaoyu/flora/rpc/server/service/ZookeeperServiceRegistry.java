/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.service;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceAttribute;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceException;
import xyz.yanghaoyu.flora.rpc.base.service.Service;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.base.service.support.zookeeper.Zookeeper;
import xyz.yanghaoyu.flora.rpc.base.util.ServiceUtil;
import xyz.yanghaoyu.flora.rpc.server.config.RegistryConfig;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZookeeperServiceRegistry implements ServiceRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceRegistry.class);

    private final Zookeeper            zookeeper;
    private final Map<String, Service> registeredServices = new ConcurrentHashMap<>();
    private final Map<String, String>  paths              = new ConcurrentHashMap<>();
    private final RegistryConfig       config;

    public ZookeeperServiceRegistry(RegistryConfig config, Zookeeper zookeeper) {
        this.zookeeper = zookeeper;
        this.config = config;
    }

    @Override
    public void register(Service service) {
        ServiceAttribute attribute   = service.getServiceAttribute();
        String           serviceName = attribute.getServiceName();
        registeredServices.put(serviceName, service);
    }

    @Override
    public void exposeServices(InetSocketAddress address) {
        for (Service service : registeredServices.values()) {
            ServiceAttribute attribute   = service.getServiceAttribute();
            String           namespace   = attribute.getNamespace();
            String           serviceName = attribute.getServiceName();
            createZookeeperNode(address, namespace, serviceName);
        }
    }

    @Override
    public void cancelServices() {
        for (String path : paths.keySet()) {
            zookeeper.deletePersistentNode(path);
        }
    }

    @Override
    public Service getService(String serviceName) {
        Service service = registeredServices.get(serviceName);
        if (service == null) {
            throw new ServiceException("unknown service: " + serviceName);
        }
        return service;
    }

    @Override
    public boolean contains(String serviceName) {
        return registeredServices.containsKey(serviceName);
    }

    private void createZookeeperNode(InetSocketAddress address, String namespace, String serviceName) {
        String serviceNodePath = ServiceUtil.buildNamespacedServiceNodePath(namespace, serviceName);
        String path            = ServiceUtil.buildServicePathWithAddress(serviceNodePath, address);
        paths.put(path, serviceName);
        zookeeper.createPersistentNode(path);
        // 从缓存移除结点
        zookeeper.registerPathChildrenWatcher(serviceNodePath, event -> {
            if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                String removedServiceName = paths.remove(path);
                if (removedServiceName != null) {
                    LOGGER.info("canceled rpc service [{}]", removedServiceName);
                }
            }
        });
    }
}
