/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.service;

import xyz.yanghaoyu.flora.rpc.server.annotation.RpcResponseAttribute;
import xyz.yanghaoyu.flora.rpc.server.annotation.ServiceAttribute;

public class Service {
    private final Object               serviceBean;
    private final ServiceAttribute     serviceConfig;
    private final RpcResponseAttribute rpcResponseConfig;

    public Service(Object serviceBean, ServiceAttribute serviceConfig, RpcResponseAttribute rpcResponseConfig) {
        this.serviceBean = serviceBean;
        this.serviceConfig = serviceConfig;
        this.rpcResponseConfig = rpcResponseConfig;
    }

    public RpcResponseAttribute getRpcResponseConfig() {
        return rpcResponseConfig;
    }

    public Object getServiceBean() {
        return serviceBean;
    }

    public ServiceAttribute getServiceConfig() {
        return serviceConfig;
    }
}
