/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.service.discovery;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceReferenceAttribute;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceNotFoundException;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.base.service.zookeeper.Zookeeper;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestConfig;
import xyz.yanghaoyu.flora.rpc.base.util.ServiceUtil;
import xyz.yanghaoyu.flora.rpc.client.cluster.RpcInvocation;
import xyz.yanghaoyu.flora.rpc.client.cluster.URL;
import xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance.ServiceLoadBalance;
import xyz.yanghaoyu.flora.rpc.client.config.DiscoveryConfig;

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

    private final Map<String, List<URL>> urlCache = new ConcurrentHashMap<>();
    private final DiscoveryConfig    config;
    private final Zookeeper          zooKeeper;
    private final ServiceLoadBalance defaultServiceLoadBalance;

    public ZookeeperServiceDiscovery(DiscoveryConfig config, Zookeeper zooKeeper) {
        this.config = config;
        this.zooKeeper = zooKeeper;
        String             defaultLoadBalanceName = config.defaultLoadBalance();
        ServiceLoadBalance loadBalance            = config.loadBalanceService().getLoadBalance(defaultLoadBalanceName);
        if (loadBalance == null) {
            throw new RuntimeException("unknown default load balance [" + defaultLoadBalanceName + "]");
        }
        this.defaultServiceLoadBalance = loadBalance;
    }

    // ========================================   public methods   =========================================

    @Override
    public InetSocketAddress discover(RpcRequestConfig requestConfig) throws ServiceNotFoundException {
        ServiceReferenceAttribute attribute = requestConfig.getServiceReferenceAttribute();

        String    serviceName = attribute.getServiceName();
        String    namespace   = requestConfig.getServiceReferenceAttribute().getNamespace();
        String    path        = ServiceUtil.buildNamespacedServiceNodePath(namespace, serviceName);
        List<URL> urls        = getURL(path);

        URL url = selectTargetUrl(serviceName, urls, requestConfig);

        if (url == null) {
            return null;
            // throw new ServiceNotFoundException("discovered no service [" + attribute.getServiceName() + "] in namespace [" + namespace + "]");
        }

        logger.info("discovered service [{}] on [{}]", attribute, url);
        return ServiceUtil.buildAddress(url.getAddress());
    }

    // ========================================   public methods   =========================================


    // -----------------------------------------------------------------------------------------------------
    // --------------------------------------    private methods    ----------------------------------------
    // -----------------------------------------------------------------------------------------------------

    private URL selectTargetUrl(String serviceName, List<URL> urls, RpcRequestConfig requestConfig) {
        return getServiceLoadBalance(requestConfig.getLoadBalance()).select(urls, buildInvocation(requestConfig, serviceName));
    }

    private ServiceLoadBalance getServiceLoadBalance(String loadBalanceName) {
        return loadBalanceName == null ? defaultServiceLoadBalance : config.loadBalanceService().getLoadBalance(loadBalanceName);
    }

    private RpcInvocation buildInvocation(RpcRequestConfig requestConfig, String serviceName) {
        return new RpcInvocation(serviceName, requestConfig.getMethodName(), requestConfig.getParams(), requestConfig.getParamTypes());
    }

    private List<URL> getURL(String servicePath) {
        List<URL> nodes = urlCache.get(servicePath);
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
        return zooKeeper.getChildrenNodes(servicePath).stream().map(URL::new).collect(Collectors.toList());
    }

    private void cacheService(String servicePath, List<URL> nodes) {
        urlCache.put(servicePath, nodes);
        // 向 zookeeper 注册监听器，保证数据的实时一致性
        zooKeeper.registerPathChildrenWatcher(servicePath, event -> {
            if (event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {
                // 结点被更新
                urlCache.put(servicePath, pullURL(servicePath));
            }
            else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                // 结点被删除
                urlCache.remove(servicePath);
            }
        });
    }

    // -----------------------------------------------------------------------------------------------------
    // --------------------------------------    private methods    ----------------------------------------
    // -----------------------------------------------------------------------------------------------------
}
