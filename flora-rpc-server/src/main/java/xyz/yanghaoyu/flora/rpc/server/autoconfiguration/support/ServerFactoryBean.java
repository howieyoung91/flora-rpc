/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.autoconfiguration.support;

import xyz.yanghaoyu.flora.annotation.Component;
import xyz.yanghaoyu.flora.annotation.Inject;
import xyz.yanghaoyu.flora.core.beans.factory.BeanFactory;
import xyz.yanghaoyu.flora.core.beans.factory.BeanFactoryAware;
import xyz.yanghaoyu.flora.core.beans.factory.ConfigurableListableBeanFactory;
import xyz.yanghaoyu.flora.core.beans.factory.FactoryBean;
import xyz.yanghaoyu.flora.core.beans.factory.config.BeanDefinition;
import xyz.yanghaoyu.flora.exception.BeansException;
import xyz.yanghaoyu.flora.rpc.base.annotation.RpcService;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.base.service.config.Service;
import xyz.yanghaoyu.flora.rpc.base.service.config.ServiceConfig;
import xyz.yanghaoyu.flora.rpc.server.transport.RpcServer;
import xyz.yanghaoyu.flora.rpc.base.util.ServiceUtil;
import xyz.yanghaoyu.flora.util.ReflectUtil;

@Component("rpcServer")
public class ServerFactoryBean implements FactoryBean<RpcServer>, BeanFactoryAware {
    @Inject.ByName("serverServiceRegistry")
    private ServiceRegistry registry;
    @Inject.ByName("serverServiceHandler")
    private ServiceHandler  handler;

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public RpcServer getObject() {
        RpcServer server = new RpcServer(registry, handler);
        // server.setPort(2181);
        server.setPort(1913);

        String[] beanDefNames = beanFactory.getBeanDefinitionNames();


        for (String beanDefName : beanDefNames) {
            BeanDefinition beanDef = beanFactory.getBeanDefinition(beanDefName);
            Class<?>       clazz   = beanDef.getBeanClass();
            clazz = ReflectUtil.isCglibProxyClass(clazz) ? clazz.getSuperclass() : clazz;
            // Class<?> clazz = ReflectUtil.getBeanClassFromCglibProxy(
            //         beanDef.getBeanClass()
            // );
            RpcService rpcServiceAnn = clazz.getAnnotation(RpcService.class);
            if (rpcServiceAnn == null) {
                continue;
            }
            ServiceConfig serviceConfig = ServiceUtil.buildServiceConfig(rpcServiceAnn);
            Service service = new Service(
                    beanFactory.getBean(beanDefName),
                    ServiceUtil.buildServiceConfig(rpcServiceAnn)
            );
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
