/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.discovery.config;

/**
 * 使用 application.yaml 配置的发现中心配置
 */
public class DefaultDiscoveryConfig implements DiscoveryConfig {
    private String namespace;
    private String loadBalance;

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getLoadBalance() {
        return loadBalance;
    }

    @Override
    public void setLoadBalance(String loadBalance) {
        this.loadBalance = loadBalance;
    }
}
