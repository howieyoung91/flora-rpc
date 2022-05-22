/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.service;

import xyz.yanghaoyu.flora.rpc.server.annotation.RpcResponseAttribute;
import xyz.yanghaoyu.flora.rpc.server.annotation.ServiceAttribute;

public class Service {
    private final Object               serviceBean;
    private final ServiceAttribute     serviceAttribute;
    private final RpcResponseAttribute responseAttribute;

    public Service(Object serviceBean, ServiceAttribute serviceAttribute, RpcResponseAttribute responseAttribute) {
        this.serviceBean = serviceBean;
        this.serviceAttribute = serviceAttribute;
        this.responseAttribute = responseAttribute;
    }

    public RpcResponseAttribute getResponseAttribute() {
        return responseAttribute;
    }

    public Object getServiceBean() {
        return serviceBean;
    }

    public ServiceAttribute getServiceAttribute() {
        return serviceAttribute;
    }
}
