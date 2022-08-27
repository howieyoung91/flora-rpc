/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.cluster.loadbalance;

import xyz.yanghaoyu.flora.rpc.base.cluster.Invocation;
import xyz.yanghaoyu.flora.rpc.base.cluster.URL;

import java.util.List;

public interface LoadBalanceService {
    URL select(List<URL> urls, Invocation invocation, String loadBalanceName);
}
