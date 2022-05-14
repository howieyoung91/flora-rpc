/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.util;

import xyz.yanghaoyu.flora.rpc.server.annotation.RpcResponse;
import xyz.yanghaoyu.flora.rpc.server.annotation.RpcResponseAttribute;
import xyz.yanghaoyu.flora.rpc.server.annotation.RpcServiceAttribute;
import xyz.yanghaoyu.flora.rpc.server.annotation.RpcService;

public abstract class ServiceUtil {
    public static RpcServiceAttribute buildServiceAttribute(RpcService rpcServiceAnn) {
        return new RpcServiceAttribute(
                rpcServiceAnn.namespace(),
                rpcServiceAnn.interfaceName(),
                rpcServiceAnn.group(),
                rpcServiceAnn.version()
        );
    }

    public static RpcResponseAttribute buildRpcResponseAttribute(RpcResponse rpcResponse) {
        RpcResponseAttribute attribute = new RpcResponseAttribute();
        attribute.setSerializerName(rpcResponse.serializer());
        attribute.setCompressorName(rpcResponse.compressor());
        return attribute;
    }
}
