/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config;

import xyz.yanghaoyu.flora.annotation.Component;
import xyz.yanghaoyu.flora.annotation.Value;

@Component(ZooKeeperConfigProperties.BEAN_NAME)
public class ZooKeeperConfigProperties {
    public static final String BEAN_NAME = "flora-rpc-server$ZooKeeperConfigProperties$";

    @Value(value = "${flora.rpc.server.registry.zookeeper.address}", required = false)
    private String  zooKeeperAddress;
    @Value(value = "${flora.rpc.server.registry.zookeeper.base-sleep-time}", required = false)
    private Integer baseSleepTime;
    @Value(value = "${flora.rpc.server.registry.zookeeper.max-retries}", required = false)
    private Integer maxRetries;

    public String getZooKeeperAddress() {
        return zooKeeperAddress;
    }

    public Integer getBaseSleepTime() {
        return baseSleepTime;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }
}
