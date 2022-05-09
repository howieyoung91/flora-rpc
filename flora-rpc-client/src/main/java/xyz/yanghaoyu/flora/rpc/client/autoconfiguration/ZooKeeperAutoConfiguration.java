/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration;

import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;
import xyz.yanghaoyu.flora.annotation.Bean;
import xyz.yanghaoyu.flora.annotation.Configuration;
import xyz.yanghaoyu.flora.annotation.Value;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.base.service.support.ZooKeeper;
import xyz.yanghaoyu.flora.rpc.base.service.support.ZooKeeperBuilder;
import xyz.yanghaoyu.flora.rpc.base.service.support.ZookeeperServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.base.strategy.loadbalance.RandomServiceLoadBalance;

@Configuration
public class ZooKeeperAutoConfiguration {
    @Value(value = "${flora.rpc.client.discovery.zookeeper.address}")
    private String      address;
    @Value(value = "${flora.rpc.client.discovery.zookeeper.namespace}",
            required = false
    )
    private String      namespace     = "";
    @Value(value = "${flora.rpc.client.discovery.zookeeper.base-sleep-time}")
    private Integer     baseSleepTime = 3000;
    @Value(value = "${flora.rpc.client.discovery.zookeeper.max-retries}")
    private Integer     maxRetries    = 3;
    private RetryPolicy retryPolicy   = new ExponentialBackoffRetry(baseSleepTime, maxRetries);

    @Bean("clientZooKeeper")
    public ZooKeeper zooKeeper() {
        return ZooKeeperBuilder.aZooKeeper()
                .zookeeperAddress(address)
                .baseSleepTime(baseSleepTime)
                .maxRetries(maxRetries)
                .namespace(namespace)
                .retryPolicy(retryPolicy)
                .build();
    }

    @Bean("clientServiceHandler")
    public ServiceDiscovery handler() {
        return new ZookeeperServiceDiscovery(zooKeeper(), new RandomServiceLoadBalance());
    }
}
