/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.service;

import xyz.yanghaoyu.flora.rpc.base.service.Service;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

// TODO 对多注册中心进行支持
public class ServiceRegistryChain implements ServiceRegistry {
    private final List<ServiceRegistry> registries = new LinkedList<>();

    public void addServiceRegistry(ServiceRegistry registry) {
        registries.add(registry);
    }

    @Override
    public void register(Service service) {
        registries.forEach(registry -> registry.register(service));
    }

    @Override
    public void exposeServices(InetSocketAddress address) {
        registries.forEach(registry -> registry.exposeServices(address));
    }

    @Override
    public void cancelServices() {
        registries.forEach(ServiceRegistry::cancelServices);
    }

    @Override
    public Service getService(String serviceName) {
        for (ServiceRegistry registry : registries) {
            Service service = registry.getService(serviceName);
            if (service != null) {
                return service;
            }
        }
        return null;
    }

    @Override
    public boolean contains(String serviceName) {
        return registries.stream().anyMatch(registry -> registry.contains(serviceName));
    }
}
