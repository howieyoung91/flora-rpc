package xyz.yanghaoyu.flora.rpc.service.support;

import xyz.yanghaoyu.flora.rpc.service.ServiceDiscovery;

import java.net.InetSocketAddress;


/**
 * 在 Zookeeper 中发现服务
 * <p>
 * todo
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery {

    @Override
    public InetSocketAddress search(String serviceName) {
        return null;
    }
}
