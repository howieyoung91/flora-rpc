/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config;

import xyz.yanghaoyu.flora.rpc.base.serialize.Deserializer;
import xyz.yanghaoyu.flora.rpc.base.serialize.Serializer;

import java.util.HashMap;
import java.util.Map;

public interface ClientConfigurer {
    default Map<String, Serializer> addSerializers() {
        return new HashMap<>(0);
    }

    default Map<String, Deserializer> addDeserializers() {
        return new HashMap<>(0);
    }
}
