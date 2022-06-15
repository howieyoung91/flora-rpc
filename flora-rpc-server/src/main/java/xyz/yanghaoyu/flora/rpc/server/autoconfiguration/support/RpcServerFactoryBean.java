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
import xyz.yanghaoyu.flora.rpc.base.annotation.RpcResponseAttribute;
import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceAttribute;
import xyz.yanghaoyu.flora.rpc.base.config.ServerConfig;
import xyz.yanghaoyu.flora.rpc.base.service.Service;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.base.transport.DefaultRpcRequestHandler;
import xyz.yanghaoyu.flora.rpc.base.transport.interceptor.ServiceInterceptor;
import xyz.yanghaoyu.flora.rpc.server.annotation.RpcService;
import xyz.yanghaoyu.flora.rpc.server.transport.support.AbstractRpcServer;
import xyz.yanghaoyu.flora.rpc.server.transport.support.RpcServerBuilder;
import xyz.yanghaoyu.flora.rpc.server.util.RpcServiceServerUtil;
import xyz.yanghaoyu.flora.util.ReflectUtil;

@Component(RpcServerFactoryBean.BEAN_NAME)
public class RpcServerFactoryBean
        implements FactoryBean<AbstractRpcServer>, InitializingBean, BeanFactoryAware {
    private static final Logger LOGGER    = LoggerFactory.getLogger(RpcServerFactoryBean.class);
    public static final  String BEAN_NAME = "flora-rpc-server$RpcServer$";

    private ConfigurableListableBeanFactory beanFactory;

    @Inject.ByName("flora-rpc-server$ServiceRegistry$")
    private ServiceRegistry registry;
    @Inject.ByName("flora-rpc-server$ServiceHandler$")
    private ServiceHandler  handler;
    @Inject.ByName("flora-rpc-server$ServerConfig$")
    private ServerConfig    config;

    private AbstractRpcServer server;

    @Override
    public void afterPropertiesSet() {
        server = RpcServerBuilder.aServer(config, registry, handler)
                .addInterceptors(beanFactory.getBeansOfType(ServiceInterceptor.class).values())
                .build();
        DefaultRpcRequestHandler requestHandler = server.getRequestHandler();
        beanFactory.registerSingleton("flora-rpc-server$RpcRequestHandler$", requestHandler);
    }

    @Override
    public AbstractRpcServer getObject() {
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDef    = beanFactory.getBeanDefinition(beanName);
            Class<?>       clazz      = ReflectUtil.getBeanClassFromCglibProxyIfNecessary(beanDef.getBeanClass());
            RpcService     serviceAnn = clazz.getAnnotation(RpcService.class);
            if (serviceAnn == null) {
                continue;
            }

            ServiceAttribute     serviceAttribute  = resolveServiceAttribute(serviceAnn, clazz);
            RpcResponseAttribute responseAttribute = resolveResponseAttribute(clazz);

            Service service = new Service(beanFactory.getBean(beanName), serviceAttribute, responseAttribute);

            LOGGER.info("publish rpc service [{}]", serviceAttribute.getServiceName());
            server.publishService(service);
        }
        return server;
    }


    private ServiceAttribute resolveServiceAttribute(RpcService serviceAnn, Class<?> clazz) {
        return RpcServiceServerUtil.buildServiceAttribute(serviceAnn, clazz, config);
    }

    private RpcResponseAttribute resolveResponseAttribute(Class<?> clazz) {
        return RpcServiceServerUtil.buildRpcResponseAttribute(clazz, config);
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
