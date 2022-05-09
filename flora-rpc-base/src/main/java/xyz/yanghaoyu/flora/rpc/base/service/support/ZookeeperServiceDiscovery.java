/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceException;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.base.strategy.loadbalance.ServiceLoadBalance;

import java.net.InetSocketAddress;
import java.util.List;


/**
 * 在 Zookeeper 中发现服务
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);

    private final ZooKeeper          zooKeeper;
    private final ServiceLoadBalance loadBalance;

    public ZookeeperServiceDiscovery(ZooKeeper zooKeeper, ServiceLoadBalance loadBalance) {
        this.zooKeeper = zooKeeper;
        this.loadBalance = loadBalance;
    }

    @Override
    public InetSocketAddress discover(String serviceName) {
        List<String> childrenNodes = zooKeeper.getChildrenNodes(serviceName);
        String       addressString = loadBalance.pick(childrenNodes);
        if (addressString == null) {
            throw new ServiceException("discovered no service");
        } else {
            logger.info("discovered service {} at {}", serviceName, addressString);
        }

        return buildAddress(addressString.split(":"));
    }

    private InetSocketAddress buildAddress(String[] socketAddressArray) {
        String host = socketAddressArray[0];
        int    port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
