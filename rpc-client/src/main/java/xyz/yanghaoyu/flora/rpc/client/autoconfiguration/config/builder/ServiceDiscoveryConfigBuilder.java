/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.builder;

import xyz.yanghaoyu.flora.rpc.base.exception.RpcClientException;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ServiceDiscoveryConfigProperties;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ServiceDiscoveryConfigurer;
import xyz.yanghaoyu.flora.rpc.base.cluster.loadbalance.AbstractLoadBalanceService;
import xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance.DefaultLoadBalanceService;
import xyz.yanghaoyu.flora.rpc.base.cluster.loadbalance.ServiceLoadBalance;
import xyz.yanghaoyu.flora.rpc.base.config.DiscoveryConfig;

import java.util.Map;
import java.util.Objects;

public class ServiceDiscoveryConfigBuilder {
    private final ServiceDiscoveryConfigurer       configurer;
    private final ServiceDiscoveryConfigProperties properties;

    private ServiceDiscoveryConfigBuilder(ServiceDiscoveryConfigurer configurer, ServiceDiscoveryConfigProperties properties) {
        this.configurer = configurer;
        this.properties = properties;
    }

    public static ServiceDiscoveryConfigBuilder aServiceDiscoveryConfig(ServiceDiscoveryConfigurer configurer, ServiceDiscoveryConfigProperties properties) {
        return new ServiceDiscoveryConfigBuilder(configurer, properties);
    }

    public DiscoveryConfig build() {
        String                     defaultLoadBalance = getDefaultLoadBalance();
        AbstractLoadBalanceService loadBalanceService = getLoadBalanceService();
        return new DiscoveryConfig() {
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

    private AbstractLoadBalanceService getLoadBalanceService() {
        DefaultLoadBalanceService loadBalanceService = new DefaultLoadBalanceService();
        if (configurer == null) {
            return loadBalanceService;
        }

        Map<String, ServiceLoadBalance> loadBalances = configurer.addLoadBalance();
        if (loadBalances == null) {
            return loadBalanceService;
        }

        // do add load balance
        loadBalances.forEach((name, value) -> {
            if (loadBalanceService.containsLoadBalance(name)) {
                throw new RpcClientException("fail to build load balance config. Cause:load balance [" + name + "] has already existed!");
            }
            loadBalanceService.addLoadBalance(name, value);
        });
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
