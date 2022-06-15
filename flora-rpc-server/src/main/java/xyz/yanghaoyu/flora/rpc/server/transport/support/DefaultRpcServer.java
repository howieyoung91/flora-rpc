/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.transport.support;

import xyz.yanghaoyu.flora.rpc.base.config.ServerConfig;
import xyz.yanghaoyu.flora.rpc.base.service.Service;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;

public class DefaultRpcServer extends AbstractRpcServer {
    private final ServiceRegistry registry;

    public DefaultRpcServer(ServerConfig config, ServiceRegistry registry, ServiceHandler handler) {
        super(config, handler);
        this.registry = registry;
    }

    @Override
    public void publishService(Service service) {
        registry.register(getConfig().address(), service);
    }
}
