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
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcRequest;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcRequestAttribute;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcServiceReference;
import xyz.yanghaoyu.flora.rpc.client.annotation.ServiceReferenceAttribute;
import xyz.yanghaoyu.flora.rpc.client.config.ClientConfig;
import xyz.yanghaoyu.flora.rpc.client.proxy.ServiceReferenceProxyFactory;
import xyz.yanghaoyu.flora.rpc.client.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.client.transport.RpcClient;
import xyz.yanghaoyu.flora.rpc.client.util.RpcServiceClientUtil;
import xyz.yanghaoyu.flora.util.ReflectUtil;

import java.lang.reflect.Field;

@Component("flora-rpc-client$RpcClientStubBeanPostProcessor$")
public class RpcClientStubBeanPostProcessor
        implements SmartInstantiationAwareBeanPostProcessor, BeanFactoryAware {
    private ConfigurableListableBeanFactory beanFactory;

    private volatile RpcClient                    client;
    private          ServiceReferenceProxyFactory proxyFactory;
    private          ServiceDiscovery             discovery;
    private          ClientConfig                 clientConfig;

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

                // handle @RpcRequest
                RpcRequest          rpcRequestAnn = field.getAnnotation(RpcRequest.class);
                RpcRequestAttribute rpcReqAttr    = getRequestAnnotationConfig(rpcRequestAnn);

                ServiceReferenceAttribute serviceRefAttr = RpcServiceClientUtil.buildServiceReferenceAttribute(rpcServiceReferenceAnn, clientConfig, field);

                Object proxy = proxyFactory
                        .getProxy(fieldClass, rpcReqAttr, serviceRefAttr, discovery);

                field.setAccessible(true);
                // inject
                field.set(bean, proxy);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private RpcRequestAttribute getRequestAnnotationConfig(RpcRequest rpcRequestAnn) {
        if (rpcRequestAnn == null) {
            return null;
        }
        return RpcServiceClientUtil.buildRpcRequestAttribute(rpcRequestAnn);
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
        client = beanFactory.getBean("flora-rpc-client$RpcClient$", RpcClient.class);
        proxyFactory = new ServiceReferenceProxyFactory(client);
        discovery = beanFactory.getBean("flora-rpc-client$ServiceDiscovery$", ServiceDiscovery.class);
        clientConfig = beanFactory.getBean("flora-rpc-client$ClientConfig$", ClientConfig.class);
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

