/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service.discovery;

import xyz.yanghaoyu.flora.rpc.base.cluster.URL;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CacheableServiceDiscovery implements ServiceDiscovery {
    protected final Map<String, Collection<URL>> urlCache = new ConcurrentHashMap<>();

    protected Map<String, Collection<URL>> getCache() {
        return urlCache;
    }

    protected Collection<URL> getCache(String s) {
        return getCache().get(s);
    }

    protected Collection<URL> removeCache(String s) {
        return getCache().remove(s);
    }

    protected Collection<URL> addCache(String s, Collection<URL> urls) {
        return getCache().put(s, urls);
    }
}
