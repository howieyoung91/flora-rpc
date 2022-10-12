/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.transport.support;

import xyz.yanghaoyu.flora.rpc.base.config.ClientConfig;
import xyz.yanghaoyu.flora.rpc.base.transport.RpcRequestHandler;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.*;
import xyz.yanghaoyu.flora.rpc.base.util.ServiceUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public class DefaultRpcClient extends AbstractRpcClient {
    private final RpcRequestHandler localRequestHandler;
    private final InetSocketAddress localhost;

    public DefaultRpcClient(ClientConfig config, InetSocketAddress localServerAddress, RpcRequestHandler localRequestHandler) {
        super(config);
        this.localhost = localServerAddress;
        this.localRequestHandler = localRequestHandler;
    }

    @Override
    public CompletableFuture<RpcResponseBody> handleRequestLocally(RpcRequestConfig requestConfig) {
        RpcRequestBody    requestBody    = ServiceUtil.buildRpcRequestBody(requestConfig);
        RpcResponseConfig responseConfig = getLocalRequestHandler().handleRequest(requestBody);
        RpcResponseBody   responseBody   = ServiceUtil.buildResponseBody(requestBody.getId(), responseConfig);
        // 返回 promise，直接 complete 即可
        // CompletableFuture<RpcResponseBody> promise = new CompletableFuture<>();
        // promise.complete(responseBody);
        return CompletableFuture.completedFuture(responseBody);
    }

    @Override
    public boolean canHandleRequestLocally(InetSocketAddress target) {
        return getLocalRequestHandler() != null && target.equals(localhost);
    }

    @Override
    public RpcRequestHandler getLocalRequestHandler() {
        return localRequestHandler;
    }
}
