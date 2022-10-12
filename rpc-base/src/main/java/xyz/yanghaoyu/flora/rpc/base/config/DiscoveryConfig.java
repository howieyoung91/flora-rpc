/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.config;

import xyz.yanghaoyu.flora.rpc.base.cluster.loadbalance.AbstractLoadBalanceService;

public interface DiscoveryConfig {
    AbstractLoadBalanceService loadBalanceService();

    String defaultLoadBalance();
}
