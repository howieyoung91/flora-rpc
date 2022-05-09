/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.util;

import xyz.yanghaoyu.flora.rpc.base.annotation.RpcService;
import xyz.yanghaoyu.flora.rpc.base.annotation.RpcServiceReference;
import xyz.yanghaoyu.flora.rpc.base.service.config.ServiceConfig;

import java.net.InetSocketAddress;

public abstract class ServiceUtil {
    public static String buildServicePath(String namespace, String serviceName, InetSocketAddress inetSocketAddress) {
        return "/" + namespace + "/" + serviceName + inetSocketAddress.toString();
    }

    public static String buildServiceName(String interfaceName, String group, String version) {
        return interfaceName + '-' + group + '@' + version;
    }

    public static ServiceConfig buildServiceConfig(RpcServiceReference rpcServiceReference) {
        return new ServiceConfig(
                rpcServiceReference.namespace(),
                rpcServiceReference.interfaceName(),
                rpcServiceReference.group(),
                rpcServiceReference.version()
        );
    }

    public static ServiceConfig buildServiceConfig(RpcService rpcServiceAnn) {
        return new ServiceConfig(
                rpcServiceAnn.namespace(),
                rpcServiceAnn.interfaceName(),
                rpcServiceAnn.group(),
                rpcServiceAnn.version()
        );
    }


}

