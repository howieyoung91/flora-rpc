/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.config;

import xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance.AbstractLoadBalanceService;

public interface DiscoveryConfig {
    String namespace();

    AbstractLoadBalanceService loadBalanceService();

    String defaultLoadBalance();
}
