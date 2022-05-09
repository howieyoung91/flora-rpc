/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.autoconfiguration;

import xyz.yanghaoyu.flora.annotation.Bean;
import xyz.yanghaoyu.flora.annotation.Configuration;
import xyz.yanghaoyu.flora.annotation.Enable;
import xyz.yanghaoyu.flora.annotation.Inject;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.base.service.support.DefaultServiceHandler;
import xyz.yanghaoyu.flora.rpc.base.service.support.ZooKeeper;
import xyz.yanghaoyu.flora.rpc.base.service.support.ZooKeeperServiceRegistry;

@Configuration
@Enable.ComponentScan(basePackages = "xyz.yanghaoyu.flora.rpc.server.autoconfiguration")
public class FloraRpcServerAutoConfiguration {
    @Inject.ByName("serverZooKeeper")
    private ZooKeeper zooKeeper;

    @Bean("serverServiceRegistry")
    public ServiceRegistry registry() {
        return new ZooKeeperServiceRegistry(zooKeeper);
    }

    @Bean("serverServiceHandler")
    public ServiceHandler handler() {
        return new DefaultServiceHandler(registry());
    }
}
