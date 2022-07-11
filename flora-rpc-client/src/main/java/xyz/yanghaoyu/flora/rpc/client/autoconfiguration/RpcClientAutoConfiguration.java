/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration;

import xyz.yanghaoyu.flora.annotation.Bean;
import xyz.yanghaoyu.flora.annotation.Configuration;
import xyz.yanghaoyu.flora.annotation.Enable;
import xyz.yanghaoyu.flora.annotation.Inject;
import xyz.yanghaoyu.flora.core.beans.factory.BeanFactory;
import xyz.yanghaoyu.flora.core.beans.factory.BeanFactoryAware;
import xyz.yanghaoyu.flora.core.beans.factory.ConfigurableListableBeanFactory;
import xyz.yanghaoyu.flora.exception.BeansException;
import xyz.yanghaoyu.flora.rpc.base.config.ClientConfig;
import xyz.yanghaoyu.flora.rpc.base.config.ServerConfig;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.base.service.zookeeper.Zookeeper;
import xyz.yanghaoyu.flora.rpc.base.transport.DefaultRpcRequestHandler;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.*;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.builder.ClientConfigBuilder;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.builder.ServiceDiscoveryConfigBuilder;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.builder.ZooKeeperBuilderFactory;
import xyz.yanghaoyu.flora.rpc.client.config.DiscoveryConfig;
import xyz.yanghaoyu.flora.rpc.client.service.discovery.DefaultServiceDiscoveryChain;
import xyz.yanghaoyu.flora.rpc.client.service.discovery.ZookeeperServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.client.transport.support.AbstractRpcClient;
import xyz.yanghaoyu.flora.rpc.client.transport.support.DefaultRpcClient;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

@Configuration(RpcClientAutoConfiguration.BEAN_NAME)
@Enable.PropertySource(location = "classpath:flora-rpc-client.yaml")
@Enable.ComponentScan(basePackages = "xyz.yanghaoyu.flora.rpc.client.autoconfiguration")
public class RpcClientAutoConfiguration implements BeanFactoryAware {
    public static final String BEAN_NAME = "flora-rpc-client$RpcClientAutoConfiguration$";

    private ConfigurableListableBeanFactory beanFactory;

    @Inject.ByName(value = "flora-rpc-server$ServerConfig$", required = false)
    private ServerConfig    serverConfig;
    @Inject.ByName(value = "flora-rpc-server$ServiceRegistry$", required = false)
    private ServiceRegistry registry;
    // @Inject.ByName(value = "flora-rpc-server$ServiceHandler$", required = false)
    // private ServiceHandler  handler;

    @Bean("flora-rpc-client$ZooKeeper$")
    public Zookeeper zooKeeper(
            @Inject.ByType(required = false)
            ZooKeeperConfigurer configurer,
            @Inject.ByName(ZooKeeperConfigProperties.BEAN_NAME)
            ZooKeeperConfigProperties properties
    ) {
        return ZooKeeperBuilderFactory.aZooKeeperConfigBuilder(configurer, properties)
                .build().build();
    }

    @Bean("flora-rpc-client$ServiceDiscoveryConfig$")
    public DiscoveryConfig discoveryConfig(
            @Inject.ByType(required = false)
            ServiceDiscoveryConfigurer configurer,
            @Inject.ByName(ServiceDiscoveryConfigProperties.BEAN_NAME)
            ServiceDiscoveryConfigProperties properties
    ) {
        return ServiceDiscoveryConfigBuilder
                .aServiceDiscoveryConfig(configurer, properties)
                .build();
    }

    @Bean("flora-rpc-client$ServiceDiscovery$")
    public ServiceDiscovery discovery(
            @Inject.ByName("flora-rpc-client$ServiceDiscoveryConfig$")
            DiscoveryConfig discoveryConfig,
            @Inject.ByName("flora-rpc-client$ZooKeeper$")
            Zookeeper zooKeeper
    ) {
        DefaultServiceDiscoveryChain.Builder builder =
                DefaultServiceDiscoveryChain.Builder.aChain()
                        .addDiscovery(new ZookeeperServiceDiscovery(discoveryConfig, zooKeeper));
        if (registry != null) {
            builder.configureLocalServiceDiscovery(registry, serverConfig.port());
        }
        return builder.build();
    }


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
    public AbstractRpcClient rpcClient(
            @Inject.ByName("flora-rpc-client$ClientConfig$")
            ClientConfig clientConfig
    ) {
        DefaultRpcRequestHandler handler   = (DefaultRpcRequestHandler) beanFactory.getSingleton("flora-rpc-server$RpcRequestHandler$");
        InetSocketAddress        localhost = null;
        if (serverConfig != null) {
            try {
                localhost = new InetSocketAddress(Inet4Address.getLocalHost().getHostAddress(), serverConfig.port());
            }
            catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return new DefaultRpcClient(clientConfig, localhost, handler);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }
}
