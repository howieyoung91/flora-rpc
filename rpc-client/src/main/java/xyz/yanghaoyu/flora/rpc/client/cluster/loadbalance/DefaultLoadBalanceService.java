/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance;

import xyz.yanghaoyu.flora.rpc.base.cluster.loadbalance.AbstractLoadBalanceService;

public class DefaultLoadBalanceService extends AbstractLoadBalanceService {
    public DefaultLoadBalanceService() {
        addLoadBalance(RandomServiceLoadBalance.NAME, new RandomServiceLoadBalance());
        addLoadBalance(ConsistentHashLoadBalance.NAME, new ConsistentHashLoadBalance());
    }
}
