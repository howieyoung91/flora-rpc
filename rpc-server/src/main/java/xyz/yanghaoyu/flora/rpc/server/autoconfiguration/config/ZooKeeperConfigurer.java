/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config;

public interface ZooKeeperConfigurer {
    default String address() {
        return null;
    }

    default Integer baseSleepTime() {
        return null;
    }

    default Integer maxRetries() {
        return null;
    }
}
