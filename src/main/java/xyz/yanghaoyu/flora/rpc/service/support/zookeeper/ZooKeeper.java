/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.service.support.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.config.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class ZooKeeper {
    private static final Logger logger = LoggerFactory.getLogger(ZooKeeper.class);

    public ZooKeeper(Configuration configuration) {
        baseSleepTime = ((Integer) configuration.get("base-sleep-time"));
        maxRetries = ((Integer) configuration.get("max-retries"));
        zookeeperAddress = ((String) configuration.get("zookeeper-address"));
        namespace = ((String) configuration.get("namespace"));
        // 先把重试策略写死
        retryPolicy = new ExponentialBackoffRetry(baseSleepTime, maxRetries);
    }

    private final    int              baseSleepTime;
    private final    int              maxRetries;
    private final    String           zookeeperAddress;
    private final    String           namespace;
    private final    RetryPolicy      retryPolicy;
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
            result = zookeeperClient.getChildren().forPath(servicePath);
        } catch (Exception e) {
            logger.error("fail to get children nodes: {}", servicePath);
        }
        return result == null ? new ArrayList<>(0) : result;
    }

    private CuratorFramework client() {
        // double check
        if (zookeeperClient != null) {
            synchronized (ZooKeeper.class) {
                if (zookeeperClient != null) {
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
}
