/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.service;

import xyz.yanghaoyu.flora.rpc.base.exception.ServiceNotFoundException;
import xyz.yanghaoyu.flora.rpc.client.annotation.ServiceReferenceAttribute;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {
    InetSocketAddress discover(ServiceReferenceAttribute serviceConfig) throws ServiceNotFoundException;
}
