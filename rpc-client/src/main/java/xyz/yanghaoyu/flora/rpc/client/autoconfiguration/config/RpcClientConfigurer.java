/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config;


public interface RpcClientConfigurer {
    default ClientConfigurer clientConfigurer() {
        return null;
    }

    default ServiceDiscoveryConfigurer serviceDiscoveryConfigurer() {
        return null;
    }

    default ZooKeeperConfigurer zooKeeperConfigurer() {
        return null;
    }
}
