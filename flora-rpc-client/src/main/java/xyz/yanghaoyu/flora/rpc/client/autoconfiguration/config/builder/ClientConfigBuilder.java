/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.builder;

import xyz.yanghaoyu.flora.rpc.base.serialize.Deserializer;
import xyz.yanghaoyu.flora.rpc.base.serialize.Serializer;
import xyz.yanghaoyu.flora.rpc.base.serialize.SmartSerializer;
import xyz.yanghaoyu.flora.rpc.base.serialize.support.KryoSmartSerializer;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ClientConfigProperties;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ClientConfigurer;
import xyz.yanghaoyu.flora.rpc.client.config.ClientConfig;

import java.util.HashMap;
import java.util.Map;

public class ClientConfigBuilder {
    private ClientConfigurer       configurer;
    private ClientConfigProperties properties;

    private ClientConfigBuilder(ClientConfigurer configurer, ClientConfigProperties properties) {
        this.configurer = configurer;
        this.properties = properties;
    }

    public static ClientConfigBuilder aClientConfig(ClientConfigurer configurer, ClientConfigProperties properties) {
        return new ClientConfigBuilder(configurer, properties);
    }

    public ClientConfig build() {
        Map<String, Serializer>   serializer        = getSerializers();
        Map<String, Deserializer> deserializers     = getDeserializers();
        String                    defaultSerializer = getDefaultSerializer();
        return new ClientConfig() {
            @Override
            public Map<String, Serializer> serializers() {
                return serializer;
            }

            @Override
            public Map<String, Deserializer> deserializers() {
                return deserializers;
            }

            @Override
            public String defaultSerializer() {
                return defaultSerializer;
            }
        };
    }

    private final SmartSerializer kryoSmartSerializer = new KryoSmartSerializer();

    public Map<String, Serializer> getSerializers() {
        Map<String, Serializer> serializers = new HashMap<>();

        if (configurer != null) {
            Map<String, Serializer> configurerSerializer = configurer.addSerializers();
            serializers.putAll(configurerSerializer);
        }

        serializers.put("KRYO", kryoSmartSerializer);
        return serializers;
    }

    public Map<String, Deserializer> getDeserializers() {
        Map<String, Deserializer> serializers = new HashMap<>();

        if (configurer != null) {
            Map<String, Deserializer> configurerSerializer = configurer.addDeserializers();
            serializers.putAll(configurerSerializer);
        }

        serializers.put("KRYO", kryoSmartSerializer);
        return serializers;
    }

    public String getDefaultSerializer() {
        String serializer = properties.getSerializer();
        if (configurer != null) {
            String serializerByConfigurer = configurer.setDefaultSerializer();
            if (serializer != null) {
                serializer = serializerByConfigurer;
            }
        }
        if (serializer == null) {
            serializer = "KRYO";
        }
        return serializer;
    }
}
