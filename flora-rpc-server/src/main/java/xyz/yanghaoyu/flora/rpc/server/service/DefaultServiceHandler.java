/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.service;


import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.server.config.RpcResponseAnnotationConfig;
import xyz.yanghaoyu.flora.rpc.server.config.Service;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestBody;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DefaultServiceHandler implements ServiceHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultUnmarshallerProvider.class);

    private ServiceRegistry registry;


    public DefaultServiceHandler(ServiceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public RpcResponseConfig handle(RpcRequestBody requestData) {
        logger.info("handle service request [{}#{}]", requestData.getServiceName(), requestData.getMethodName());

        Service service     = registry.getService(requestData.getServiceName());
        Object  serviceBean = service.getServiceBean();

        Object                      result            = invokeTargetMethod(serviceBean, requestData);
        RpcResponseAnnotationConfig rpcResponseConfig = service.getRpcResponseConfig();

        RpcResponseConfig response = new RpcResponseConfig();
        response.setSerializer(rpcResponseConfig.getSerializerName());
        response.setBody(result);
        return response;
    }

    private Object invokeTargetMethod(Object serviceBean, RpcRequestBody request) {
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
