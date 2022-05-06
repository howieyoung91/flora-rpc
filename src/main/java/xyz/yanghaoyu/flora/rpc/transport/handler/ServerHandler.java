/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.transport.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import xyz.yanghaoyu.flora.rpc.transport.dto.Message;
import xyz.yanghaoyu.flora.rpc.transport.dto.Request;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Message) {
            Message rpcMsg = (Message) msg;

            Request request = (Request) rpcMsg.getData();
            // handle request

            // send response
        }
    }
}
