/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.service;


import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.annotation.RpcResponseAttribute;
import xyz.yanghaoyu.flora.rpc.base.service.Service;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseConfig;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestBody;

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

        Object            result         = null;
        RpcResponseConfig responseConfig = new RpcResponseConfig();

        try {
            result = invokeTargetMethod(serviceBean, requestBody);
            responseConfig.setData(result);
            responseConfig.setCode(RpcResponseConfig.SUCCESS);
        }
        catch (NoSuchMethodException e) {
            responseConfig.setData(null);
            responseConfig.setCode(RpcResponseConfig.NO_SUCH_METHOD);
            logger.info("{}", e.toString());
        }

        RpcResponseAttribute responseAttribute = service.getResponseAttribute();
        responseConfig.setSerializer(responseAttribute.getSerializerName());
        responseConfig.setCompressor(responseAttribute.getCompressorName());

        return responseConfig;
    }

    private Object invokeTargetMethod(Object serviceBean, RpcRequestBody request) throws NoSuchMethodException {
        String     methodName   = request.getMethodName();
        Class<?>[] paramTypes   = request.getParamTypes();
        Method     targetMethod = serviceBean.getClass().getDeclaredMethod(methodName, paramTypes);
        try {
            return targetMethod.invoke(serviceBean, request.getParams());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
