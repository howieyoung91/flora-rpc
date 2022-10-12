/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service.discovery;

import xyz.yanghaoyu.flora.rpc.base.cluster.loadbalance.LoadBalanceFactory;
import xyz.yanghaoyu.flora.rpc.base.cluster.loadbalance.LoadBalanceRegistry;
import xyz.yanghaoyu.flora.rpc.base.cluster.loadbalance.ServiceLoadBalance;

import java.util.Objects;

/**
 * 对负载均衡做实现
 */
public abstract class AbstractServiceDiscovery
        extends CacheableServiceDiscovery implements LoadBalanceSupportServiceDiscovery {
    protected LoadBalanceFactory  loadBalanceFactory;
    protected LoadBalanceRegistry loadBalanceRegistry;
    protected String              defaultLoadBalanceName;
    protected ServiceLoadBalance  defaultLoadBalance;

    public AbstractServiceDiscovery(LoadBalanceFactory loadBalanceFactory, LoadBalanceRegistry loadBalanceRegistry,
                                    String defaultLoadBalanceName, ServiceLoadBalance defaultLoadBalance) {
        this.loadBalanceFactory = loadBalanceFactory;
        this.loadBalanceRegistry = loadBalanceRegistry;
        this.defaultLoadBalanceName = defaultLoadBalanceName;
        Objects.requireNonNull(defaultLoadBalance, "unknown default load balance [" + defaultLoadBalanceName + "]");
        this.defaultLoadBalance = defaultLoadBalance;
    }

    protected ServiceLoadBalance determineLoadBalance(String loadBalanceName) {
        return loadBalanceName == null ?
                getDefaultLoadBalance() : loadBalanceFactory.getLoadBalance(loadBalanceName);
    }

    @Override
    public ServiceLoadBalance getDefaultLoadBalance() {
        return defaultLoadBalance;
    }

    @Override
    public boolean supports(String loadBalanceName) {
        return Objects.equals(loadBalanceName, defaultLoadBalanceName)
               || loadBalanceRegistry.containsLoadBalance(loadBalanceName);
    }
}
