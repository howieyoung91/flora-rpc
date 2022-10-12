/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.transport;

import xyz.yanghaoyu.flora.rpc.base.service.Service;

public interface ServicePublisher {
    void publishService(Service service);

    void cancelServices();
}
