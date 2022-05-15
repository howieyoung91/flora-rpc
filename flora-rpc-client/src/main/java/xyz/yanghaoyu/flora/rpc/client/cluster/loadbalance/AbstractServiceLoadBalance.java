/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance;

import xyz.yanghaoyu.flora.rpc.client.cluster.Invocation;
import xyz.yanghaoyu.flora.rpc.client.cluster.URL;

import java.util.List;

public abstract class AbstractServiceLoadBalance implements ServiceLoadBalance {

    @Override
    public URL select(List<URL> serverAddresses, Invocation invocation) {
        if (serverAddresses.isEmpty()) {
            return null;
        }
        if (serverAddresses.size() == 1) {
            return serverAddresses.get(0);
        }
        return doSelect(serverAddresses, invocation);
    }

    protected abstract URL doSelect(List<URL> serverAddresses, Invocation invocation);
}
