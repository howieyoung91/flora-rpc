/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceException;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.base.service.config.ServiceConfig;
import xyz.yanghaoyu.flora.rpc.base.service.support.ZooKeeper;
import xyz.yanghaoyu.flora.rpc.base.util.ServiceUtil;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcServiceReference;
import xyz.yanghaoyu.flora.rpc.client.discovery.config.DefaultDiscoveryConfig;
import xyz.yanghaoyu.flora.rpc.client.strategy.loadbalance.ServiceLoadBalance;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 在 Zookeeper 中发现服务
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);

    private Map<String, List<String>> cache = new ConcurrentHashMap<>();

    private final DefaultDiscoveryConfig config;
    private final ZooKeeper              zooKeeper;
    private final ServiceLoadBalance     loadBalance;

    public ZookeeperServiceDiscovery(DefaultDiscoveryConfig config, ZooKeeper newZooKeeper, ServiceLoadBalance loadBalance) {
        this.config = config;
        this.zooKeeper = newZooKeeper;
        this.loadBalance = loadBalance;
    }

    @Override
    public InetSocketAddress discover(ServiceConfig serviceConfig) {
        String       serviceName = serviceConfig.getServiceName();
        String       namespace   = getNamespace(serviceConfig);
        String       servicePath = ServiceUtil.buildNamespacedServiceNodePath(namespace, serviceName);
        List<String> addresses   = getAddresses(servicePath);

        String addressString = loadBalance.select(serviceConfig, addresses);

        if (addressString == null) {
            throw new ServiceException("discovered no service named " + serviceConfig);
        }

        logger.info("discovered service [{}] at [{}]", serviceConfig, addressString);
        return ServiceUtil.buildAddress(addressString);
    }

    private String getNamespace(ServiceConfig serviceConfig) {
        String namespace = serviceConfig.getNamespace();
        if (serviceConfig.getServiceName().equals(RpcServiceReference.EMPTY_NAMESPACE)) {
            namespace = config.getNamespace();
        }
        return namespace;
    }

    private List<String> getAddresses(String servicePath) {
        List<String> nodes = cache.get(servicePath);

        if (nodes == null) {
            nodes = zooKeeper.getChildrenNodes(servicePath);
            cacheService(servicePath, nodes);
        }
        return nodes;
    }

    private void cacheService(String servicePath, List<String> nodes) {
        cache.put(servicePath, nodes);
        zooKeeper.registerPathChildrenWatcher(servicePath, event -> {
            cache.put(servicePath, zooKeeper.getChildrenNodes(servicePath));
        });
    }
}
