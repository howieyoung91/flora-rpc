/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config;

public interface ZooKeeperConfigurer {
    default String getZooKeeperAddress() {
        return null;
    }

    default Integer getBaseSleepTime() {
        return null;
    }

    default Integer getMaxRetries() {
        return null;
    }
}
