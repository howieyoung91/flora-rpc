/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.builder;

import xyz.yanghaoyu.flora.rpc.base.serialize.Deserializer;
import xyz.yanghaoyu.flora.rpc.base.serialize.Serializer;
import xyz.yanghaoyu.flora.rpc.base.serialize.support.KryoSmartSerializer;
import xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.ServerConfigProperties;
import xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.ServerConfigurer;
import xyz.yanghaoyu.flora.rpc.server.config.ServerConfig;

import java.util.HashMap;
import java.util.Map;

public class ServerConfigBuilder {
    private ServerConfigurer       configurer;
    private ServerConfigProperties properties;

    public ServerConfigBuilder(ServerConfigurer configurer, ServerConfigProperties properties) {
        this.configurer = configurer;
        this.properties = properties;
    }

    public static ServerConfigBuilder aServerConfig(ServerConfigurer configurer, ServerConfigProperties properties) {
        return new ServerConfigBuilder(configurer, properties);
    }

    public ServerConfig build() {
        Integer                   port          = determinePort();
        Map<String, Serializer>   serializer    = getSerializers();
        Map<String, Deserializer> deserializers = getDeserializers();
        return new ServerConfig() {
            @Override
            public int port() {
                return port;
            }

            @Override
            public Map<String, Serializer> getSerializers() {
                return serializer;
            }

            @Override
            public Map<String, Deserializer> getDeserializers() {
                return deserializers;
            }
        };
    }

    private Integer determinePort() {
        Integer port = properties.getPort();
        if (configurer != null) {
            Integer portByConfigurer = configurer.port();
            if (portByConfigurer != null) {
                port = portByConfigurer;
            }
        }
        if (port == null) {
            port = 2001;
        }
        return port;
    }

    public Map<String, Serializer> getSerializers() {
        Map<String, Serializer> serializers = new HashMap<>();

        if (configurer != null) {
            Map<String, Serializer> configurerSerializer = configurer.addSerializers();
            serializers.putAll(configurerSerializer);
        }

        serializers.put("KRYO", new KryoSmartSerializer());
        return serializers;
    }

    public Map<String, Deserializer> getDeserializers() {
        Map<String, Deserializer> deserializers = new HashMap<>();

        if (configurer != null) {
            Map<String, Deserializer> configurerSerializer = configurer.addDeserializers();
            deserializers.putAll(configurerSerializer);
        }

        deserializers.put("KRYO", new KryoSmartSerializer());
        return deserializers;
    }
}
