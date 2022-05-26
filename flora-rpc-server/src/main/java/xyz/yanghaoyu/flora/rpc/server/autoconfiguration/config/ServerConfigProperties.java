/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config;

import xyz.yanghaoyu.flora.annotation.Component;
import xyz.yanghaoyu.flora.annotation.Value;

@Component(ServerConfigProperties.BEAN_NAME)
public class ServerConfigProperties {
    public static final String BEAN_NAME = "flora-rpc-server$ServerConfigProperties$";

    @Value("${flora.rpc.server.port}")
    private Integer port;
    @Value("${flora.rpc.server.serializer}")
    private String  serializer;
    @Value("${flora.rpc.server.compressor}")
    private String  compressor;
    @Value("${flora.rpc.server.application.namespace}")
    private String  namespace;
    @Value("${flora.rpc.server.application.group}")
    private String  group;
    @Value("${flora.rpc.server.application.version}")
    private String  version;

    public Integer getPort() {
        return port;
    }

    public String getSerializer() {
        return serializer;
    }

    public String getCompressor() {
        return compressor;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getGroup() {
        return group;
    }

    public String getVersion() {
        return version;
    }
}
