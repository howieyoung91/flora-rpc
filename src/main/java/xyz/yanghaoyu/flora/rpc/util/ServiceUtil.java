/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.util;

import java.net.InetSocketAddress;

public abstract class ServiceUtil {
    public static String buildServiceName(String serviceName, InetSocketAddress inetSocketAddress) {
        // todo
        return "/" + serviceName + inetSocketAddress.toString();
    }

    public static String buildServiceName(String interfaceName, String group, String version) {
        return interfaceName + '#' + group + '@' + version;
    }
}

