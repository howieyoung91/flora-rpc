/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.builder;

import xyz.yanghaoyu.flora.rpc.base.exception.RpcClientException;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ServiceDiscoveryConfigProperties;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ServiceDiscoveryConfigurer;
import xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance.AbstractLoadBalanceService;
import xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance.DefaultLoadBalanceService;
import xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance.ServiceLoadBalance;
import xyz.yanghaoyu.flora.rpc.client.config.DiscoveryConfig;

import java.util.Map;
import java.util.Objects;

public class ServiceDiscoveryConfigBuilder {
    private final ServiceDiscoveryConfigurer       configurer;
    private final ServiceDiscoveryConfigProperties properties;

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
        String                     namespace          = getNamespace();
        String                     defaultLoadBalance = getDefaultLoadBalance();
        AbstractLoadBalanceService loadBalanceService = getLoadBalanceService();
        return new DiscoveryConfig() {
            @Override
            public String namespace() {
                return namespace;
            }

            @Override
            public AbstractLoadBalanceService loadBalanceService() {
                return loadBalanceService;
            }

            @Override
            public String defaultLoadBalance() {
                return defaultLoadBalance;
            }
        };
    }

    private String getNamespace() {
        String namespace = properties.getNamespace();
        if (configurer != null) {
            String namespaceByConfigurer = configurer.namespace();
            if (namespaceByConfigurer != null) {
                namespace = namespaceByConfigurer;
            }
        }

        Objects.requireNonNull(namespace, "found no namespace");
        return namespace;
    }


    private AbstractLoadBalanceService getLoadBalanceService() {
        DefaultLoadBalanceService loadBalanceService = new DefaultLoadBalanceService();
        if (configurer != null) {
            Map<String, ServiceLoadBalance> loadBalances = configurer.addLoadBalance();
            if (loadBalances != null) {
                for (Map.Entry<String, ServiceLoadBalance> entry : loadBalances.entrySet()) {
                    String name = entry.getKey();
                    if (loadBalanceService.containsLoadBalance(name)) {
                        throw new RpcClientException("fail to build load balance config. cause: load balance [" + name + "] has already existed!");
                    }
                    loadBalanceService.addLoadBalance(name, entry.getValue());
                }
            }
        }
        return loadBalanceService;
    }

    private String getDefaultLoadBalance() {
        String loadBalance = properties.getLoadBalance();
        if (configurer != null) {
            String loadBalanceByConfigurer = configurer.defaultLoadBalance();
            if (loadBalanceByConfigurer != null) {
                loadBalance = loadBalanceByConfigurer;
            }
        }
        Objects.requireNonNull(loadBalance, "found no load balance");
        return loadBalance;
    }
}
