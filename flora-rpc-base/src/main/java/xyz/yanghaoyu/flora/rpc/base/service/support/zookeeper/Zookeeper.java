/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service.support.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class Zookeeper {
    private static final Logger logger = LoggerFactory.getLogger(Zookeeper.class);

    private final    String           zookeeperAddress;
    private final    RetryPolicy      retryPolicy;
    private volatile CuratorFramework zookeeperClient;

    Zookeeper(String zookeeperAddress, RetryPolicy retryPolicy) {
        this.zookeeperAddress = zookeeperAddress;
        this.retryPolicy = retryPolicy;
        getClient();
    }

    // ========================================   public methods   =========================================

    public void createPersistentNode(String path) {
        try {
            if (!canCreateNode(path)) {
                logger.warn("fail to create persistent node: {} cause: {}", path, "this node already exists!");
            }

            zookeeperClient.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT).forPath(path);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.warn("fail to create persistent node: {}", path);
        }
    }

    public void deletePersistentNode(String path) {
        try {
            zookeeperClient.delete().forPath(path);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.warn("fail to delete persistent node: {}", path);
        }
    }

    public List<String> getChildrenNodes(String path) {
        List<String> result = doGetChildrenNodes(path, getClient());
        return result == null ? new ArrayList<>(0) : result;
    }

    public void registerPathChildrenWatcher(String path, Consumer<PathChildrenCacheEvent> consumer) {
        PathChildrenCache pathChildrenCache =
                new PathChildrenCache(getClient(), path, true);
        pathChildrenCache.getListenable().addListener((curator, event) -> consumer.accept(event));

        try {
            pathChildrenCache.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========================================   public methods   =========================================


    // -----------------------------------------------------------------------------------------------------
    // --------------------------------------    private methods    ----------------------------------------
    // -----------------------------------------------------------------------------------------------------
    private boolean canCreateNode(String path) throws Exception {
        return zookeeperClient.checkExists().forPath(path) == null;
    }

    private List<String> doGetChildrenNodes(String servicePath, CuratorFramework client) {
        try {
            return client.getChildren().forPath(servicePath);
        }
        catch (Exception e) {
            e.printStackTrace();
            // throw new ServiceException("fail to get persistent node: " + servicePath);
        }
        return new ArrayList<>(0);
    }


    private CuratorFramework getClient() {
        // double check
        if (zookeeperClient == null) {
            synchronized (Zookeeper.class) {
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
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zookeeperClient;
    }

    // -----------------------------------------------------------------------------------------------------
    // --------------------------------------    private methods    ----------------------------------------
    // -----------------------------------------------------------------------------------------------------
}
