/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.util;

import xyz.yanghaoyu.flora.rpc.base.service.config.ServiceReferenceConfig;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcRequest;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcServiceReference;
import xyz.yanghaoyu.flora.rpc.client.config.RpcRequestAnnotationConfig;

public abstract class RpcServiceClientUtil {
    public static ServiceReferenceConfig buildServiceReferenceConfig(RpcServiceReference rpcServiceReference) {
        return new ServiceReferenceConfig(
                rpcServiceReference.namespace(),
                rpcServiceReference.interfaceName(),
                rpcServiceReference.group(),
                rpcServiceReference.version()
        );
    }

    public static RpcRequestAnnotationConfig buildRpcRequestConfig(RpcRequest rpcRequestAnn) {
        RpcRequestAnnotationConfig config = new RpcRequestAnnotationConfig();
        config.setSerializerName(rpcRequestAnn.serializer());
        return config;
    }
}

