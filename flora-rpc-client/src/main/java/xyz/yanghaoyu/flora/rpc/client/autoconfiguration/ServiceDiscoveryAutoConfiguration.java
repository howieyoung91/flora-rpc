/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration;

import xyz.yanghaoyu.flora.annotation.Bean;
import xyz.yanghaoyu.flora.annotation.Configuration;
import xyz.yanghaoyu.flora.annotation.Enable;
import xyz.yanghaoyu.flora.annotation.Inject;
import xyz.yanghaoyu.flora.rpc.base.service.zookeeper.ZooKeeper;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ServiceDiscoveryConfigProperties;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ServiceDiscoveryConfigurer;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ZooKeeperConfigProperties;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ZooKeeperConfigurer;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.builder.ServiceDiscoveryConfigBuilder;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.builder.ZooKeeperBuilderFactory;
import xyz.yanghaoyu.flora.rpc.client.config.DiscoveryConfig;
import xyz.yanghaoyu.flora.rpc.client.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.client.service.ZookeeperServiceDiscovery;

@Configuration(ServiceDiscoveryAutoConfiguration.BEAN_NAME)
@Enable.ComponentScan(basePackages = "xyz.yanghaoyu.flora.rpc.client.autoconfiguration")
public class ServiceDiscoveryAutoConfiguration {
    public static final String BEAN_NAME = "flora-rpc-client$ServiceDiscoveryAutoConfiguration$";

    @Bean("flora-rpc-client$ZooKeeper$")
    public ZooKeeper zooKeeper(
            @Inject.ByType(required = false)
                    ZooKeeperConfigurer configurer,
            @Inject.ByName(ZooKeeperConfigProperties.BEAN_NAME)
                    ZooKeeperConfigProperties properties
    ) {
        return ZooKeeperBuilderFactory.aZooKeeperConfigBuilder(configurer, properties)
                .build().build();
    }

    @Bean("flora-rpc-client$ServiceDiscoveryConfig$")
    public DiscoveryConfig discoveryConfig(
            @Inject.ByType(required = false)
                    ServiceDiscoveryConfigurer configurer,
            @Inject.ByName(ServiceDiscoveryConfigProperties.BEAN_NAME)
                    ServiceDiscoveryConfigProperties properties
    ) {
        return ServiceDiscoveryConfigBuilder
                .aServiceDiscoveryConfig(configurer, properties)
                .build();
    }

    @Bean("flora-rpc-client$ServiceDiscovery$")
    public ServiceDiscovery discovery(
            @Inject.ByName("flora-rpc-client$ServiceDiscoveryConfig$")
                    DiscoveryConfig discoveryConfig,
            @Inject.ByName("flora-rpc-client$ZooKeeper$")
                    ZooKeeper zooKeeper
    ) {
        return new ZookeeperServiceDiscovery(discoveryConfig, zooKeeper);
    }

}
