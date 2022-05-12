/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.config;

public class Service {
    private final Object            serviceBean;
    private final ServiceConfig     serviceConfig;
    private final RpcResponseConfig rpcResponseConfig;

    public Service(Object serviceBean, ServiceConfig serviceConfig, RpcResponseConfig rpcResponseConfig) {
        this.serviceBean = serviceBean;
        this.serviceConfig = serviceConfig;
        this.rpcResponseConfig = rpcResponseConfig;
    }

    public RpcResponseConfig getRpcResponseConfig() {
        return rpcResponseConfig;
    }

    public Object getServiceBean() {
        return serviceBean;
    }

    public ServiceConfig getServiceConfig() {
        return serviceConfig;
    }
}
