/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.service.support;

import xyz.yanghaoyu.flora.rpc.service.RequestHandler;
import xyz.yanghaoyu.flora.rpc.service.ServiceRegistry;

public abstract class AbstractRequestHandler implements RequestHandler {
    protected ServiceRegistry registry;

    public AbstractRequestHandler(ServiceRegistry registry) {
        this.registry = registry;
    }
}
