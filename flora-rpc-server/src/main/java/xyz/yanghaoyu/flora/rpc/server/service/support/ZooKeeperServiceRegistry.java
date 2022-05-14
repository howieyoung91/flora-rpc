/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.service.support;

import xyz.yanghaoyu.flora.rpc.base.exception.ServiceException;
import xyz.yanghaoyu.flora.rpc.server.annotation.RpcServiceAttribute;
import xyz.yanghaoyu.flora.rpc.base.service.zookeeper.ZooKeeper;
import xyz.yanghaoyu.flora.rpc.base.util.ServiceUtil;
import xyz.yanghaoyu.flora.rpc.server.service.Service;
import xyz.yanghaoyu.flora.rpc.server.service.ServiceRegistry;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZooKeeperServiceRegistry implements ServiceRegistry {
    private final ZooKeeper            zooKeeper;
    private final Map<String, Service> registeredServices = new ConcurrentHashMap<>();

    public ZooKeeperServiceRegistry(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    @Override
    public void register(InetSocketAddress address, Service service) {
        RpcServiceAttribute serviceConfig = service.getServiceConfig();
        String              namespace     = serviceConfig.getNamespace();
        String              serviceName   = serviceConfig.getServiceName();
        String              path          = ServiceUtil.buildServicePathWithAddress(namespace, serviceName, address);

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
