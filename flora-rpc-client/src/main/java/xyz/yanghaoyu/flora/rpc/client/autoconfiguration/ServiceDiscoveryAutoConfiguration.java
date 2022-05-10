/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration;

import org.apache.curator.retry.ExponentialBackoffRetry;
import xyz.yanghaoyu.flora.annotation.Bean;
import xyz.yanghaoyu.flora.annotation.Configuration;
import xyz.yanghaoyu.flora.annotation.Value;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceDiscoveryException;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.base.service.support.ZooKeeper;
import xyz.yanghaoyu.flora.rpc.base.service.support.ZooKeeperBuilder;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcServiceReference;
import xyz.yanghaoyu.flora.rpc.client.discovery.ZookeeperServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.client.discovery.config.DefaultDiscoveryConfig;
import xyz.yanghaoyu.flora.rpc.client.strategy.loadbalance.RandomServiceLoadBalance;
import xyz.yanghaoyu.flora.rpc.client.strategy.loadbalance.ServiceLoadBalance;

import java.util.Objects;

@Configuration
public class ServiceDiscoveryAutoConfiguration {
    @Value("${flora.rpc.client.discovery.zookeeper.address}")
    private String  zooKeeperAddress;
    @Value("${flora.rpc.client.discovery.zookeeper.base-sleep-time}")
    private Integer baseSleepTime;
    @Value("${flora.rpc.client.discovery.zookeeper.max-retries}")
    private Integer maxRetries;

    @Value("${flora.rpc.client.discovery.zookeeper.namespace}")
    private String namespace;
    @Value("${flora.rpc.client.discovery.zookeeper.load-balance}")
    private String loadBalance;

    @Bean
    public DefaultDiscoveryConfig discoveryConfig() {
        DefaultDiscoveryConfig config = new DefaultDiscoveryConfig();
        if (Objects.equals(namespace, RpcServiceReference.EMPTY_NAMESPACE)) {
            throw new ServiceDiscoveryException("the discovery namespace is blank! it should start with /");
        }
        if (namespace.charAt(0) != '/') {
            throw new ServiceDiscoveryException("discovery namespace should start with /");
        }
        config.setNamespace(namespace);
        config.setLoadBalance(loadBalance);
        return config;
    }

    @Bean("floraRpcClient$ServiceDiscovery$")
    public ServiceDiscovery discovery() {
        ServiceLoadBalance loadBalance;
        switch (this.loadBalance) {
            case "RANDOM": {
                loadBalance = new RandomServiceLoadBalance();
                break;
            }
            default: {
                throw new ServiceDiscoveryException("unknown load balance strategy");
            }
        }

        return new ZookeeperServiceDiscovery(discoveryConfig(), zooKeeper(), loadBalance);
    }

    @Bean("floraRpcClient$ZooKeeper$")
    public ZooKeeper zooKeeper() {
        return ZooKeeperBuilder.aNewZooKeeper()
                .zookeeperAddress(zooKeeperAddress)
                .retryPolicy(new ExponentialBackoffRetry(baseSleepTime, maxRetries))
                .build();
    }


}
