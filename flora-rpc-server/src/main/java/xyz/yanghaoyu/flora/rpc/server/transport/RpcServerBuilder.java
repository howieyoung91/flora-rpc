/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.transport;

import xyz.yanghaoyu.flora.core.OrderComparator;
import xyz.yanghaoyu.flora.rpc.server.config.ServerConfig;
import xyz.yanghaoyu.flora.rpc.server.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.server.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.server.transport.interceptor.ServiceInterceptor;

import java.util.Collection;
import java.util.TreeSet;

public class RpcServerBuilder {
    private ServerConfig                config;
    private ServiceRegistry             registry;
    private ServiceHandler              handler;
    private TreeSet<ServiceInterceptor> interceptors = new TreeSet<>(OrderComparator.INSTANCE);

    public static RpcServerBuilder aServer(ServerConfig config, ServiceRegistry registry, ServiceHandler handler) {
        RpcServerBuilder builder = new RpcServerBuilder();
        builder.config = config;
        builder.registry = registry;
        builder.handler = handler;
        return builder;
    }

    public RpcServerBuilder addInterceptors(Collection<ServiceInterceptor> interceptors) {
        this.interceptors.addAll(interceptors);
        return this;
    }

    public RpcServer build() {
        RpcServer server = new RpcServer(config, registry, handler);
        server.setInterceptors(interceptors);
        return server;
    }
}
