/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config;

import xyz.yanghaoyu.flora.annotation.Component;
import xyz.yanghaoyu.flora.annotation.ConfigurationProperties;

@Component(ClientConfigProperties.BEAN_NAME)
@ConfigurationProperties(prefix = "flora.rpc.client")
public class ClientConfigProperties {
    public static final String BEAN_NAME = "flora-rpc-client$ClientConfigProperties$";

    private String serializer;
    private String compressor;
    private String namespace;
    private String group;
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
