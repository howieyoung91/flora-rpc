/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.util;

import xyz.yanghaoyu.flora.rpc.server.annotation.RpcResponse;
import xyz.yanghaoyu.flora.rpc.server.config.RpcResponseConfig;
import xyz.yanghaoyu.flora.rpc.server.config.ServiceConfig;
import xyz.yanghaoyu.flora.rpc.server.annotation.RpcService;

public final class ServiceUtil {
    public static ServiceConfig buildServiceConfig(RpcService rpcServiceAnn) {
        return new ServiceConfig(
                rpcServiceAnn.namespace(),
                rpcServiceAnn.interfaceName(),
                rpcServiceAnn.group(),
                rpcServiceAnn.version()
        );
    }

    public static RpcResponseConfig buildRpcResponseConfig(RpcResponse rpcResponse) {
        RpcResponseConfig rpcResponseConfig = new RpcResponseConfig();
        rpcResponseConfig.setSerializerName(rpcResponse.serializer());
        return rpcResponseConfig;
    }
}
