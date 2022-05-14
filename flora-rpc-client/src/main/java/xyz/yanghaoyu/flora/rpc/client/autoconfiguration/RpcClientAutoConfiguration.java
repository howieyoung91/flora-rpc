/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration;

import xyz.yanghaoyu.flora.annotation.Bean;
import xyz.yanghaoyu.flora.annotation.Configuration;
import xyz.yanghaoyu.flora.annotation.Inject;
import xyz.yanghaoyu.flora.rpc.client.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ClientConfigProperties;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ClientConfigurer;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.builder.ClientConfigBuilder;
import xyz.yanghaoyu.flora.rpc.client.config.ClientConfig;
import xyz.yanghaoyu.flora.rpc.client.transport.RpcClient;

@Configuration("flora-rpc-client$RpcClientAutoConfiguration$")
public class RpcClientAutoConfiguration {


    @Bean("flora-rpc-client$ClientConfig$")
    public ClientConfig clientConfig(
            @Inject.ByType(required = false)
                    ClientConfigurer configurer,
            @Inject.ByName(ClientConfigProperties.BEAN_NAME)
                    ClientConfigProperties properties
    ) {
        return ClientConfigBuilder.aClientConfig(configurer, properties).build();
    }

    @Bean("flora-rpc-client$RpcClient$")
    public RpcClient rpcClient(
            @Inject.ByName("flora-rpc-client$ServiceDiscovery$")
                    ServiceDiscovery discovery,
            @Inject.ByName("flora-rpc-client$ClientConfig$")
                    ClientConfig clientConfig
    ) {
        return new RpcClient(clientConfig, discovery);
    }
}
