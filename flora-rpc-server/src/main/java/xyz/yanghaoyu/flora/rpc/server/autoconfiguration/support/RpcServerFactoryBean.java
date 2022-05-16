/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.autoconfiguration.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.annotation.Component;
import xyz.yanghaoyu.flora.annotation.Inject;
import xyz.yanghaoyu.flora.core.beans.factory.BeanFactory;
import xyz.yanghaoyu.flora.core.beans.factory.BeanFactoryAware;
import xyz.yanghaoyu.flora.core.beans.factory.ConfigurableListableBeanFactory;
import xyz.yanghaoyu.flora.core.beans.factory.FactoryBean;
import xyz.yanghaoyu.flora.core.beans.factory.config.BeanDefinition;
import xyz.yanghaoyu.flora.core.beans.factory.support.InitializingBean;
import xyz.yanghaoyu.flora.exception.BeansException;
import xyz.yanghaoyu.flora.rpc.server.annotation.RpcResponse;
import xyz.yanghaoyu.flora.rpc.server.annotation.RpcResponseAttribute;
import xyz.yanghaoyu.flora.rpc.server.annotation.RpcService;
import xyz.yanghaoyu.flora.rpc.server.annotation.ServiceAttribute;
import xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.ServerConfigProperties;
import xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.ServerConfigurer;
import xyz.yanghaoyu.flora.rpc.server.autoconfiguration.config.builder.ServerConfigBuilder;
import xyz.yanghaoyu.flora.rpc.server.config.ServerConfig;
import xyz.yanghaoyu.flora.rpc.server.service.Service;
import xyz.yanghaoyu.flora.rpc.server.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.server.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.server.transport.RpcServer;
import xyz.yanghaoyu.flora.util.ReflectUtil;

import java.util.Objects;

@Component(RpcServerFactoryBean.BEAN_NAME)
public class RpcServerFactoryBean
        implements FactoryBean<RpcServer>, BeanFactoryAware, InitializingBean {
    private static final Logger loggerFactory = LoggerFactory.getLogger(RpcServerFactoryBean.class);
    public static final  String BEAN_NAME     = "floraRpcServer$RpcServer$";

    private ConfigurableListableBeanFactory beanFactory;

    @Inject.ByName("floraRpcServer$ServiceRegistry$")
    private ServiceRegistry        registry;
    @Inject.ByName("floraRpcServer$ServiceHandler$")
    private ServiceHandler         handler;
    @Inject.ByType(required = false)
    private ServerConfigurer       configurer;
    @Inject.ByName(ServerConfigProperties.BEAN_NAME)
    private ServerConfigProperties properties;
    private ServerConfig           serverConfig;

    @Override
    public void afterPropertiesSet() {
        this.serverConfig = ServerConfigBuilder.aServerConfig(configurer, properties).build();
    }

    @Override
    public RpcServer getObject() {
        RpcServer server = new RpcServer(serverConfig, registry, handler);

        for (String beanDefName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDef       = beanFactory.getBeanDefinition(beanDefName);
            Class<?>       clazz         = ReflectUtil.getBeanClassFromCglibProxy(beanDef.getBeanClass());
            RpcService     rpcServiceAnn = clazz.getAnnotation(RpcService.class);
            if (rpcServiceAnn == null) {
                continue;
            }

            ServiceAttribute     serviceAttr = getServiceReferenceAttribute(rpcServiceAnn, clazz);
            RpcResponseAttribute rpcRespAttr = getRpcResponseAttribute(clazz);

            Service service = new Service(beanFactory.getBean(beanDefName), serviceAttr, rpcRespAttr);

            loggerFactory.info("publish rpc service [{}]", serviceAttr.getServiceName());
            server.publishService(service);
        }
        return server;
    }


    private ServiceAttribute getServiceReferenceAttribute(
            RpcService serviceAnn, Class<?> clazz
    ) {
        String group = serviceAnn.group();
        if (group.equals(RpcService.EMPTY_GROUP)) {
            String defaultGroup = serverConfig.group();
            Objects.requireNonNull(defaultGroup, "found no group");
            group = defaultGroup;
        }

        String namespace = serviceAnn.namespace();
        if (namespace.equals(RpcService.EMPTY_NAMESPACE)) {
            String defaultNamespace = serverConfig.namespace();
            Objects.requireNonNull(defaultNamespace, "found no namespace");
            namespace = defaultNamespace;
        }

        String interfaceName = serviceAnn.interfaceName();
        if (interfaceName.equals(RpcService.EMPTY_INTERFACE_NAME)) {
            Class<?>[] interfaces = clazz.getInterfaces();
            // 没有实现接口 就直接用类名作为 service name
            // 有接口 就直接用第一个实现的接口名字作为 service name
            interfaceName = interfaces.length == 0 ? clazz.getName() : interfaces[0].getName();
        }

        String version = serviceAnn.version();
        if (version.equals(RpcService.EMPTY_VERSION)) {
            String defaultVersion = serverConfig.version();
            Objects.requireNonNull(defaultVersion, "found no version");
            version = defaultVersion;
        }

        return new ServiceAttribute(namespace, interfaceName, group, version);
    }

    private RpcResponseAttribute getRpcResponseAttribute(
            Class<?> clazz
    ) {
        RpcResponse rpcResponseAnn = clazz.getAnnotation(RpcResponse.class);
        if (rpcResponseAnn == null) {
            RpcResponseAttribute attribute = new RpcResponseAttribute();
            attribute.setCompressorName(serverConfig.defaultCompressor());
            attribute.setSerializerName(serverConfig.defaultSerializer());
            return attribute;
        }
        String compressor = rpcResponseAnn.compressor();
        if (compressor.equals(RpcResponse.EMPTY_COMPRESSOR)) {
            String defaultCompressor = serverConfig.defaultCompressor();
            Objects.requireNonNull(defaultCompressor, "found no compressor");
            compressor = defaultCompressor;
        }

        String serializer = rpcResponseAnn.serializer();
        if (serializer.equals(RpcResponse.EMPTY_SERIALIZER)) {
            String defaultSerializer = serverConfig.defaultSerializer();
            Objects.requireNonNull(defaultSerializer, "found no serializer");
            serializer = defaultSerializer;
        }

        RpcResponseAttribute attribute = new RpcResponseAttribute();
        attribute.setCompressorName(compressor);
        attribute.setSerializerName(serializer);
        return attribute;
    }

    // private RpcResponseAttribute getRpcResponseAttribute(Class<?> clazz) {
    //     RpcResponse rpcResponseAnn = clazz.getAnnotation(RpcResponse.class);
    //
    //     RpcResponseAttribute rpcRespAttr = new RpcResponseAttribute();
    //     if (rpcResponseAnn == null) {
    //         rpcRespAttr.setSerializerName(serverConfig.defaultSerializer());
    //     } else {
    //         rpcRespAttr = ServiceUtil.buildRpcResponseAttribute(rpcResponseAnn);
    //     }
    //
    //     return rpcRespAttr;
    // }

    @Override
    public Class<?> getObjectType() {
        return RpcService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }
}
