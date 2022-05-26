/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.support;

import xyz.yanghaoyu.flora.annotation.Component;
import xyz.yanghaoyu.flora.core.beans.factory.BeanFactory;
import xyz.yanghaoyu.flora.core.beans.factory.BeanFactoryAware;
import xyz.yanghaoyu.flora.core.beans.factory.ConfigurableListableBeanFactory;
import xyz.yanghaoyu.flora.core.beans.factory.PropertyValues;
import xyz.yanghaoyu.flora.core.beans.factory.config.InstantiationAwareBeanPostProcessor;
import xyz.yanghaoyu.flora.exception.BeansException;
import xyz.yanghaoyu.flora.rpc.base.annotation.RpcRequestAttribute;
import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceReferenceAttribute;
import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceReferenceContext;
import xyz.yanghaoyu.flora.rpc.client.annotation.*;
import xyz.yanghaoyu.flora.rpc.base.config.ClientConfig;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceReference;
import xyz.yanghaoyu.flora.rpc.client.service.config.ServiceReferenceInterceptor;
import xyz.yanghaoyu.flora.rpc.client.service.proxy.ServiceReferenceProxyFactory;
import xyz.yanghaoyu.flora.rpc.client.transport.RpcClient;
import xyz.yanghaoyu.flora.rpc.client.util.RpcServiceClientUtil;
import xyz.yanghaoyu.flora.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component("flora-rpc-client$RpcClientStubBeanPostProcessor$")
public class RpcClientStubBeanPostProcessor
        implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {
    private ConfigurableListableBeanFactory         beanFactory;
    private RpcClient                               client;
    private ServiceReferenceProxyFactory            proxyFactory;
    private ServiceDiscovery                        discovery;
    private ClientConfig                            clientConfig;
    private Collection<ServiceReferenceInterceptor> interceptors;

    private void initClientIfNecessary() {
        if (client == null) {
            initClient();
        }
    }

    private void initClient() {
        client = beanFactory.getBean("flora-rpc-client$RpcClient$", RpcClient.class);
        proxyFactory = new ServiceReferenceProxyFactory(client);
        discovery = beanFactory.getBean("flora-rpc-client$ServiceDiscovery$", ServiceDiscovery.class);
        clientConfig = beanFactory.getBean("flora-rpc-client$ClientConfig$", ClientConfig.class);
        interceptors = beanFactory.getBeansOfType(ServiceReferenceInterceptor.class).values();
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        Class<?> clazz  = ReflectUtil.getBeanClassFromCglibProxy(bean.getClass());
        Field[]  fields = clazz.getDeclaredFields();

        try {
            for (Field field : fields) {
                Class<?> fieldClass = field.getType();

                RpcServiceReference serviceReferenceAnn = getRpcServiceReferenceAnnotation(field, fieldClass);
                if (serviceReferenceAnn == null) {
                    continue;
                }

                initClientIfNecessary();

                ServiceReferenceAttribute serviceReferenceAttribute = resolveServiceReferenceAttribute(field, serviceReferenceAnn);
                RpcRequestAttribute       requestAttribute          = resolveRpcRequestAttribute(field.getAnnotation(RpcRequest.class));

                List<ServiceReferenceInterceptor> interceptors = selectInterceptors(bean, beanName);
                ServiceReferenceContext           context      = new ServiceReferenceContext(bean, beanName);
                ServiceReference                  reference    = new ServiceReference(requestAttribute, serviceReferenceAttribute, context);

                // create proxy
                Object proxy = proxyFactory.getProxy(fieldClass, reference, discovery, interceptors);

                field.setAccessible(true);
                // inject
                field.set(bean, proxy);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ServiceReferenceAttribute resolveServiceReferenceAttribute(Field field, RpcServiceReference serviceReferenceAnn) {
        return RpcServiceClientUtil.buildServiceReferenceAttribute(serviceReferenceAnn, clientConfig, field);
    }

    private List<ServiceReferenceInterceptor> selectInterceptors(Object bean, String beanName) {
        return interceptors.stream()
                .filter(interceptor -> interceptor.shouldIntercept(bean, beanName))
                .collect(Collectors.toList());
    }

    private RpcRequestAttribute resolveRpcRequestAttribute(RpcRequest requestAnn) {
        return RpcServiceClientUtil.buildRpcRequestAttribute(requestAnn, clientConfig);
    }

    private RpcServiceReference getRpcServiceReferenceAnnotation(Field field, Class<?> fieldClass) {
        if (!fieldClass.isInterface()) {
            return null;
        }
        return field.getAnnotation(RpcServiceReference.class);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }
}

