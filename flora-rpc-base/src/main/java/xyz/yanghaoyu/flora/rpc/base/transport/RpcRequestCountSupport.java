/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.transport;

import cn.hutool.core.collection.ConcurrentHashSet;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@ChannelHandler.Sharable
public class RpcRequestCountSupport extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcRequestCountSupport.class);

    private final AtomicInteger                count    = new AtomicInteger(0);
    private final Set<CompletableFuture<Void>> promises = new ConcurrentHashSet<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        count.incrementAndGet();
        LOGGER.debug("active channel {}, remain {}", ctx.channel(), count);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (count.decrementAndGet() == 0) {
            for (CompletableFuture promise : promises) {
                promise.complete(null);
            }
        }
        LOGGER.debug("inactive channel {}, remain {}", ctx.channel(), count);
    }

    public CompletableFuture<Void> waitAllChannelClosed() {
        if (count.get() == 0) {
            return CompletableFuture.completedFuture(null);
        }
        CompletableFuture<Void> promise = new CompletableFuture();
        promises.add(promise);
        return promise;
    }
}
