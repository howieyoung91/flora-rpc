/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.annotation;

import xyz.yanghaoyu.flora.rpc.base.util.ServiceUtil;

public class ServiceReferenceAttribute {
    private String interfaceName;
    private String group;
    private String version;
    private String namespace;
    private String serviceName;

    public ServiceReferenceAttribute(String namespace, String serviceInterfaceName, String group, String version) {
        this.namespace = namespace;
        this.interfaceName = serviceInterfaceName;
        this.group = group;
        this.version = version;
        serviceName = ServiceUtil.buildServiceName(serviceInterfaceName, group, version);
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getGroup() {
        return group;
    }

    public String getVersion() {
        return version;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public String toString() {
        return getServiceName();
    }

}
