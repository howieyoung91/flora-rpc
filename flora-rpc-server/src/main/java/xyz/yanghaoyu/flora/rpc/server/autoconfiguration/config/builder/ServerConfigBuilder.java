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
import xyz.yanghaoyu.flora.rpc.server.config.ServerConfig;

import java.util.List;

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

    private DefaultSerializeService serializeService = new DefaultSerializeService();

    public DefaultSerializeService getSerializerService() {
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
            if (serializer != null) {
                serializer = serializerByConfigurer;
            }
        }
        if (serializer != null) {
            serializer = "KRYO";
        }
        return serializer;
    }

    private DefaultCompressorService compressorService = new DefaultCompressorService();

    public DefaultCompressorService getCompressorService() {
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
            if (compressor != null) {
                compressor = compressorByConfigurer;
            }
        }
        if (compressor == null) {
            compressor = "NOCOMPRESS";
        }
        return compressor;
    }
}
