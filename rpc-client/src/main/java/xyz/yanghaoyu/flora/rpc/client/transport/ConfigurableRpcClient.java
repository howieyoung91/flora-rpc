/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.transport;

import xyz.yanghaoyu.flora.rpc.base.config.ClientConfig;

public interface ConfigurableRpcClient extends RpcClient {
    ClientConfig getConfig();
}
