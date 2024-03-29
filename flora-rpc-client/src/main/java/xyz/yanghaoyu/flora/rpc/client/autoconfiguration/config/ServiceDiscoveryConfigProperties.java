/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config;

import xyz.yanghaoyu.flora.framework.annotation.Component;
import xyz.yanghaoyu.flora.framework.annotation.Value;

@Component(ServiceDiscoveryConfigProperties.BEAN_NAME)
public class ServiceDiscoveryConfigProperties {
    public static final String BEAN_NAME = "flora-rpc-client$ServiceDiscoveryConfigProperties$";

    @Value(value = "${flora.rpc.client.discovery.zookeeper.load-balance}", required = false)
    private String loadBalance;

    public String getLoadBalance() {
        return loadBalance;
    }
}
