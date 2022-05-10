/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.discovery.config;

public interface DiscoveryConfig {
    String getNamespace();

    void setNamespace(String namespace);

    String getLoadBalance();

    void setLoadBalance(String loadBalance);
}
