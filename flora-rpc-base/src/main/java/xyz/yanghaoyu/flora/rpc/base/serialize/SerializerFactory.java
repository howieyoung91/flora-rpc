/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.serialize;

public interface SerializerFactory {
    SmartSerializer getSmartSerializer(String name);

    Serializer getSerializer(String name);

    Deserializer getDeserializer(String name);

    SmartSerializer getSmartSerializer(byte code);

    Serializer getSerializer(byte code);

    Deserializer getDeserializer(byte code);
}
