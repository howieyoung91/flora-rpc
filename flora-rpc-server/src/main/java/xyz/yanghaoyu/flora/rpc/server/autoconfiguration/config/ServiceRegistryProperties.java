/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config;

import xyz.yanghaoyu.flora.annotation.Component;

@Component(ServiceRegistryProperties.BEAN_NAME)
public class ServiceRegistryProperties {
    public static final String BEAN_NAME = "flora-rpc-server$ServiceRegistryProperties$";

    // @Value(value = "${flora.rpc.server.registry.zookeeper.namespace}", required = false)
    // private String namespace;
    //
    // public String getNamespace() {
    //     return namespace;
    // }
}
