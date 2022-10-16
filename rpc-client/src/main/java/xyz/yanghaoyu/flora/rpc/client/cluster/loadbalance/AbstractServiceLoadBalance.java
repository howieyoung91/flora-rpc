/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance;

import xyz.yanghaoyu.flora.rpc.base.cluster.Invocation;
import xyz.yanghaoyu.flora.rpc.base.cluster.URL;
import xyz.yanghaoyu.flora.rpc.base.cluster.loadbalance.ServiceLoadBalance;

import java.util.Collection;

public abstract class AbstractServiceLoadBalance implements ServiceLoadBalance {
    @Override
    public URL select(Collection<URL> serverAddresses, Invocation invocation) {
        if (serverAddresses.isEmpty()) {
            return null;
        }
        if (serverAddresses.size() == 1) {
            return serverAddresses.iterator().next();
        }
        return doSelect(serverAddresses, invocation);
    }

    protected abstract URL doSelect(Collection<URL> serverAddresses, Invocation invocation);
}
