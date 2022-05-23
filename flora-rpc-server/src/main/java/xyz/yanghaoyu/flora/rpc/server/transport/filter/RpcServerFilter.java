/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.transport.filter;

import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestBody;

interface RpcServerFilter {
    void doFilter(RpcRequestBody object);

    default void continueFilter(RpcRequestBody requestBody) {}
}
