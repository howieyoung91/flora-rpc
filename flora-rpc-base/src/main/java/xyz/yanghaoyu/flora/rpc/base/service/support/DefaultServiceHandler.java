/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service.support;


import xyz.yanghaoyu.flora.rpc.base.service.config.Service;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DefaultServiceHandler implements ServiceHandler {
    private ServiceRegistry registry;

    public DefaultServiceHandler(ServiceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Object handle(RpcRequest request) {
        Service service     = registry.getService(request.getServiceConfig().getServiceName());
        Object  serviceBean = service.getServiceBean();

        return invokeTargetMethod(serviceBean, request);
    }

    private Object invokeTargetMethod(Object serviceBean, RpcRequest request) {
        String     methodName = request.getMethodName();
        Class<?>[] paramTypes = request.getParamTypes();
        try {
            Method targetMethod = serviceBean.getClass()
                    .getDeclaredMethod(methodName, paramTypes);
            return targetMethod.invoke(serviceBean, request.getParams());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
