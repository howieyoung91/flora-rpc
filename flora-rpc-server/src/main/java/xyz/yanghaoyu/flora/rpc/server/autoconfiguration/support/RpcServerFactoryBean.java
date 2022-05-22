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
import xyz.yanghaoyu.flora.rpc.server.util.ServiceUtil;
import xyz.yanghaoyu.flora.util.ReflectUtil;

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
            BeanDefinition beanDef    = beanFactory.getBeanDefinition(beanDefName);
            Class<?>       clazz      = ReflectUtil.getBeanClassFromCglibProxy(beanDef.getBeanClass());
            RpcService     serviceAnn = clazz.getAnnotation(RpcService.class);
            if (serviceAnn == null) {
                continue;
            }

            ServiceAttribute     serviceAttribute  = resolveServiceAttribute(serviceAnn, clazz);
            RpcResponseAttribute responseAttribute = resolveRpcResponseAttribute(clazz);

            Service service = new Service(beanFactory.getBean(beanDefName), serviceAttribute, responseAttribute);

            loggerFactory.info("publish rpc service [{}]", serviceAttribute.getServiceName());
            server.publishService(service);
        }
        return server;
    }


    private ServiceAttribute resolveServiceAttribute(RpcService serviceAnn, Class<?> clazz) {
        return ServiceUtil.buildServiceAttribute(serviceAnn, clazz, serverConfig);
    }

    private RpcResponseAttribute resolveRpcResponseAttribute(Class<?> clazz) {
        return ServiceUtil.buildRpcResponseAttribute(clazz, serverConfig);
    }

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
