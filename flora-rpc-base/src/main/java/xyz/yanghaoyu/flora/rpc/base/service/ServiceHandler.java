/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service;

import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestBody;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseConfig;

public interface ServiceHandler {
    RpcResponseConfig handle(RpcRequestBody request);
}
