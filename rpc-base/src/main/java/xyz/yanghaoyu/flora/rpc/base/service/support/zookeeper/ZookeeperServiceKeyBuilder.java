/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service.support.zookeeper;

import xyz.yanghaoyu.flora.rpc.base.service.ServiceKey;
import xyz.yanghaoyu.flora.rpc.base.service.key.ServiceKeyBuilder;

public final class ZookeeperServiceKeyBuilder implements ServiceKeyBuilder {
    private String interfaceName;
    private String group;
    private String version;
    private String namespace;

    private ZookeeperServiceKeyBuilder() {}

    public static ZookeeperServiceKeyBuilder aKey() {
        return new ZookeeperServiceKeyBuilder();
    }

    public ZookeeperServiceKeyBuilder interfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
        return this;
    }

    public ZookeeperServiceKeyBuilder group(String group) {
        this.group = group;
        return this;
    }

    public ZookeeperServiceKeyBuilder version(String version) {
        this.version = version;
        return this;
    }

    public ZookeeperServiceKeyBuilder namespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public ServiceKey build() {
        return new ZookeeperServiceKey(namespace, interfaceName, group, version);
    }
}
