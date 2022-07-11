/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service;

import java.net.InetSocketAddress;
import java.util.Collection;

public interface ServiceRegistry {
    void register(Service service);

    void exposeServices(InetSocketAddress address);

    /**
     * 取消所有服务
     */
    void cancelServices();

    Service getService(String serviceName);

    Collection<Service> services();

    boolean contains(String serviceName);
}
