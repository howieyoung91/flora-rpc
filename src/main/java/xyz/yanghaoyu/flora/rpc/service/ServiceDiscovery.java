package xyz.yanghaoyu.flora.rpc.service;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {
    InetSocketAddress search(String serviceName);
}
