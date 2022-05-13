/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.serialize.support;

import xyz.yanghaoyu.flora.rpc.base.serialize.*;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSerializeService
        implements SerializeService, SerializerFactory, SerializerRegistry {
    private Map<String, SmartSerializer> serializersByName = new ConcurrentHashMap<>();
    private Map<Byte, SmartSerializer>   serializerByCode  = new ConcurrentHashMap<>();

    @Override
    public void addSerializer(SmartSerializer serializer) {
        add(serializer);
    }

    @Override
    public byte[] serialize(Object o, String name) throws Exception {
        return getSerializerNonNull(name).serialize(name);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz, String name) throws Exception {
        return getSerializerNonNull(name).deserialize(data, clazz);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz, byte code) throws Exception {
        return getSerializerNonNull(code).deserialize(data, clazz);
    }

    @Override
    public SmartSerializer getSmartSerializer(String name) {
        return serializersByName.get(name);
    }

    @Override
    public Serializer getSerializer(String name) {
        return getSmartSerializer(name);
    }

    @Override
    public Deserializer getDeserializer(String name) {
        return getSmartSerializer(name);
    }

    @Override
    public SmartSerializer getSmartSerializer(byte code) {
        return serializerByCode.get(code);
    }

    @Override
    public Serializer getSerializer(byte code) {
        return getSmartSerializer(code);
    }

    @Override
    public Deserializer getDeserializer(byte code) {
        return getSmartSerializer(code);
    }

    private SmartSerializer getSerializerNonNull(byte code) {
        SmartSerializer serializer = getSmartSerializer(code);
        Objects.requireNonNull(serializer, "unknown smart serializer with code [" + code + "]");
        return serializer;
    }

    private SmartSerializer getSerializerNonNull(String name) {
        SmartSerializer serializer = getSmartSerializer(name);
        Objects.requireNonNull(serializer, "unknown smart serializer [" + name + "]");
        return serializer;
    }

    private void add(SmartSerializer serializer) {
        serializersByName.put(serializer.name(), serializer);
        serializerByCode.put(serializer.code(), serializer);
    }
}
