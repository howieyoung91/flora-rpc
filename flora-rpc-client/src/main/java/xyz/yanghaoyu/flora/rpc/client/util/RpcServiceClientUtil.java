/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.util;

import xyz.yanghaoyu.flora.rpc.base.service.config.ServiceConfig;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcServiceReference;

public abstract class RpcServiceClientUtil {
    public static ServiceConfig buildServiceConfig(RpcServiceReference rpcServiceReference) {
        return new ServiceConfig(
                rpcServiceReference.namespace(),
                rpcServiceReference.interfaceName(),
                rpcServiceReference.group(),
                rpcServiceReference.version()
        );
    }
}

