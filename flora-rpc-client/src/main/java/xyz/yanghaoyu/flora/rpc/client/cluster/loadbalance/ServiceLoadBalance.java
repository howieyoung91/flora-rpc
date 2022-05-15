/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance;


import xyz.yanghaoyu.flora.rpc.base.strategy.LoadBalance;
import xyz.yanghaoyu.flora.rpc.client.cluster.Invocation;
import xyz.yanghaoyu.flora.rpc.client.cluster.URL;

import java.util.List;

public interface ServiceLoadBalance extends LoadBalance {
    URL select(List<URL> serverAddresses, Invocation invocation);
}
