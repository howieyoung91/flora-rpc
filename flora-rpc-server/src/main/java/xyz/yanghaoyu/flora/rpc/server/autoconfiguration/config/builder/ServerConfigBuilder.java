/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.builder;

import xyz.yanghaoyu.flora.rpc.base.compress.CompressorFactory;
import xyz.yanghaoyu.flora.rpc.base.compress.SmartCompressor;
import xyz.yanghaoyu.flora.rpc.base.compress.support.DefaultCompressorService;
import xyz.yanghaoyu.flora.rpc.base.exception.RpcServerException;
import xyz.yanghaoyu.flora.rpc.base.serialize.SerializeService;
import xyz.yanghaoyu.flora.rpc.base.serialize.SerializerFactory;
import xyz.yanghaoyu.flora.rpc.base.serialize.SmartSerializer;
import xyz.yanghaoyu.flora.rpc.base.serialize.support.DefaultSerializeService;
import xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.ServerConfigProperties;
import xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.ServerConfigurer;
import xyz.yanghaoyu.flora.rpc.base.config.ServerConfig;

import java.util.List;
import java.util.Objects;

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
        Integer                  port              = determinePort();
        String                   defaultSerializer = getDefaultSerializer();
        DefaultSerializeService  serializerService = getSerializerService();
        String                   defaultCompressor = getDefaultCompressor();
        DefaultCompressorService compressorService = getCompressorService();
        String                   namespace         = getNamespace();
        String                   group             = getGroup();
        String                   version           = getVersion();
        return new ServerConfig() {
            @Override
            public int port() {
                return port;
            }

            @Override
            public SerializerFactory serializerFactory() {
                return serializerService;
            }

            @Override
            public SerializeService serializeService() {
                return serializerService;
            }

            @Override
            public String defaultSerializer() {
                return defaultSerializer;
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
            public String defaultCompressor() {
                return defaultCompressor;
            }

            @Override
            public String namespace() {
                return namespace;
            }

            @Override
            public String group() {
                return group;
            }

            @Override
            public String version() {
                return version;
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
        Objects.requireNonNull(port, "found no port");
        return port;
    }


    public DefaultSerializeService getSerializerService() {
        DefaultSerializeService serializeService = new DefaultSerializeService();
        if (configurer != null) {
            List<SmartSerializer> configurerSerializer = configurer.addSerializers();
            if (configurerSerializer != null) {
                for (SmartSerializer serializer : configurerSerializer) {
                    if (serializeService.containsSerializer(serializer.name())) {
                        throw new RpcServerException("fail to build server config, cause: smart serializer [" + serializer.name() + "] has already existed!");
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
        return serializer;
    }


    public DefaultCompressorService getCompressorService() {
        DefaultCompressorService compressorService = new DefaultCompressorService();
        if (configurer != null) {
            List<SmartCompressor> compressors = configurer.addCompressors();
            if (compressors != null) {
                for (SmartCompressor compressor : compressors) {
                    if (compressorService.containsCompressor(compressor.name())) {
                        throw new RpcServerException("fail to build server config, cause: smart compressor [" + compressor.name() + "] has already existed!");
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
            if (compressorByConfigurer != null) {
                compressor = compressorByConfigurer;
            }
        }
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

        Objects.requireNonNull(namespace, "found no namespace");
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
