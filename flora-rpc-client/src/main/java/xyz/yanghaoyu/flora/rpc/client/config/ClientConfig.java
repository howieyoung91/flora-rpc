/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.config;


import xyz.yanghaoyu.flora.rpc.base.compress.CompressorFactory;
import xyz.yanghaoyu.flora.rpc.base.serialize.SerializeService;
import xyz.yanghaoyu.flora.rpc.base.serialize.SerializerFactory;

public interface ClientConfig {
    SerializerFactory serializerFactory();

    SerializeService serializeService();

    CompressorFactory compressorFactory();

    CompressorFactory compressorService();

    String defaultSerializer();

    String defaultCompressor();

    String group();

    String namespace();

    String version();
}
