/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.util;

import xyz.yanghaoyu.flora.rpc.base.annotation.RpcResponseAttribute;
import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceAttribute;
import xyz.yanghaoyu.flora.rpc.base.config.ServerConfig;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceException;
import xyz.yanghaoyu.flora.rpc.base.service.support.zookeeper.ZookeeperServiceKeyBuilder;
import xyz.yanghaoyu.flora.rpc.base.util.StringUtil;
import xyz.yanghaoyu.flora.rpc.server.annotation.RpcResponse;
import xyz.yanghaoyu.flora.rpc.server.annotation.RpcService;

public abstract class RpcServiceServerUtil {
    public static ServiceAttribute buildServiceAttribute(RpcService serviceAnn, Class<?> clazz, ServerConfig serverConfig) {
        String group         = StringUtil.getOrDefaultNonNull(serviceAnn.group(), serverConfig.group(), "no group found");
        String namespace     = StringUtil.getOrDefaultNonNull(serviceAnn.namespace(), serverConfig.namespace(), "no namespace found");
        String version       = StringUtil.getOrDefaultNonNull(serviceAnn.version(), serverConfig.version(), "no version found");
        String interfaceName = determineInterfaceName(serviceAnn, clazz);
        return new ServiceAttribute(ZookeeperServiceKeyBuilder.aKey().group(group).namespace(namespace).interfaceName(interfaceName).version(version).build());
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
            return getDefaultRpcResponseAttribute(serverConfig.defaultCompressor(), serverConfig.defaultSerializer());
        }
        return doBuildRpcResponseAttribute(serverConfig, rpcResponseAnn);
    }

    private static RpcResponseAttribute getDefaultRpcResponseAttribute(String serverConfig, String serverConfig1) {
        RpcResponseAttribute attribute = new RpcResponseAttribute();
        attribute.setCompressorName(serverConfig);
        attribute.setSerializerName(serverConfig1);
        return attribute;
    }

    private static RpcResponseAttribute doBuildRpcResponseAttribute(ServerConfig serverConfig, RpcResponse rpcResponseAnn) {
        String compressor = StringUtil.getOrDefaultNonNull(rpcResponseAnn.compressor(), serverConfig.defaultCompressor(), "no compressor found");
        String serializer = StringUtil.getOrDefaultNonNull(rpcResponseAnn.serializer(), serverConfig.defaultSerializer(), "no serializer found");

        RpcResponseAttribute attribute = new RpcResponseAttribute();
        attribute.setCompressorName(compressor);
        attribute.setSerializerName(serializer);
        return attribute;
    }
}
