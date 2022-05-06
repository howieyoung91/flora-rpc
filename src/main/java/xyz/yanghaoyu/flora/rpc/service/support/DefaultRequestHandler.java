/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.service.support;

import xyz.yanghaoyu.flora.rpc.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.transport.dto.Request;

public class DefaultRequestHandler extends AbstractRequestHandler {
    public DefaultRequestHandler(ServiceRegistry registry) {
        super(registry);
    }

    @Override
    public Object handle(Request request) {
        return null;
    }
}
