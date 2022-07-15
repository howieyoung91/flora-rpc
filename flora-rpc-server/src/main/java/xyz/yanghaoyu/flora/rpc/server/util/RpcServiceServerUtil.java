/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.util;

import xyz.yanghaoyu.flora.rpc.base.annotation.RpcResponseAttribute;
import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceAttribute;
import xyz.yanghaoyu.flora.rpc.base.config.ServerConfig;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceException;
import xyz.yanghaoyu.flora.rpc.base.service.zookeeper.ZookeeperServiceKey;
import xyz.yanghaoyu.flora.rpc.server.annotation.RpcResponse;
import xyz.yanghaoyu.flora.rpc.server.annotation.RpcService;

import java.util.Objects;

public abstract class RpcServiceServerUtil {
    public static ServiceAttribute buildServiceAttribute(
            RpcService serviceAnn, Class<?> clazz, ServerConfig serverConfig) {
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

        String interfaceName = determineInterfaceName(serviceAnn, clazz);

        String version = serviceAnn.version();
        if (version.equals(RpcService.EMPTY_VERSION)) {
            String defaultVersion = serverConfig.version();
            Objects.requireNonNull(defaultVersion, "found no version");
            version = defaultVersion;
        }
        return new ServiceAttribute(new ZookeeperServiceKey(namespace, interfaceName, group, version));
    }

    private static String determineInterfaceName(RpcService serviceAnn, Class<?> clazz) {
        String interfaceName = serviceAnn.interfaceName();
        Class  interfaceType = serviceAnn.interfaceType();
        if (interfaceName.equals(RpcService.EMPTY_INTERFACE_NAME)) {
            if (interfaceType.equals(RpcService.EMPTY_INTERFACE_TYPE)) {
                Class<?>[] interfaces = clazz.getInterfaces();
                // 没有实现接口 就直接用类名作为 service name
                // 有接口 就直接用第一个实现的接口名字作为 interface name
                interfaceName = interfaces.length == 0 ? clazz.getName() : interfaces[0].getName();
            }
            else {
                interfaceName = interfaceType.getName();
            }
        }
        else {
            if (!interfaceType.equals(RpcService.EMPTY_INTERFACE_TYPE)) {
                throw new ServiceException("cannot determine interface name on class [" + clazz + "]. cause: interfaceName [" + interfaceName + "] and interfaceType [" + interfaceType + "]. Which should I use?");
            }
        }
        return interfaceName;
    }

    public static RpcResponseAttribute buildRpcResponseAttribute(Class<?> clazz, ServerConfig serverConfig) {
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
