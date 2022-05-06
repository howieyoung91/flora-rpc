/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc;

import xyz.yanghaoyu.flora.rpc.config.Configuration;
import xyz.yanghaoyu.flora.rpc.transport.Client;
import xyz.yanghaoyu.flora.rpc.transport.Server;

public final class FloraRpcBootstrap {
    private Client        client;
    private Server        server;
    private Configuration configuration;

    private FloraRpcBootstrap(Configuration configuration) {
        this.configuration = configuration;
        this.client = new Client();
        this.server = new Server(configuration);
    }

    public static void start(Configuration configuration) {
        FloraRpcBootstrap floraRpc = new FloraRpcBootstrap(configuration);
        floraRpc.configuration = configuration;
        floraRpc.client = new Client();
    }
}
