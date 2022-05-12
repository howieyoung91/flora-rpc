/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.config;

import xyz.yanghaoyu.flora.rpc.client.strategy.loadbalance.ServiceLoadBalance;

import java.util.Map;

public interface DiscoveryConfig {
    String getNamespace();

    Map<String, ServiceLoadBalance> getLoadBalance();
}
