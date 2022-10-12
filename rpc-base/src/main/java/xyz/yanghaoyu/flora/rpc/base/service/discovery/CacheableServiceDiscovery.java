/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service.discovery;

import xyz.yanghaoyu.flora.rpc.base.cluster.URL;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CacheableServiceDiscovery implements ServiceDiscovery {
    protected final Map<String, List<URL>> urlCache = new ConcurrentHashMap<>();

    protected Map<String, List<URL>> getCache() {
        return urlCache;
    }
}
