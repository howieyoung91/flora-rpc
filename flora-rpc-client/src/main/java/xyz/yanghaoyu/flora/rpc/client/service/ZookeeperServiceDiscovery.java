/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.service;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceNotFoundException;
import xyz.yanghaoyu.flora.rpc.base.service.zookeeper.ZooKeeper;
import xyz.yanghaoyu.flora.rpc.base.util.ServiceUtil;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcServiceReference;
import xyz.yanghaoyu.flora.rpc.client.annotation.ServiceReferenceAttribute;
import xyz.yanghaoyu.flora.rpc.client.cluster.RpcInvocation;
import xyz.yanghaoyu.flora.rpc.client.cluster.URL;
import xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance.ServiceLoadBalance;
import xyz.yanghaoyu.flora.rpc.client.config.DiscoveryConfig;
import xyz.yanghaoyu.flora.rpc.client.transport.RpcRequestConfig;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * 在 Zookeeper 中发现服务
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);

    private Map<String, List<URL>> cache = new ConcurrentHashMap<>();

    private final DiscoveryConfig    config;
    private final ZooKeeper          zooKeeper;
    private final ServiceLoadBalance defaultServiceLoadBalance;

    public ZookeeperServiceDiscovery(DiscoveryConfig config, ZooKeeper zooKeeper) {
        this.config = config;
        this.zooKeeper = zooKeeper;

        // build default load balance
        String             defaultLoadBalanceName = config.defaultLoadBalance();
        ServiceLoadBalance loadBalance            = config.loadBalanceService().getLoadBalance(defaultLoadBalanceName);
        if (loadBalance == null) {
            throw new RuntimeException("unknown default load balance [" + defaultLoadBalanceName + "]");
        }
        this.defaultServiceLoadBalance = loadBalance;
    }

    @Override
    public InetSocketAddress discover(RpcRequestConfig reqConfig) throws ServiceNotFoundException {
        ServiceReferenceAttribute serviceRefAttr = reqConfig.getServiceRefAttr();

        String    serviceName = serviceRefAttr.getServiceName();
        String    namespace   = getNamespace(serviceRefAttr);
        String    servicePath = ServiceUtil.buildNamespacedServiceNodePath(namespace, serviceName);
        List<URL> urls        = getURL(servicePath);

        URL url = getServiceLoadBalance(reqConfig.getLoadBalance())
                .select(urls, buildInvocation(reqConfig, serviceName));

        if (url == null) {
            throw new ServiceNotFoundException("discovered no service [" + serviceRefAttr.getServiceName() + "] in namespace [" + namespace + "]");
        }

        logger.info("discovered service [{}] at [{}]", serviceRefAttr, url);
        return ServiceUtil.buildAddress(url.getAddress());
    }

    private ServiceLoadBalance getServiceLoadBalance(String loadBalanceName) {
        return loadBalanceName == null
                ? defaultServiceLoadBalance
                : config.loadBalanceService().getLoadBalance(loadBalanceName);
    }

    private RpcInvocation buildInvocation(RpcRequestConfig reqConfig, String serviceName) {
        RpcInvocation rpcInvocation = new RpcInvocation(serviceName, reqConfig.getMethodName(), reqConfig.getParams(), reqConfig.getParamTypes());
        return rpcInvocation;
    }

    private String getNamespace(ServiceReferenceAttribute serviceConfig) {
        String namespace = serviceConfig.getNamespace();
        if (serviceConfig.getNamespace().equals(RpcServiceReference.EMPTY_NAMESPACE)) {
            namespace = config.namespace();
        }
        return namespace;
    }

    private List<URL> getURL(String servicePath) {
        List<URL> nodes = cache.get(servicePath);
        if (nodes != null) {
            return nodes;
        }

        nodes = pullURL(servicePath);
        cacheService(servicePath, nodes);
        return nodes;
    }

    /**
     * 从 zookeeper 拉取最新数据
     */
    private List<URL> pullURL(String servicePath) {
        return zooKeeper.getChildrenNodes(servicePath)
                .stream().map(URL::new)
                .collect(Collectors.toList());
    }

    private void cacheService(String servicePath, List<URL> nodes) {
        cache.put(servicePath, nodes);
        // 向 zookeeper 注册监听器，保证数据的实时一致性
        zooKeeper.registerPathChildrenWatcher(servicePath, event -> {
            if (event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {
                // 结点被更新
                cache.put(servicePath, pullURL(servicePath));
            } else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                // 结点被删除
                cache.remove(servicePath);
            }
        });
    }
}
