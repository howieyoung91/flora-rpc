/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.cluster.loadbalance;

import xyz.yanghaoyu.flora.rpc.base.cluster.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractLoadBalanceService
        implements LoadBalanceService, LoadBalanceRegistry, LoadBalanceFactory {
    private       Map<String, ServiceLoadBalance> loadBalances = new ConcurrentHashMap<>();

    @Override
    public URL select(List<URL> urls, Invocation invocation, String loadBalanceName) {
        return getLoadBalanceNonNull(loadBalanceName).select(urls, invocation);
    }

    @Override
    public ServiceLoadBalance getLoadBalance(String name) {
        return loadBalances.get(name);
    }

    private ServiceLoadBalance getLoadBalanceNonNull(String name) {
        ServiceLoadBalance loadBalance = getLoadBalance(name);
        Objects.requireNonNull(loadBalance, "unknown load balance [" + name + "]");
        return loadBalance;
    }

    @Override
    public void addLoadBalance(String name, ServiceLoadBalance loadBalance) {
        loadBalances.put(name, loadBalance);
    }

    @Override
    public boolean containsLoadBalance(String name) {
        return loadBalances.containsKey(name);
    }
}
