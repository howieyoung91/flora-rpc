/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config;


public interface ServiceDiscoveryConfigurer {
    default String namespace() {
        return null;
    }

    // String loadBalance();
}
