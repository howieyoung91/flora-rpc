/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.util;

import xyz.yanghaoyu.flora.rpc.base.annotation.RpcRequestAttribute;
import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceReferenceAttribute;
import xyz.yanghaoyu.flora.rpc.base.config.ClientConfig;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceException;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcRequest;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcServiceReference;

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

        String   interfaceName = rpcServiceReference.interfaceName();
        Class<?> interfaceType = rpcServiceReference.interfaceType();
        if (RpcServiceReference.EMPTY_INTERFACE_NAME.equals(interfaceName)) {
            if (interfaceType.equals(RpcServiceReference.EMPTY_INTERFACE_TYPE)) {
                // 默认把接口名字作为服务名
                interfaceName = field.getType().getName();
            } else {
                interfaceName = interfaceType.getName();
            }
        } else {
            if (!interfaceType.equals(RpcServiceReference.EMPTY_INTERFACE_TYPE)) {
                throw new ServiceException("cannot determine interface name on field [" + field.getDeclaringClass().getName() + '#' + field.getName() + "]. cause: interfaceName [" + interfaceName + "] and interfaceType [" + interfaceType + "]. Which should I use?");
            }
        }

        String version = rpcServiceReference.version();
        if (version.equals(RpcServiceReference.EMPTY_VERSION)) {
            String defaultVersion = config.version();
            Objects.requireNonNull(defaultVersion, "found no version");
            version = defaultVersion;
        }
        return new ServiceReferenceAttribute(namespace, interfaceName, group, version);
    }

    public static RpcRequestAttribute buildRpcRequestAttribute(RpcRequest rpcRequestAnn, ClientConfig config) {
        RpcRequestAttribute attribute = new RpcRequestAttribute();
        if (rpcRequestAnn == null) {
            attribute.setSerializerName(config.defaultSerializer());
            attribute.setCompressorName(config.defaultCompressor());
            attribute.setAlwaysRemote(RpcRequest.DEFAULT_ALWAYS_REMOTE);
            return attribute;
        }

        String serializer = rpcRequestAnn.serializer();
        if (serializer.equals("")) {
            serializer = config.defaultSerializer();
        }

        String compressor = rpcRequestAnn.compressor();
        if (compressor.equals("")) {
            compressor = config.defaultCompressor();
        }

        attribute.setSerializerName(serializer);
        attribute.setCompressorName(compressor);
        attribute.setAlwaysRemote(rpcRequestAnn.alwaysRemote());
        return attribute;
    }
}

