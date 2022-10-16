/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config;


import xyz.yanghaoyu.flora.rpc.base.cluster.loadbalance.ServiceLoadBalance;

import java.util.Collection;

public interface ServiceDiscoveryConfigurer {
    default Collection<ServiceLoadBalance> addLoadBalance() {
        return null;
    }

    default String defaultLoadBalance() {
        return null;
    }
}


