/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.proxy;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import xyz.yanghaoyu.flora.rpc.base.exception.RpcClientException;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceNotFoundException;
import xyz.yanghaoyu.flora.rpc.client.annotation.ServiceReferenceAttribute;
import xyz.yanghaoyu.flora.rpc.client.transport.RpcRequestConfig;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseBody;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcRequestAttribute;
import xyz.yanghaoyu.flora.rpc.client.transport.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ServiceReferenceProxy implements InvocationHandler {
    private static final Snowflake snowflake =
            IdUtil.getSnowflake(0, 0);

    private RpcClient                  rpcClient;
    private ServiceReferenceAttribute serviceReferenceConfig;
    private RpcRequestAttribute       requestConfig;

    public ServiceReferenceProxy(RpcClient rpcClient, ServiceReferenceAttribute serviceReferenceConfig, RpcRequestAttribute requestConfig) {
        this.rpcClient = rpcClient;
        this.serviceReferenceConfig = serviceReferenceConfig;
        this.requestConfig = requestConfig;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(this, args);
        }

        return request(proxy, method, args);
    }

    private Object request(Object proxy, Method method, Object[] args) throws InterruptedException, ExecutionException {
        RpcRequestConfig reqConfig = getRpcRequestConfig(method, args);

        CompletableFuture<RpcResponseBody> promise;

        try {
            promise = rpcClient.send(reqConfig);
        } catch (ServiceNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        // 等待服务器响应，代码阻塞在这里
        RpcResponseBody response = promise.get();

        checkResponse(reqConfig, response);
        return response.getData();
    }

    private void checkResponse(RpcRequestConfig request, RpcResponseBody response) {
        if (response == null) {
            throw new RpcClientException("response is null. method: " + request.getMethodName());
        }
        if (!Objects.equals(request.getId(), response.getRequestId())) {
            throw new RpcClientException("not matched response. method: " + request.getMethodName());
        }
        if (response.getCode() != 200) {
            throw new RpcClientException("something in server went wrong. method: " + request.getMethodName());
        }
    }

    private RpcRequestConfig getRpcRequestConfig(Method method, Object[] args) {
        RpcRequestConfig reqConfig = new RpcRequestConfig();

        // service reference config
        reqConfig.setMethodName(method.getName());
        reqConfig.setParamTypes(method.getParameterTypes());
        reqConfig.setParams(args);
        reqConfig.setServiceReferenceConfig(serviceReferenceConfig);
        reqConfig.setId(snowflake.nextIdStr());

        applyRpcRequestAnnotationConfig(reqConfig);

        return reqConfig;
    }

    private void applyRpcRequestAnnotationConfig(RpcRequestConfig request) {
        if (requestConfig == null) {
            return;
        }

        request.setCompressor(requestConfig.getCompressorName());
        request.setSerializer(requestConfig.getSerializerName());
    }

    @Override
    public String toString() {
        return "ServiceReferenceProxy{" +
               "serviceReferenceConfig=" + serviceReferenceConfig +
               '}';
    }
}
