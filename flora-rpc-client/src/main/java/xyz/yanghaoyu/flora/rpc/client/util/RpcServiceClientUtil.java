/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.util;

import xyz.yanghaoyu.flora.rpc.client.annotation.ServiceReferenceAttribute;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcRequest;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcServiceReference;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcRequestAttribute;

public abstract class RpcServiceClientUtil {
    public static ServiceReferenceAttribute buildServiceReferenceAttribute(RpcServiceReference rpcServiceReference) {
        return new ServiceReferenceAttribute(
                rpcServiceReference.namespace(),
                rpcServiceReference.interfaceName(),
                rpcServiceReference.group(),
                rpcServiceReference.version()
        );
    }

    public static RpcRequestAttribute buildRpcRequestAttribute(RpcRequest rpcRequestAnn) {
        RpcRequestAttribute attribute = new RpcRequestAttribute();
        attribute.setSerializerName(rpcRequestAnn.serializer());
        attribute.setCompressorName(rpcRequestAnn.compressor());
        return attribute;
    }
}

