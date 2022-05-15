/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.autoconfiguration;

import xyz.yanghaoyu.flora.annotation.Bean;
import xyz.yanghaoyu.flora.annotation.Configuration;
import xyz.yanghaoyu.flora.annotation.Enable;
import xyz.yanghaoyu.flora.annotation.Inject;
import xyz.yanghaoyu.flora.rpc.base.service.zookeeper.ZooKeeper;
import xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.ServiceRegistryConfigurer;
import xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.ServiceRegistryProperties;
import xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.ZooKeeperConfigProperties;
import xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.ZooKeeperConfigurer;
import xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.builder.ServiceRegistryConfigBuilder;
import xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.builder.ZooKeeperBuilderFactory;
import xyz.yanghaoyu.flora.rpc.server.config.RegistryConfig;
import xyz.yanghaoyu.flora.rpc.server.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.server.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.server.service.support.DefaultServiceHandler;
import xyz.yanghaoyu.flora.rpc.server.service.support.ZooKeeperServiceRegistry;

@Configuration
@Enable.ComponentScan(basePackages = "xyz.yanghaoyu.flora.rpc.server.autoconfiguration")
@Enable.PropertySource(location = "classpath:flora-rpc-server.yaml")
public class FloraRpcServerAutoConfiguration {

    /**
     * zookeeper
     */
    @Bean("floraRpcServer$ZooKeeper$")
    public ZooKeeper zooKeeper(
            @Inject.ByType(required = false) ZooKeeperConfigurer configurer,
            @Inject.ByName(ZooKeeperConfigProperties.BEAN_NAME) ZooKeeperConfigProperties properties
    ) {
        return ZooKeeperBuilderFactory
                .aZooKeeperConfigBuilder(configurer, properties)
                .build().build();
    }

    @Bean("flora-rpc-server$ServiceRegistryConfiguration$")
    public RegistryConfig registryConfig(
            @Inject.ByType(required = false)
                    ServiceRegistryConfigurer configurer,
            @Inject.ByName(ServiceRegistryProperties.BEAN_NAME)
                    ServiceRegistryProperties properties
    ) {
        return ServiceRegistryConfigBuilder.aServiceRegistryConfiguration(configurer, properties).build();
    }


    /**
     * service registry 配置
     */
    @Bean("floraRpcServer$ServiceRegistry$")
    public ServiceRegistry registry(
            @Inject.ByName("flora-rpc-server$ServiceRegistryConfiguration$")
                    RegistryConfig registryConfig,
            @Inject.ByName("floraRpcServer$ZooKeeper$")
                    ZooKeeper zooKeeper
    ) {
        return new ZooKeeperServiceRegistry(registryConfig, zooKeeper);
    }

    /**
     * service handler 配置
     */
    @Bean("floraRpcServer$ServiceHandler$")
    public ServiceHandler handler(
            @Inject.ByName("floraRpcServer$ServiceRegistry$")
                    ServiceRegistry registry
    ) {
        return new DefaultServiceHandler(registry);
    }
}
