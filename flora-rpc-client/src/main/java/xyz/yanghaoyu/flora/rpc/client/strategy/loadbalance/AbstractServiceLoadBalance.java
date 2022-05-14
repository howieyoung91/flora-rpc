/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.strategy.loadbalance;

import xyz.yanghaoyu.flora.rpc.client.annotation.ServiceReferenceAttribute;

import java.util.List;

public abstract class AbstractServiceLoadBalance implements ServiceLoadBalance {
    @Override
    public String select(ServiceReferenceAttribute serviceConfig, List<String> serviceAddresses) {
        if (serviceAddresses.isEmpty()) {
            return null;
        }
        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }
        return doSelect(serviceConfig, serviceAddresses);
    }

    protected abstract String doSelect(ServiceReferenceAttribute serviceConfig, List<String> serviceAddresses);
}
