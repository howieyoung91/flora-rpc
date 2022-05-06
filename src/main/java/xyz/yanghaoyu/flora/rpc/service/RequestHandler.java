/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.service;

import xyz.yanghaoyu.flora.rpc.transport.dto.Request;

public interface RequestHandler {
    Object handle(Request request);
}
