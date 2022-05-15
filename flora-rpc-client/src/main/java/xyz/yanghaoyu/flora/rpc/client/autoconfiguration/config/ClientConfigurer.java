/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config;

import xyz.yanghaoyu.flora.rpc.base.compress.SmartCompressor;
import xyz.yanghaoyu.flora.rpc.base.serialize.SmartSerializer;

import java.util.List;

public interface ClientConfigurer {
    default List<SmartSerializer> addSerializers() {
        return null;
    }

    default List<SmartCompressor> addCompressors() {
        return null;
    }

    default String defaultSerializer() {
        return null;
    }

    default String defaultCompressor() {
        return null;
    }
}
