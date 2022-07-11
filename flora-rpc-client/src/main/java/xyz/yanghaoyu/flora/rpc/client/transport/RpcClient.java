/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.transport;

import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestConfig;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseBody;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public interface RpcClient {
    CompletableFuture<RpcResponseBody> send(RpcRequestConfig requestConfig, InetSocketAddress target);

    void close();
}
