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
import xyz.yanghaoyu.flora.core.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import xyz.yanghaoyu.flora.exception.BeansException;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcServiceReference;
import xyz.yanghaoyu.flora.rpc.client.proxy.ServiceReferenceProxyFactory;
import xyz.yanghaoyu.flora.rpc.client.transport.RpcClient;
import xyz.yanghaoyu.flora.rpc.client.util.RpcServiceClientUtil;
import xyz.yanghaoyu.flora.util.ReflectUtil;

import java.lang.reflect.Field;

@Component
public class ClientStubBeanPostProcessor
        implements SmartInstantiationAwareBeanPostProcessor, BeanFactoryAware {
    private          ConfigurableListableBeanFactory beanFactory;
    private volatile RpcClient                       client;
    private          ServiceReferenceProxyFactory    proxyFactory;

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException {

        Class<?> clazz  = ReflectUtil.getBeanClassFromCglibProxy(bean.getClass());
        Field[]  fields = clazz.getDeclaredFields();

        try {
            for (Field field : fields) {
                Class fieldClass = field.getType();
                RpcServiceReference rpcServiceReferenceAnn
                        = getRpcServiceReferenceAnnotation(field, fieldClass);
                if (rpcServiceReferenceAnn == null) {
                    continue;
                }

                initClientIfNecessary();
                // 生成 ClientStub
                Object proxy = proxyFactory.getProxy(
                        fieldClass, RpcServiceClientUtil.buildServiceConfig(rpcServiceReferenceAnn)
                );
                field.setAccessible(true);
                // inject
                field.set(bean, proxy);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initClientIfNecessary() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    initClient();
                }
            }
        }
    }

    private void initClient() {
        client = beanFactory.getBean(RpcClientFactoryBean.BEAN_NAME, RpcClient.class);
        proxyFactory = new ServiceReferenceProxyFactory(client);
    }

    private RpcServiceReference getRpcServiceReferenceAnnotation(
            Field field, Class<? extends Field> fieldClass
    ) {
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

