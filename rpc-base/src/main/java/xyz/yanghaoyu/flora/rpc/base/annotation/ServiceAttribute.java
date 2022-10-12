/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.annotation;

import xyz.yanghaoyu.flora.rpc.base.service.ServiceKey;

public class ServiceAttribute {
    private ServiceKey serviceKey;

    public ServiceAttribute(ServiceKey serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String interfaceName() {
        return serviceKey.interfaceName();
    }

    public String getGroup() {
        return serviceKey.group();
    }

    public String getVersion() {
        return serviceKey.version();
    }

    public String getServiceName() {
        return serviceKey.serviceName();
    }

    public String getNamespace() {
        return serviceKey.namespace();
    }

    @Override
    public String toString() {
        return getServiceName();
    }
}
