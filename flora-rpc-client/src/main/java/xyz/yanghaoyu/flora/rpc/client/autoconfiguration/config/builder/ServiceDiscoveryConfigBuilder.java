/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.builder;

import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ServiceDiscoveryConfigProperties;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ServiceDiscoveryConfigurer;
import xyz.yanghaoyu.flora.rpc.client.config.DiscoveryConfig;
import xyz.yanghaoyu.flora.rpc.client.strategy.loadbalance.ServiceLoadBalance;

import java.util.Map;

public class ServiceDiscoveryConfigBuilder {
    private ServiceDiscoveryConfigurer       configurer;
    private ServiceDiscoveryConfigProperties properties;

    private ServiceDiscoveryConfigBuilder(
            ServiceDiscoveryConfigurer configurer,
            ServiceDiscoveryConfigProperties properties
    ) {
        this.configurer = configurer;
        this.properties = properties;
    }

    public static ServiceDiscoveryConfigBuilder aServiceDiscoveryConfig(ServiceDiscoveryConfigurer configurer, ServiceDiscoveryConfigProperties properties) {
        return new ServiceDiscoveryConfigBuilder(configurer, properties);
    }


    public DiscoveryConfig build() {
        String namespace = determineNamespace();
        return new DiscoveryConfig() {
            @Override
            public String getNamespace() {
                return namespace;
            }

            @Override
            public Map<String, ServiceLoadBalance> getLoadBalance() {
                return null;
            }
        };
    }

    private String determineNamespace() {
        String namespace = properties.getNamespace();
        if (configurer != null) {
            String namespaceByConfigurer = configurer.namespace();
            if (namespaceByConfigurer != null) {
                namespace = namespaceByConfigurer;
            }
        }

        if (namespace == null) {
            namespace = "/";
        }
        return namespace;
    }
}
