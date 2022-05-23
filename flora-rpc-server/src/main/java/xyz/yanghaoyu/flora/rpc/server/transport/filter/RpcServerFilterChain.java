/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.transport.filter;

import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RpcServerFilterChain implements RpcServerFilter {
    private List<RpcServerFilter>     filters     = new ArrayList<>(0);
    private RpcServerFilter           initializer = new FilterChainInitializer(this);
    private Iterator<RpcServerFilter> iterator    = null;

    public void addFilter(RpcServerFilter filter) {
        this.filters.add(filter);
    }

    public void addFilter(RpcServerFilter... filters) {
        Collections.addAll(this.filters, filters);
    }

    @Override
    public void doFilter(RpcRequestBody object) {
        if (iterator == null) {
            initializer.doFilter(object);
            return;
        }
        if (iterator.hasNext()) {
            iterator.next().doFilter(object);
        }
    }

    void start() {
        iterator = filters.iterator();
    }

    void end() {
        iterator = null;
    }

    @Override
    public final void continueFilter(RpcRequestBody requestBody) {
        doFilter(requestBody);
    }

    private static class FilterChainInitializer extends AbstractRpcServerFilter {
        public FilterChainInitializer(RpcServerFilterChain filterChain) {
            super(filterChain);
        }

        @Override
        public void doFilter(RpcRequestBody object) {
            filterChain.start();
            continueFilter(object);
            filterChain.end();
        }
    }
}
