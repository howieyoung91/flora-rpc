/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.service.discovery;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceReferenceAttribute;
import xyz.yanghaoyu.flora.rpc.base.cluster.URL;
import xyz.yanghaoyu.flora.rpc.base.service.discovery.AbstractServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.base.service.support.zookeeper.Zookeeper;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestConfig;
import xyz.yanghaoyu.flora.rpc.base.util.ServiceUtil;
import xyz.yanghaoyu.flora.rpc.client.cluster.RpcInvocation;
import xyz.yanghaoyu.flora.rpc.client.config.DiscoveryConfig;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 在 Zookeeper 中发现服务
 */
public class ZookeeperServiceDiscovery extends AbstractServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);

    private final DiscoveryConfig config;
    private final Zookeeper       zookeeper;

    public ZookeeperServiceDiscovery(DiscoveryConfig config, Zookeeper zooKeeper) {
        super(config.loadBalanceService(), config.loadBalanceService(),
                config.defaultLoadBalance(), config.loadBalanceService().getLoadBalance(config.defaultLoadBalance()));
        this.config = config;
        this.zookeeper = zooKeeper;
    }

    // ========================================   public methods   =========================================

    @Override
    public InetSocketAddress discover(RpcRequestConfig requestConfig) {
        ServiceReferenceAttribute attribute = requestConfig.getServiceReferenceAttribute();

        String    serviceName = attribute.getServiceName();
        String    namespace   = requestConfig.getServiceReferenceAttribute().getNamespace();
        String    path        = ServiceUtil.buildNamespacedServiceNodePath(namespace, serviceName);
        List<URL> urls        = getURL(path);

        URL url = selectTargetUrl(serviceName, urls, requestConfig);

        if (url == null) {
            logger.error("no service [{}] discovered. Method: {}", attribute.getServiceName(), requestConfig.getMethod());
            return null;
        }

        logger.info("discovered service [{}] on [{}]", attribute.getServiceName(), url);
        return ServiceUtil.buildAddress(url.getAddress());
    }

    // ========================================   public methods   =========================================


    // -----------------------------------------------------------------------------------------------------
    // --------------------------------------    private methods    ----------------------------------------
    // -----------------------------------------------------------------------------------------------------

    private List<URL> getURL(String servicePath) {
        List<URL> urls = getCache().get(servicePath);
        if (urls != null) {
            return urls;
        }

        urls = pullURL(servicePath);
        cacheService(servicePath, urls);
        return urls;
    }

    private URL selectTargetUrl(String serviceName, List<URL> urls, RpcRequestConfig requestConfig) {
        return determineLoadBalance(requestConfig.getLoadBalance())
                .select(urls, RpcInvocation.of(serviceName, requestConfig));
    }

    /**
     * 从 zookeeper 拉取最新数据
     */
    private List<URL> pullURL(String servicePath) {
        return zookeeper.getChildrenNodes(servicePath).stream().map(URL::new).collect(Collectors.toList());
    }

    private void cacheService(String servicePath, List<URL> nodes) {
        // 第一次 put 时注册监听器
        Map<String, List<URL>> cache = getCache();
        if (cache.put(servicePath, nodes) == null) {
            // 向 zookeeper 注册监听器，保证数据的实时一致性
            zookeeper.registerPathChildrenWatcher(servicePath, event -> {
                if (event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {
                    // 结点被更新
                    cache.put(servicePath, pullURL(servicePath));
                }
                else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                    // 结点被删除
                    cache.remove(servicePath);
                }
            });
        }
    }
    // -----------------------------------------------------------------------------------------------------
    // --------------------------------------    private methods    ----------------------------------------
    // -----------------------------------------------------------------------------------------------------
}
