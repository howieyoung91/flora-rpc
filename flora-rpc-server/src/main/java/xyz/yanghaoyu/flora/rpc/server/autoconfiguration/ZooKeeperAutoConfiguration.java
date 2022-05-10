/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.autoconfiguration;

import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;
import xyz.yanghaoyu.flora.annotation.Bean;
import xyz.yanghaoyu.flora.annotation.Configuration;
import xyz.yanghaoyu.flora.annotation.Value;
import xyz.yanghaoyu.flora.rpc.base.service.support.ZooKeeper;
import xyz.yanghaoyu.flora.rpc.base.service.support.ZooKeeperBuilder;

@Configuration
public class ZooKeeperAutoConfiguration {
    @Value(value = "${flora.rpc.server.registry.zookeeper.address}")
    private String      address;
    @Value(value = "${flora.rpc.server.registry.zookeeper.namespace}", required = false)
    private String      namespace     = "";
    @Value(value = "${flora.rpc.server.registry.zookeeper.base-sleep-time}")
    private Integer     baseSleepTime = 3000;
    @Value(value = "${flora.rpc.server.registry.zookeeper.max-retries}")
    private Integer     maxRetries    = 3;
    private RetryPolicy retryPolicy   = new ExponentialBackoffRetry(baseSleepTime, maxRetries);

    @Bean("floraRpcServer$ZooKeeper$")
    public ZooKeeper zooKeeper() {
        return ZooKeeperBuilder.aNewZooKeeper()
                .zookeeperAddress(address)
                .retryPolicy(new ExponentialBackoffRetry(baseSleepTime, maxRetries))
                .build();
    }
}
