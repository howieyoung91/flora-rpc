/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.transport.filter;

public interface RpcFilter {
    // void init();

    void doFilter();

    // void destroy();
}
