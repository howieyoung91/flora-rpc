/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.cluster;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class URL {
    private final String              address;
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    public URL(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public String toString() {
        return address;
    }
}
