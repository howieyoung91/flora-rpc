/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service.discovery;

import xyz.yanghaoyu.flora.rpc.base.cluster.loadbalance.ServiceLoadBalance;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;

public interface LoadBalanceSupportServiceDiscovery extends ServiceDiscovery {
    ServiceLoadBalance getDefaultLoadBalance();

    boolean supports(String loadBalanceName);
}
