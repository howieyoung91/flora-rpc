/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.autoconfiguration.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.annotation.Component;
import xyz.yanghaoyu.flora.annotation.Inject;
import xyz.yanghaoyu.flora.annotation.Value;
import xyz.yanghaoyu.flora.core.beans.factory.BeanFactory;
import xyz.yanghaoyu.flora.core.beans.factory.BeanFactoryAware;
import xyz.yanghaoyu.flora.core.beans.factory.ConfigurableListableBeanFactory;
import xyz.yanghaoyu.flora.core.beans.factory.FactoryBean;
import xyz.yanghaoyu.flora.core.beans.factory.config.BeanDefinition;
import xyz.yanghaoyu.flora.exception.BeansException;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.base.service.config.Service;
import xyz.yanghaoyu.flora.rpc.base.service.config.ServiceConfig;
import xyz.yanghaoyu.flora.rpc.server.annotation.RpcService;
import xyz.yanghaoyu.flora.rpc.server.transport.RpcServer;
import xyz.yanghaoyu.flora.rpc.server.transport.RpcServerBuilder;
import xyz.yanghaoyu.flora.rpc.server.util.ServiceUtil;
import xyz.yanghaoyu.flora.util.ReflectUtil;

@Component(RpcServerFactoryBean.BEAN_NAME)
public class RpcServerFactoryBean implements FactoryBean<RpcServer>, BeanFactoryAware {
    private static final Logger loggerFactory = LoggerFactory.getLogger(RpcServerFactoryBean.class);
    public static final  String BEAN_NAME     = "floraRpcServer$RpcServer$";

    private ConfigurableListableBeanFactory beanFactory;

    @Inject.ByName("floraRpcServer$ServiceRegistry$")
    private ServiceRegistry registry;
    @Inject.ByName("floraRpcServer$ServiceHandler$")
    private ServiceHandler  handler;
    @Value("${flora.rpc.server.port}")
    private Integer         port;


    @Override
    public RpcServer getObject() {
        RpcServer server = RpcServerBuilder.aRpcServer(registry, handler)
                .port(port).build();

        String[] beanDefNames = beanFactory.getBeanDefinitionNames();


        for (String beanDefName : beanDefNames) {
            BeanDefinition beanDef = beanFactory.getBeanDefinition(beanDefName);
            // Class<?>       clazz   = beanDef.getBeanClass();
            // clazz = ReflectUtil.isCglibProxyClass(clazz) ? clazz.getSuperclass() : clazz;
            Class<?> clazz = ReflectUtil.getBeanClassFromCglibProxy(
                    beanDef.getBeanClass()
            );
            RpcService rpcServiceAnn = clazz.getAnnotation(RpcService.class);
            if (rpcServiceAnn == null) {
                continue;
            }
            ServiceConfig serviceConfig = ServiceUtil.buildServiceConfig(rpcServiceAnn);
            Service service = new Service(
                    beanFactory.getBean(beanDefName),
                    ServiceUtil.buildServiceConfig(rpcServiceAnn)
            );
            loggerFactory.info("publish rpc service [{}]", serviceConfig);
            server.publishService(service);
        }
        return server;
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
