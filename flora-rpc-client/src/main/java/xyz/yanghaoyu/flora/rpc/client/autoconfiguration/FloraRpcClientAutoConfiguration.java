/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration;

import xyz.yanghaoyu.flora.annotation.Bean;
import xyz.yanghaoyu.flora.annotation.Configuration;
import xyz.yanghaoyu.flora.annotation.Enable;
import xyz.yanghaoyu.flora.annotation.Inject;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.base.service.support.ZooKeeper;
import xyz.yanghaoyu.flora.rpc.base.service.support.ZooKeeperServiceRegistry;

@Configuration
@Enable.ComponentScan(basePackages = "xyz.yanghaoyu.flora.rpc.client.autoconfiguration")
public class FloraRpcClientAutoConfiguration {
    @Inject.ByName("clientZooKeeper")
    private ZooKeeper zooKeeper;

    @Bean("clientServiceRegistry")
    public ServiceRegistry registry() {
        return new ZooKeeperServiceRegistry(zooKeeper);
    }
}
