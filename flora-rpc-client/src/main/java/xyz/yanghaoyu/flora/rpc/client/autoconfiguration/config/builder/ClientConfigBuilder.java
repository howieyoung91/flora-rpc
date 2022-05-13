/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.builder;

import xyz.yanghaoyu.flora.rpc.base.serialize.SerializeService;
import xyz.yanghaoyu.flora.rpc.base.serialize.SerializerFactory;
import xyz.yanghaoyu.flora.rpc.base.serialize.SmartSerializer;
import xyz.yanghaoyu.flora.rpc.base.serialize.support.DefaultSerializeService;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ClientConfigProperties;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ClientConfigurer;
import xyz.yanghaoyu.flora.rpc.client.config.ClientConfig;

import java.util.List;

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
        DefaultSerializeService serializer        = getSerializers();
        String                  defaultSerializer = getDefaultSerializer();
        return new ClientConfig() {
            @Override
            public SerializerFactory serializerFactory() {
                return serializer;
            }

            @Override
            public SerializeService serializeService() {
                return serializer;
            }

            @Override
            public String defaultSerializer() {
                return defaultSerializer;
            }
        };
    }

    private DefaultSerializeService serializeService = new DefaultSerializeService();

    public DefaultSerializeService getSerializers() {
        if (configurer != null) {
            List<SmartSerializer> configurerSerializer = configurer.addSerializers();
            if (configurerSerializer != null) {
                for (SmartSerializer serializer : configurerSerializer) {
                    serializeService.addSerializer(serializer);
                }
            }
        }
        return serializeService;
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
