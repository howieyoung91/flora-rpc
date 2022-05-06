/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.strategy.loadbalance;

import xyz.yanghaoyu.flora.rpc.strategy.LoadBalance;
import xyz.yanghaoyu.flora.rpc.transport.dto.Request;

import java.util.List;

public interface ServiceLoadBalance extends LoadBalance {
    String pick(List<String> serviceAddresses, Request rpcRequest);
}
