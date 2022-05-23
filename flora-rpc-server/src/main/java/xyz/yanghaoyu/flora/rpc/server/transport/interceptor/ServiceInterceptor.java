/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.transport.interceptor;

import xyz.yanghaoyu.flora.core.Ordered;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestBody;
import xyz.yanghaoyu.flora.rpc.server.transport.RpcResponseConfig;

public interface ServiceInterceptor extends Ordered {
    default RpcResponseConfig adviseHandle(RpcRequestBody requestBody) {
        return null;
    }

    default void beforeHandle(RpcRequestBody requestBody) {
    }

    default void afterHandle(RpcRequestBody requestBody, RpcResponseConfig responseConfig) {
    }
}
