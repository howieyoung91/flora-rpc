/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.transport;

import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;

public final class RpcServerBuilder {
    private int             port;
    private ServiceRegistry registry;
    private ServiceHandler  serviceHandler;

    private RpcServerBuilder() {
    }

    public static RpcServerBuilder aRpcServer(ServiceRegistry registry, ServiceHandler handler) {
        RpcServerBuilder builder = new RpcServerBuilder();
        builder.registry = registry;
        builder.serviceHandler = handler;
        return builder;
    }

    public RpcServerBuilder port(int port) {
        this.port = port;
        return this;
    }

    public RpcServerBuilder registry(ServiceRegistry registry) {
        this.registry = registry;
        return this;
    }

    public RpcServerBuilder serviceHandler(ServiceHandler serviceHandler) {
        this.serviceHandler = serviceHandler;
        return this;
    }

    public RpcServer build() {
        RpcServer rpcServer = new RpcServer(registry, serviceHandler);
        rpcServer.setPort(port);
        return rpcServer;
    }
}
