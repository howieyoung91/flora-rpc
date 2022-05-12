/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config;

import xyz.yanghaoyu.flora.annotation.Component;
import xyz.yanghaoyu.flora.annotation.ConfigurationProperties;

@Component(ServerConfigProperties.BEAN_NAME)
@ConfigurationProperties(prefix = "flora.rpc.server")
public class ServerConfigProperties {
    public static final String BEAN_NAME = "flora-rpc-server$ServerConfigProperties$";

    private Integer port;
    private String  serializer;

    public Integer getPort() {
        return port;
    }

    public String getSerializer() {
        return serializer;
    }
}