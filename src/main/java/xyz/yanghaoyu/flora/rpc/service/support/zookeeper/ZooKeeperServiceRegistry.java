/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.service.support.zookeeper;

import xyz.yanghaoyu.flora.rpc.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.util.ServiceUtil;

import java.net.InetSocketAddress;

public class ZooKeeperServiceRegistry implements ServiceRegistry {
    private ZooKeeper zooKeeper; // todo inject

    @Override
    public void register(String serviceName, InetSocketAddress address) {
        String path = ServiceUtil.buildServiceName(serviceName, address);
        zooKeeper.createPersistentNode(path);
    }
}
