/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.util;

import xyz.yanghaoyu.flora.rpc.base.service.config.ServiceReferenceConfig;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcRequest;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcServiceReference;
import xyz.yanghaoyu.flora.rpc.client.config.RpcRequestConfig;

public abstract class RpcServiceClientUtil {
    public static ServiceReferenceConfig buildServiceReferenceConfig(RpcServiceReference rpcServiceReference) {
        return new ServiceReferenceConfig(
                rpcServiceReference.namespace(),
                rpcServiceReference.interfaceName(),
                rpcServiceReference.group(),
                rpcServiceReference.version()
        );
    }

    public static RpcRequestConfig buildRpcRequestConfig(RpcRequest rpcRequestAnn) {
        RpcRequestConfig config = new RpcRequestConfig();

        String serializer = rpcRequestAnn.serializer();
        if (serializer.equals("")) {
            serializer = "KRYO";
        }
        
        config.setSerializerName(serializer);
        return config;
    }
}

