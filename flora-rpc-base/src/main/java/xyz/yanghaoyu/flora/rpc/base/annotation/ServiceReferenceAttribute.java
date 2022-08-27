/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.annotation;

import xyz.yanghaoyu.flora.rpc.base.service.ServiceKey;

public class ServiceReferenceAttribute {
    private ServiceKey key;

    public ServiceReferenceAttribute(ServiceKey key) {
        this.key = key;
    }

    public String getInterfaceName() {
        return key.interfaceName();
    }

    public String getGroup() {
        return key.group();
    }

    public String getVersion() {
        return key.version();
    }

    public String getServiceName() {
        return key.serviceName();
    }

    public String getNamespace() {
        return key.namespace();
    }

    @Override
    public String toString() {
        return getServiceName();
    }
}
