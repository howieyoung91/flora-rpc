/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.util;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public abstract class ServiceUtil {
    public static String buildNamespacedServiceNodePath(String namespace, String serviceName) {
        return namespace + "/" + serviceName;
    }

    public static String buildServicePathWithAddress(String namespace, String serviceName, InetSocketAddress inetSocketAddress) {
        return namespace + "/" + serviceName + inetSocketAddress.toString();
    }

    public static String buildServiceName(String interfaceName, String group, String version) {
        return interfaceName + '-' + group + '@' + version;
    }

    public static InetSocketAddress buildAddress(String addressString) {
        return buildAddress(addressString.split(":"));
    }

    public static InetSocketAddress buildAddress(String[] socketAddressArray) {
        String host = socketAddressArray[0];
        int    port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }

    public static InetSocketAddress getLocalAddress(int port) {
        try {
            return new InetSocketAddress(Inet4Address.getLocalHost(), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("fail to get local address. port: [" + port + "]");
    }
}

