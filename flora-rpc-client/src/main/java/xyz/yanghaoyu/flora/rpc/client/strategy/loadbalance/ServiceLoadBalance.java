/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.strategy.loadbalance;


import xyz.yanghaoyu.flora.rpc.base.service.config.ServiceReferenceConfig;
import xyz.yanghaoyu.flora.rpc.base.strategy.LoadBalance;

import java.util.List;

public interface ServiceLoadBalance extends LoadBalance {
    String select(ServiceReferenceConfig serviceConfig, List<String> serviceAddresses);
}