/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.config;

import xyz.yanghaoyu.flora.rpc.base.compress.CompressorFactory;
import xyz.yanghaoyu.flora.rpc.base.serialize.SerializeService;
import xyz.yanghaoyu.flora.rpc.base.serialize.SerializerFactory;

import java.net.InetSocketAddress;

public interface ServerConfig {
    InetSocketAddress address();

    int port();

    SerializerFactory serializerFactory();

    SerializeService serializeService();

    String defaultSerializer();

    CompressorFactory compressorFactory();

    CompressorFactory compressorService();

    String defaultCompressor();

    String namespace();

    String group();

    String version();
}
