/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.builder;

import xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.ServiceRegistryConfigurer;
import xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.ServiceRegistryProperties;
import xyz.yanghaoyu.flora.rpc.server.config.RegistryConfig;

public class ServiceRegistryConfigBuilder {
    private ServiceRegistryConfigurer configurer;
    private ServiceRegistryProperties properties;

    private ServiceRegistryConfigBuilder(ServiceRegistryConfigurer configurer, ServiceRegistryProperties properties) {
        this.configurer = configurer;
        this.properties = properties;
    }

    public static ServiceRegistryConfigBuilder aServiceRegistryConfiguration(ServiceRegistryConfigurer configurer, ServiceRegistryProperties properties) {
        return new ServiceRegistryConfigBuilder(configurer, properties);
    }

    public RegistryConfig build() {
        return new RegistryConfig() {
        };
    }
}
