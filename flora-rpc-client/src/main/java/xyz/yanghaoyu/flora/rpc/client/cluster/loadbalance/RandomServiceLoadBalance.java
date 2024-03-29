/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance;

import xyz.yanghaoyu.flora.rpc.base.cluster.Invocation;
import xyz.yanghaoyu.flora.rpc.base.cluster.URL;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomServiceLoadBalance extends AbstractServiceLoadBalance {
    public static final String NAME = "RANDOM";

    @Override
    protected URL doSelect(List<URL> serverUrls, Invocation invocation) {
        int randomIndex = ThreadLocalRandom.current().nextInt(serverUrls.size());
        return serverUrls.get(randomIndex);
    }
}
