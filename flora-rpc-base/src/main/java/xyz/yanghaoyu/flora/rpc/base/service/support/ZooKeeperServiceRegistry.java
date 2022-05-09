/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service.support;

import xyz.yanghaoyu.flora.rpc.base.exception.ServiceException;
import xyz.yanghaoyu.flora.rpc.base.service.config.Service;
import xyz.yanghaoyu.flora.rpc.base.service.config.ServiceConfig;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.base.util.ServiceUtil;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZooKeeperServiceRegistry implements ServiceRegistry {
    private ZooKeeper            zooKeeper;
    private Map<String, Service> registeredServices = new ConcurrentHashMap<>();

    public ZooKeeperServiceRegistry(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    @Override
    public void register(InetSocketAddress address, Service service) {
        ServiceConfig serviceConfig = service.getConfig();
        String        namespace     = serviceConfig.getNamespace();
        String        serviceName   = serviceConfig.getServiceName();
        String        path          = ServiceUtil.buildServicePath(namespace, serviceName, address);

        zooKeeper.createPersistentNode(path);
        registeredServices.put(serviceName, service);
        // todo remove service
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
