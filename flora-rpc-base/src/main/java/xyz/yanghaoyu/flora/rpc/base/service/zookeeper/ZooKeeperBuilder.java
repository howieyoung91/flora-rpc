/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service.zookeeper;

import org.apache.curator.RetryPolicy;

public final class ZooKeeperBuilder {
    private String      zookeeperAddress;
    private RetryPolicy retryPolicy;

    private ZooKeeperBuilder() {
    }

    public static ZooKeeperBuilder aNewZooKeeper() {
        return new ZooKeeperBuilder();
    }

    public ZooKeeperBuilder zookeeperAddress(String zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;
        return this;
    }

    public ZooKeeperBuilder retryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }

    public ZooKeeper build() {
        return new ZooKeeper(zookeeperAddress, retryPolicy);
    }
}
