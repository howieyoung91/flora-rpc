/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config;

import xyz.yanghaoyu.flora.annotation.Component;
import xyz.yanghaoyu.flora.annotation.Value;

@Component(ClientConfigProperties.BEAN_NAME)
public class ClientConfigProperties {
    public static final String BEAN_NAME = "flora-rpc-client$ClientConfigProperties$";

    @Value("${flora.rpc.client.serializer}")
    private String serializer;
    @Value("${flora.rpc.client.compressor}")
    private String compressor;
    @Value("${flora.rpc.client.application.namespace}")
    private String namespace;
    @Value("${flora.rpc.client.application.group}")
    private String group;
    @Value("${flora.rpc.client.application.version}")
    private String version;

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
