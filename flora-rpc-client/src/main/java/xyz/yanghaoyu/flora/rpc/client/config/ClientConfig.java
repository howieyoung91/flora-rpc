/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.config;


import xyz.yanghaoyu.flora.rpc.base.serialize.Deserializer;
import xyz.yanghaoyu.flora.rpc.base.serialize.Serializer;

import java.util.Map;

public interface ClientConfig {
    Map<String, Serializer> serializers();

    Map<String, Deserializer> deserializers();

    String defaultSerializer();
}