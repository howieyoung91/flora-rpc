/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.builder;

import xyz.yanghaoyu.flora.rpc.base.compress.CompressorFactory;
import xyz.yanghaoyu.flora.rpc.base.compress.SmartCompressor;
import xyz.yanghaoyu.flora.rpc.base.compress.support.DefaultCompressorService;
import xyz.yanghaoyu.flora.rpc.base.exception.RpcClientException;
import xyz.yanghaoyu.flora.rpc.base.serialize.SerializeService;
import xyz.yanghaoyu.flora.rpc.base.serialize.SerializerFactory;
import xyz.yanghaoyu.flora.rpc.base.serialize.SmartSerializer;
import xyz.yanghaoyu.flora.rpc.base.serialize.support.DefaultSerializeService;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ClientConfigProperties;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ClientConfigurer;
import xyz.yanghaoyu.flora.rpc.base.config.ClientConfig;

import java.util.List;
import java.util.Objects;

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
        String                   defaultSerializer = getDefaultSerializer();
        DefaultSerializeService  serializerService = getSerializers();
        String                   defaultCompressor = getDefaultCompressor();
        DefaultCompressorService compressorService = getCompressorService();
        String                   group             = getGroup();
        String                   namespace         = getNamespace();
        String                   version           = getVersion();

        return new ClientConfig() {
            @Override
            public SerializerFactory serializerFactory() {
                return serializerService;
            }

            @Override
            public SerializeService serializeService() {
                return serializerService;
            }

            @Override
            public CompressorFactory compressorFactory() {
                return compressorService;
            }

            @Override
            public CompressorFactory compressorService() {
                return compressorService;
            }

            @Override
            public String defaultSerializer() {
                return defaultSerializer;
            }

            @Override
            public String defaultCompressor() {
                return defaultCompressor;
            }

            @Override
            public String group() {
                return group;
            }

            @Override
            public String namespace() {
                return namespace;
            }

            @Override
            public String version() {
                return version;
            }
        };
    }

    private DefaultSerializeService serializeService = new DefaultSerializeService();

    public DefaultSerializeService getSerializers() {
        if (configurer != null) {
            List<SmartSerializer> configurerSerializer = configurer.addSerializers();
            if (configurerSerializer != null) {
                for (SmartSerializer serializer : configurerSerializer) {
                    if (serializeService.containsSerializer(serializer.name())) {
                        throw new RpcClientException("fail to build client config, cause: smart serializer [" + serializer.name() + "] has already existed!");
                    }
                    serializeService.addSerializer(serializer);
                }
            }
        }
        return serializeService;
    }

    public String getDefaultSerializer() {
        String serializer = properties.getSerializer();
        if (configurer != null) {
            String serializerByConfigurer = configurer.defaultSerializer();
            if (serializerByConfigurer != null) {
                serializer = serializerByConfigurer;
            }
        }
        Objects.requireNonNull(serializer, "found no serializer");
        return serializer;
    }

    private DefaultCompressorService compressorService = new DefaultCompressorService();

    public DefaultCompressorService getCompressorService() {
        if (configurer != null) {
            List<SmartCompressor> compressors = configurer.addCompressors();
            if (compressors != null) {
                for (SmartCompressor compressor : compressors) {
                    if (compressorService.containsCompressor(compressor.name())) {
                        throw new RpcClientException("fail to build client config, cause: smart compressor [" + compressor.name() + "] has already existed!");
                    }
                    compressorService.addCompressor(compressor);
                }
            }
        }
        return compressorService;
    }

    public String getDefaultCompressor() {
        String compressor = properties.getCompressor();
        if (configurer != null) {
            String compressorByConfigurer = configurer.defaultCompressor();
            if (compressor != null) {
                compressor = compressorByConfigurer;
            }
        }
        Objects.requireNonNull(compressor, "found no default compressor");
        return compressor;
    }

    private String getNamespace() {
        String namespace = properties.getNamespace();
        if (configurer != null) {
            String namespaceByConfigurer = configurer.namespace();
            if (namespaceByConfigurer != null) {
                namespace = namespaceByConfigurer;
            }
        }

        // Objects.requireNonNull(namespace, "found no namespace");
        return namespace;
    }

    private String getGroup() {
        String group = properties.getGroup();
        if (configurer != null) {
            String groupByConfigurer = configurer.group();
            if (groupByConfigurer != null) {
                group = groupByConfigurer;
            }
        }
        return group;
    }

    private String getVersion() {
        String version = properties.getVersion();
        if (configurer != null) {
            String versionByConfigurer = configurer.version();
            if (versionByConfigurer != null) {
                version = versionByConfigurer;
            }
        }
        return version;
    }
}
