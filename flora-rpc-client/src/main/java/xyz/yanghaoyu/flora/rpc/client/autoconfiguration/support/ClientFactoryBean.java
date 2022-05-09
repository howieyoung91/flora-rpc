/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.support;

import xyz.yanghaoyu.flora.annotation.Component;
import xyz.yanghaoyu.flora.annotation.Inject;
import xyz.yanghaoyu.flora.core.beans.factory.FactoryBean;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.client.transport.RpcClient;

@Component("rpcClient")
public class ClientFactoryBean implements FactoryBean<RpcClient> {
    @Inject.ByName("clientServiceHandler")
    private ServiceDiscovery discovery;

    @Override
    public RpcClient getObject() {
        return new RpcClient(discovery);
    }

    @Override
    public Class<?> getObjectType() {
        return RpcClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
