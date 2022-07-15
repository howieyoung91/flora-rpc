/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service.zookeeper;

import xyz.yanghaoyu.flora.rpc.base.service.ServiceKey;
import xyz.yanghaoyu.flora.rpc.base.util.ServiceUtil;

public class ZookeeperServiceKey implements ServiceKey {
    private String interfaceName;
    private String group;
    private String version;
    private String namespace;
    private String serviceName;

    public ZookeeperServiceKey(String namespace, String interfaceName, String group, String version) {
        this.namespace = namespace;
        this.interfaceName = interfaceName;
        this.group = group;
        this.version = version;
        this.serviceName = ServiceUtil.buildServiceName(interfaceName, group, version);
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
