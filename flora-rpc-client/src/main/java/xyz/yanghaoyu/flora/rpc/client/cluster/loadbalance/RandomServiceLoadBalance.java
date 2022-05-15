/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance;

import xyz.yanghaoyu.flora.rpc.client.cluster.Invocation;
import xyz.yanghaoyu.flora.rpc.client.cluster.URL;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomServiceLoadBalance extends AbstractServiceLoadBalance {
    public static final String NAME = "RANDOM";
    // private static final String WEIGHT_KEY = "RANDOM_LOAD_BALANCE_WEIGHT";

    @Override
    protected URL doSelect(List<URL> serverUrls, Invocation invocation) {
        int randomIndex = ThreadLocalRandom.current().nextInt(serverUrls.size());
        return serverUrls.get(randomIndex);
    }
}
