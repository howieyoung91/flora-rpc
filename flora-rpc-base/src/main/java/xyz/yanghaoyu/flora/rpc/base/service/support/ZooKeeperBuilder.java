/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service.support;

import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;

public final class ZooKeeperBuilder {
    private int         baseSleepTime;
    private int         maxRetries;
    private String      zookeeperAddress;
    private String      namespace;
    private RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTime, maxRetries);

    private ZooKeeperBuilder() {
    }

    public static ZooKeeperBuilder aZooKeeper() {
        return new ZooKeeperBuilder();
    }

    public ZooKeeperBuilder baseSleepTime(int baseSleepTime) {
        this.baseSleepTime = baseSleepTime;
        return this;
    }

    public ZooKeeperBuilder maxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public ZooKeeperBuilder zookeeperAddress(String zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;
        return this;
    }

    public ZooKeeperBuilder namespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public ZooKeeperBuilder retryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }

    public ZooKeeper build() {
        return new ZooKeeper(baseSleepTime, maxRetries, zookeeperAddress, namespace, retryPolicy);
    }
}
