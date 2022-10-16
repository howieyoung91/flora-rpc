/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance;

import xyz.yanghaoyu.flora.rpc.base.cluster.Invocation;
import xyz.yanghaoyu.flora.rpc.base.cluster.URL;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomServiceLoadBalance extends AbstractServiceLoadBalance {
    public static final String NAME = "RANDOM";

    @Override
    protected URL doSelect(Collection<URL> serverUrls, Invocation invocation) {
        int randomIndex = ThreadLocalRandom.current().nextInt(serverUrls.size());
        if (serverUrls instanceof List) {
            return ((List<URL>) serverUrls).get(randomIndex);
        }
        else {
            Iterator<URL> it = serverUrls.iterator();
            while (randomIndex-- != 0) {
                it.next();
            }
            return it.next();
        }
    }

    @Override
    public String name() {
        return NAME;
    }
}
