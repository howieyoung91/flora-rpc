/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.strategy.loadbalance;

import xyz.yanghaoyu.flora.rpc.transport.dto.Request;

import java.util.List;

public abstract class AbstractServiceLoadBalance implements ServiceLoadBalance {
    @Override
    public String pick(List<String> serviceAddresses, Request request) {
        if (serviceAddresses.isEmpty()) {
            return null;
        }
        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses, request);
    }

    protected abstract String doSelect(List<String> serviceAddresses, Request request);
}
