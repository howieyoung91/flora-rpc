/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service.support;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class ZooKeeper {
    private static final Logger logger = LoggerFactory.getLogger(ZooKeeper.class);

    public ZooKeeper(int baseSleepTime, int maxRetries, String zookeeperAddress, String namespace, RetryPolicy retryPolicy) {
        this.baseSleepTime = baseSleepTime;
        this.maxRetries = maxRetries;
        this.zookeeperAddress = zookeeperAddress;
        this.namespace = namespace;
        this.retryPolicy = retryPolicy;
        client();
    }

    private          int              baseSleepTime;
    private          int              maxRetries;
    private          String           zookeeperAddress;
    private          String           namespace;
    private          RetryPolicy      retryPolicy = new ExponentialBackoffRetry(baseSleepTime, maxRetries);
    private volatile CuratorFramework zookeeperClient;

    public void createPersistentNode(String path) {
        try {
            if (!canCreateNode(path)) {
                logger.warn(
                        "fail to create persistent node: {} cause: {}",
                        path, "this node already exists!"
                );
            }

            zookeeperClient.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("fail to create persistent node: {}", path);
        }
    }

    private boolean canCreateNode(String path) throws Exception {
        return zookeeperClient.checkExists().forPath(path) == null;
    }

    public List<String> getChildrenNodes(String serviceName) {
        List<String> result      = null;
        String       servicePath = namespace + "/" + serviceName;
        try {
            result = client().getChildren().forPath(servicePath);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("fail to get children nodes: {}", servicePath);
        }
        return result == null ? new ArrayList<>(0) : result;
    }

    private CuratorFramework client() {
        // double check
        if (zookeeperClient == null) {
            synchronized (ZooKeeper.class) {
                if (zookeeperClient == null) {
                    return newCuratorClient();
                }
            }
        }

        return zookeeperClient;
    }

    private CuratorFramework newCuratorClient() {
        zookeeperClient = CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddress)
                .retryPolicy(retryPolicy)
                .build();
        zookeeperClient.start();
        try {
            if (!zookeeperClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Time out waiting to connect to zookeeper");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zookeeperClient;
    }

    private void registerWatcher(String rpcServiceName, CuratorFramework zkClient) throws Exception {
        String servicePath = namespace + "/" + rpcServiceName;
        PathChildrenCache pathChildrenCache
                = new PathChildrenCache(zkClient, servicePath, true);
        pathChildrenCache.getListenable().addListener(
                (curatorFramework, pathChildrenCacheEvent) -> {
                    List<String> serviceAddresses =
                            curatorFramework.getChildren().forPath(servicePath);
                    // SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddresses);
                });
        pathChildrenCache.start();
    }
}
