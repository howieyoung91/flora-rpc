/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.util;

import xyz.yanghaoyu.flora.rpc.base.annotation.RpcRequestAttribute;
import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceReferenceAttribute;
import xyz.yanghaoyu.flora.rpc.base.config.ClientConfig;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceException;
import xyz.yanghaoyu.flora.rpc.base.service.support.zookeeper.ZookeeperServiceKeyBuilder;
import xyz.yanghaoyu.flora.rpc.base.util.StringUtil;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcRequest;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcServiceReference;

import java.lang.reflect.Field;

public abstract class RpcServiceClientUtil {
    public static ServiceReferenceAttribute buildServiceReferenceAttribute(
            RpcServiceReference rpcServiceReference, ClientConfig config, Field field) {
        String group         = StringUtil.getOrDefaultNonNull(rpcServiceReference.group(), config.group(), "no namespace found");
        String namespace     = StringUtil.getOrDefaultNonNull(rpcServiceReference.namespace(), config.namespace(), "no group found");
        String version       = StringUtil.getOrDefaultNonNull(rpcServiceReference.version(), config.version(), "no version found");
        String interfaceName = determineInterfaceName(field, rpcServiceReference.interfaceName(), rpcServiceReference.interfaceType());

        return new ServiceReferenceAttribute(
                ZookeeperServiceKeyBuilder.aKey()
                        .namespace(namespace)
                        .group(group)
                        .interfaceName(interfaceName)
                        .version(version)
                        .build()
        );
    }

    private static String determineInterfaceName(Field field, String interfaceName, Class<?> interfaceType) {
        if (RpcServiceReference.EMPTY_INTERFACE_NAME.equals(interfaceName)) {
            if (interfaceType.equals(RpcServiceReference.EMPTY_INTERFACE_TYPE)) {
                interfaceName = field.getType().getName();  // 默认把接口名字作为 interfaceName
            }
            else {
                interfaceName = interfaceType.getName();
            }
        }
        else {
            if (!interfaceType.equals(RpcServiceReference.EMPTY_INTERFACE_TYPE)) {
                throw new ServiceException("cannot determine interface name on field [" + field.getDeclaringClass().getName() + '#' + field.getName() + "]. cause: interfaceName [" + interfaceName + "] and interfaceType [" + interfaceType + "]. Which should I use?");
            }
        }
        return interfaceName;
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

