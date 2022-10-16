/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.service.discovery;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.framework.core.beans.factory.ApplicationEventPublisherAware;
import xyz.yanghaoyu.flora.framework.core.context.ApplicationEventPublisher;
import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceReferenceAttribute;
import xyz.yanghaoyu.flora.rpc.base.cluster.URL;
import xyz.yanghaoyu.flora.rpc.base.config.DiscoveryConfig;
import xyz.yanghaoyu.flora.rpc.base.event.RemoteService;
import xyz.yanghaoyu.flora.rpc.base.event.ServiceCanceledEvent;
import xyz.yanghaoyu.flora.rpc.base.event.ServicePublishedEvent;
import xyz.yanghaoyu.flora.rpc.base.service.discovery.AbstractServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.base.service.support.zookeeper.Zookeeper;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestConfig;
import xyz.yanghaoyu.flora.rpc.base.util.ServiceUtil;
import xyz.yanghaoyu.flora.rpc.client.cluster.RpcInvocation;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * 在 Zookeeper 中发现服务
 */
public class ZookeeperServiceDiscovery extends AbstractServiceDiscovery implements ApplicationEventPublisherAware {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);

    private final DiscoveryConfig           config;
    private final Zookeeper                 zookeeper;
    private       ApplicationEventPublisher eventPublisher;

    public ZookeeperServiceDiscovery(DiscoveryConfig config, Zookeeper zookeeper) {
        super(config.loadBalanceService(), config.loadBalanceService(),
                config.defaultLoadBalance(), config.loadBalanceService().getLoadBalance(config.defaultLoadBalance()));
        this.config = config;
        this.zookeeper = zookeeper;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.eventPublisher = publisher;
    }

    // ========================================   public methods   =========================================

    @Override
    public InetSocketAddress discover(RpcRequestConfig requestConfig) {
        ServiceReferenceAttribute attribute = requestConfig.serviceReferenceAttribute();

        String          serviceName = attribute.getServiceName();
        String          namespace   = requestConfig.serviceReferenceAttribute().getNamespace();
        Collection<URL> urls        = getUrls(serviceName, namespace);

        URL url = selectTargetUrl(serviceName, urls, requestConfig);

        if (url == null) {
            logger.error("no service [{}] discovered. Method: {}", attribute.getServiceName(), requestConfig.methodName());
            return null;
        }

        logger.info("discovered service [{}] on [{}]", attribute.getServiceName(), url);
        return ServiceUtil.buildAddress(url.getAddress());
    }


    // ========================================   public methods   =========================================


    // -----------------------------------------------------------------------------------------------------
    // --------------------------------------    private methods    ----------------------------------------
    // -----------------------------------------------------------------------------------------------------
    private Collection<URL> getUrls(String serviceName, String namespace) {
        String          path = ServiceUtil.buildNamespacedServiceNodePath(namespace, serviceName);
        Collection<URL> urls = getCache(path);
        if (urls != null) {
            return urls;
        }

        urls = pullURL(path);
        cacheService(serviceName, path, (List<URL>) urls);
        return urls;
    }

    private void cacheService(String serviceName, String servicePath, List<URL> nodes) {
        // 第一次缓存时注册监听器
        if (addCache(servicePath, nodes) == null) {
            // 向 zookeeper 注册监听器，保证数据的实时一致性
            zookeeper.registerPathChildrenWatcher(servicePath, event -> {
                if (event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {
                    addCache(servicePath, pullURL(servicePath));
                    eventPublisher.publishEvent(new ServicePublishedEvent(new RemoteService(serviceName, nodes)));
                }
                else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                    removeCache(servicePath);
                    String canceledPath    = event.getData().getPath();
                    String canceledAddress = canceledPath.substring(canceledPath.lastIndexOf("/") + 1);
                    ArrayList<URL> urls = new ArrayList<>();
                    urls.add(new URL(canceledAddress));
                    eventPublisher.publishEvent(new ServiceCanceledEvent(new RemoteService(serviceName, urls)));
                }
            });
        }
    }

    /**
     * 从 zookeeper 拉取最新数据
     */
    private List<URL> pullURL(String servicePath) {
        List<URL> list = new CopyOnWriteArrayList<>(); // 读多写少
        for (String s : zookeeper.getChildrenNodes(servicePath)) {
            URL url = new URL(s);
            list.add(url);
        }
        return list;
    }

    private URL selectTargetUrl(String serviceName, Collection<URL> urls, RpcRequestConfig requestConfig) {
        return determineLoadBalance(requestConfig.loadBalance())
                .select(urls, RpcInvocation.of(serviceName, requestConfig));
    }

    // -----------------------------------------------------------------------------------------------------
    // --------------------------------------    private methods    ----------------------------------------
    // -----------------------------------------------------------------------------------------------------
}
