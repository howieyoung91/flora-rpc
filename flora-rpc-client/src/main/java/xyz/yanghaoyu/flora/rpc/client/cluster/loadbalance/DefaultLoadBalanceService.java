/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance;

public class DefaultLoadBalanceService extends AbstractLoadBalanceService {
    public DefaultLoadBalanceService() {
        addLoadBalance(RandomServiceLoadBalance.NAME, new RandomServiceLoadBalance());
        addLoadBalance(ConsistentHashLoadBalance.NAME, new ConsistentHashLoadBalance());
    }
}
