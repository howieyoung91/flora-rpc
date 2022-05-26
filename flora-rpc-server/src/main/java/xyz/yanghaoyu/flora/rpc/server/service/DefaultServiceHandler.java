/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.service;


import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestBody;
import xyz.yanghaoyu.flora.rpc.base.service.Service;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.base.transport.RpcResponseConfig;
import xyz.yanghaoyu.flora.rpc.base.annotation.RpcResponseAttribute;

import java.lang.reflect.Method;

public class DefaultServiceHandler implements ServiceHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultUnmarshallerProvider.class);

    private final ServiceRegistry registry;

    public DefaultServiceHandler(ServiceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public RpcResponseConfig handle(RpcRequestBody requestBody) {
        logger.info("handle service request [{}#{}]", requestBody.getServiceName(), requestBody.getMethodName());

        Service service     = registry.getService(requestBody.getServiceName());
        Object  serviceBean = service.getServiceBean();

        Object result = invokeTargetMethod(serviceBean, requestBody);

        RpcResponseConfig    responseConfig    = new RpcResponseConfig();
        RpcResponseAttribute responseAttribute = service.getResponseAttribute();
        responseConfig.setSerializer(responseAttribute.getSerializerName());
        responseConfig.setCompressor(responseAttribute.getCompressorName());
        responseConfig.setData(result);
        return responseConfig;
    }

    private Object invokeTargetMethod(Object serviceBean, RpcRequestBody request) {
        String     methodName = request.getMethodName();
        Class<?>[] paramTypes = request.getParamTypes();
        try {
            Method targetMethod = serviceBean.getClass().getDeclaredMethod(methodName, paramTypes);
            return targetMethod.invoke(serviceBean, request.getParams());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
