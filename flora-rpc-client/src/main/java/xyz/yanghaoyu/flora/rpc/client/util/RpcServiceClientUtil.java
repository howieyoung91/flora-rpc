/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.util;

import xyz.yanghaoyu.flora.rpc.client.annotation.RpcRequest;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcRequestAttribute;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcServiceReference;
import xyz.yanghaoyu.flora.rpc.client.annotation.ServiceReferenceAttribute;
import xyz.yanghaoyu.flora.rpc.client.config.ClientConfig;

import java.lang.reflect.Field;
import java.util.Objects;

public abstract class RpcServiceClientUtil {
    public static ServiceReferenceAttribute buildServiceReferenceAttribute(
            RpcServiceReference rpcServiceReference, ClientConfig config, Field field
    ) {
        String group = rpcServiceReference.group();
        if (group.equals(RpcServiceReference.EMPTY_GROUP)) {
            String defaultGroup = config.group();
            Objects.requireNonNull(defaultGroup, "found no group");
            group = defaultGroup;
        }

        String namespace = rpcServiceReference.namespace();
        if (namespace.equals(RpcServiceReference.EMPTY_NAMESPACE)) {
            String defaultNamespace = config.namespace();
            Objects.requireNonNull(defaultNamespace, "found no namespace");
            namespace = defaultNamespace;
        }

        String interfaceName = rpcServiceReference.interfaceName();
        if (interfaceName.equals(RpcServiceReference.EMPTY_INTERFACE_NAME)) {
            // 默认把接口名字作为服务名
            interfaceName = field.getType().getName();
        }

        String version = rpcServiceReference.version();
        if (version.equals(RpcServiceReference.EMPTY_VERSION)) {
            String defaultVersion = config.version();
            Objects.requireNonNull(defaultVersion, "found no version");
            version = defaultVersion;
        }
        return new ServiceReferenceAttribute(namespace, interfaceName, group, version);
    }

    public static RpcRequestAttribute buildRpcRequestAttribute(RpcRequest rpcRequestAnn) {
        RpcRequestAttribute attribute = new RpcRequestAttribute();
        attribute.setSerializerName(rpcRequestAnn.serializer());
        attribute.setCompressorName(rpcRequestAnn.compressor());
        return attribute;
    }
}

