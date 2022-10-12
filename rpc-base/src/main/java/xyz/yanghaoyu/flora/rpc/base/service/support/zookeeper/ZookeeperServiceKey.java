/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service.support.zookeeper;

import xyz.yanghaoyu.flora.rpc.base.service.ServiceKey;

public class ZookeeperServiceKey implements ServiceKey {
    private String interfaceName;
    private String group;
    private String version;
    private String namespace;
    private String serviceName;

    ZookeeperServiceKey(String namespace, String interfaceName, String group, String version) {
        this.namespace = namespace;
        this.interfaceName = interfaceName;
        this.group = group;
        this.version = version;
        this.serviceName = buildServiceName(interfaceName, group, version);
    }

    public static ZookeeperServiceKey of(String namespace, String interfaceName, String group, String version) {
        return new ZookeeperServiceKey(namespace, interfaceName, group, version);
    }

    private static String buildServiceName(String interfaceName, String group, String version) {
        return group + '#' + interfaceName + '@' + version;
    }

    @Override
    public String group() {
        return group;
    }

    @Override
    public String interfaceName() {
        return interfaceName;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public String namespace() {
        return namespace;
    }

    @Override
    public String serviceName() {
        return serviceName;
    }
}
