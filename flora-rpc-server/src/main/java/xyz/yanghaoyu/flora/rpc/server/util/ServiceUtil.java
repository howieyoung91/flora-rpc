/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.util;

import xyz.yanghaoyu.flora.rpc.server.annotation.RpcResponse;
import xyz.yanghaoyu.flora.rpc.server.annotation.RpcResponseAttribute;
import xyz.yanghaoyu.flora.rpc.server.annotation.RpcService;
import xyz.yanghaoyu.flora.rpc.server.annotation.ServiceAttribute;
import xyz.yanghaoyu.flora.rpc.server.config.ServerConfig;

import java.util.Objects;

public abstract class ServiceUtil {
    // public static ServiceAttribute buildServiceAttribute(RpcService rpcServiceAnn) {
    //     return new ServiceAttribute(
    //             rpcServiceAnn.namespace(),
    //             rpcServiceAnn.interfaceName(),
    //             rpcServiceAnn.group(),
    //             rpcServiceAnn.version()
    //     );
    // }

    // public static RpcResponseAttribute buildRpcResponseAttribute(RpcResponse rpcResponse) {
    //     RpcResponseAttribute attribute = new RpcResponseAttribute();
    //     attribute.setSerializerName(rpcResponse.serializer());
    //     attribute.setCompressorName(rpcResponse.compressor());
    //     return attribute;
    // }


    public static ServiceAttribute buildServiceAttribute(RpcService serviceAnn, Class<?> clazz, ServerConfig serverConfig) {
        String group = serviceAnn.group();
        if (group.equals(RpcService.EMPTY_GROUP)) {
            String defaultGroup = serverConfig.group();
            Objects.requireNonNull(defaultGroup, "found no group");
            group = defaultGroup;
        }

        String namespace = serviceAnn.namespace();
        if (namespace.equals(RpcService.EMPTY_NAMESPACE)) {
            String defaultNamespace = serverConfig.namespace();
            Objects.requireNonNull(defaultNamespace, "found no namespace");
            namespace = defaultNamespace;
        }

        String interfaceName = serviceAnn.interfaceName();
        if (interfaceName.equals(RpcService.EMPTY_INTERFACE_NAME)) {
            Class<?>[] interfaces = clazz.getInterfaces();
            // 没有实现接口 就直接用类名作为 service name
            // 有接口 就直接用第一个实现的接口名字作为 service name
            interfaceName = interfaces.length == 0 ? clazz.getName() : interfaces[0].getName();
        }

        String version = serviceAnn.version();
        if (version.equals(RpcService.EMPTY_VERSION)) {
            String defaultVersion = serverConfig.version();
            Objects.requireNonNull(defaultVersion, "found no version");
            version = defaultVersion;
        }

        return new ServiceAttribute(namespace, interfaceName, group, version);
    }

    public static RpcResponseAttribute buildRpcResponseAttribute(
            Class<?> clazz, ServerConfig serverConfig
    ) {
        RpcResponse rpcResponseAnn = clazz.getAnnotation(RpcResponse.class);
        if (rpcResponseAnn == null) {
            RpcResponseAttribute attribute = new RpcResponseAttribute();
            attribute.setCompressorName(serverConfig.defaultCompressor());
            attribute.setSerializerName(serverConfig.defaultSerializer());
            return attribute;
        }
        String compressor = rpcResponseAnn.compressor();
        if (compressor.equals(RpcResponse.EMPTY_COMPRESSOR)) {
            String defaultCompressor = serverConfig.defaultCompressor();
            Objects.requireNonNull(defaultCompressor, "found no compressor");
            compressor = defaultCompressor;
        }

        String serializer = rpcResponseAnn.serializer();
        if (serializer.equals(RpcResponse.EMPTY_SERIALIZER)) {
            String defaultSerializer = serverConfig.defaultSerializer();
            Objects.requireNonNull(defaultSerializer, "found no serializer");
            serializer = defaultSerializer;
        }

        RpcResponseAttribute attribute = new RpcResponseAttribute();
        attribute.setCompressorName(compressor);
        attribute.setSerializerName(serializer);
        return attribute;
    }
}
