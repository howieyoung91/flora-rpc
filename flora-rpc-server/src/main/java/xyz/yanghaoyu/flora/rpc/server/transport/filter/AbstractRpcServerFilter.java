/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.transport.filter;

import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestBody;

public abstract class AbstractRpcServerFilter implements RpcServerFilter {
    protected final RpcServerFilterChain filterChain;

    public AbstractRpcServerFilter(RpcServerFilterChain filterChain) {
        this.filterChain = filterChain;
    }

    @Override
    public final void continueFilter(RpcRequestBody requestBody) {
        this.filterChain.continueFilter(requestBody);
    }
}
