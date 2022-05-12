/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.autoconfiguration;

import xyz.yanghaoyu.flora.annotation.Bean;
import xyz.yanghaoyu.flora.annotation.Configuration;
import xyz.yanghaoyu.flora.annotation.Enable;
import xyz.yanghaoyu.flora.annotation.Inject;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.server.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.base.service.support.ZooKeeper;
import xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.ZooKeeperConfigProperties;
import xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.ZooKeeperConfigurer;
import xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.builder.ZooKeeperBuilderFactory;
import xyz.yanghaoyu.flora.rpc.server.service.DefaultServiceHandler;
import xyz.yanghaoyu.flora.rpc.server.service.ZooKeeperServiceRegistry;

@Configuration
@Enable.ComponentScan(basePackages = "xyz.yanghaoyu.flora.rpc.server.autoconfiguration")
public class FloraRpcServerAutoConfiguration {
    @Bean("floraRpcServer$ZooKeeper$")
    public ZooKeeper zooKeeper(
            @Inject.ByType(required = false) ZooKeeperConfigurer configurer,
            @Inject.ByName(ZooKeeperConfigProperties.BEAN_NAME) ZooKeeperConfigProperties properties
    ) {
        return ZooKeeperBuilderFactory
                .aZooKeeperConfigBuilder(configurer, properties)
                .build().build();
    }

    @Bean("floraRpcServer$ServiceRegistry$")
    public ServiceRegistry registry(
            @Inject.ByName("floraRpcServer$ZooKeeper$") ZooKeeper zooKeeper
    ) {
        return new ZooKeeperServiceRegistry(zooKeeper);
    }

    @Bean("floraRpcServer$ServiceHandler$")
    public ServiceHandler handler(
            @Inject.ByName("floraRpcServer$ServiceRegistry$")
                    ServiceRegistry registry
    ) {
        return new DefaultServiceHandler(registry);
    }
}
