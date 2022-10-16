/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.builder;

import xyz.yanghaoyu.flora.framework.core.beans.factory.ConfigurableListableBeanFactory;
import xyz.yanghaoyu.flora.framework.core.beans.factory.support.DefaultListableBeanFactory;
import xyz.yanghaoyu.flora.rpc.base.cluster.loadbalance.AbstractLoadBalanceService;
import xyz.yanghaoyu.flora.rpc.base.cluster.loadbalance.ServiceLoadBalance;
import xyz.yanghaoyu.flora.rpc.base.config.DiscoveryConfig;
import xyz.yanghaoyu.flora.rpc.base.exception.RpcClientException;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ServiceDiscoveryConfigProperties;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ServiceDiscoveryConfigurer;
import xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance.DefaultLoadBalanceService;

import java.util.Collection;
import java.util.Objects;

public class ServiceDiscoveryConfigBuilder {
    private final ConfigurableListableBeanFactory  beanFactory;
    private final ServiceDiscoveryConfigurer       configurer;
    private final ServiceDiscoveryConfigProperties properties;

    private ServiceDiscoveryConfigBuilder(ConfigurableListableBeanFactory beanFactory, ServiceDiscoveryConfigurer configurer, ServiceDiscoveryConfigProperties properties) {
        this.beanFactory = beanFactory;
        this.configurer = configurer;
        this.properties = properties;
    }

    public static ServiceDiscoveryConfigBuilder aServiceDiscoveryConfig(ConfigurableListableBeanFactory beanFactory, ServiceDiscoveryConfigurer configurer, ServiceDiscoveryConfigProperties properties) {
        return new ServiceDiscoveryConfigBuilder(beanFactory, configurer, properties);
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
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) this.beanFactory;

        // register default load balances
        // beanFactory.registerBeanDefinition("flora-rpc-client$ConsistentHashLoadBalance$", new BeanDefinition(ConsistentHashLoadBalance.class));
        // beanFactory.registerBeanDefinition("flora-rpc-client$RandomServiceLoadBalance$", new BeanDefinition(RandomServiceLoadBalance.class));

        DefaultLoadBalanceService loadBalanceService = new DefaultLoadBalanceService();
        // add load balances
        Collection<ServiceLoadBalance> loadBalances = beanFactory.getBeansOfType(ServiceLoadBalance.class).values();
        for (ServiceLoadBalance loadBalance : loadBalances) {
            String name = loadBalance.name();
            if (loadBalanceService.containsLoadBalance(name)) {
                throw new RpcClientException("Fail to build load balance config. Cause:load balance [" + name + "] has already existed!");
            }
            loadBalanceService.addLoadBalance(name, loadBalance);
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
