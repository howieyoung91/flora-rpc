/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.service.support.zookeeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.strategy.loadbalance.ServiceLoadBalance;
import xyz.yanghaoyu.flora.rpc.transport.dto.Request;
import xyz.yanghaoyu.flora.rpc.util.ServiceUtil;

import java.net.InetSocketAddress;
import java.util.List;


/**
 * 在 Zookeeper 中发现服务
 * <p>
 * todo
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);

    private ServiceLoadBalance loadBalance; // todo inject
    private ZooKeeper          zooKeeper;   // todo inject

    @Override
    public InetSocketAddress search(Request request) {
        String serviceName = ServiceUtil.buildServiceName(
                request.getInterfaceName(), request.getGroup(), request.getVersion()
        );

        List<String> childrenNodes = zooKeeper.getChildrenNodes(serviceName);
        String       addressString = loadBalance.pick(childrenNodes, request);
        logger.info("discovered service {} at {}", serviceName, addressString);

        return buildAddress(addressString.split(":"));
    }

    private InetSocketAddress buildAddress(String[] socketAddressArray) {
        String host = socketAddressArray[0];
        int    port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
