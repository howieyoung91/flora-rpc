/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.proxy;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import xyz.yanghaoyu.flora.rpc.base.exception.RpcClientException;
import xyz.yanghaoyu.flora.rpc.base.service.config.ServiceConfig;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequest;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponse;
import xyz.yanghaoyu.flora.rpc.client.transport.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ServiceReferenceProxy implements InvocationHandler {
    private static final Snowflake snowflake =
            IdUtil.getSnowflake(0, 0);

    private ServiceConfig serviceConfig;
    private RpcClient     rpcClient;

    public ServiceReferenceProxy(RpcClient rpcClient, ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
        this.rpcClient = rpcClient;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(this, args);
        }

        return invokeClientStub(proxy, method, args);
    }

    private Object invokeClientStub(Object proxy, Method method, Object[] args) throws InterruptedException, ExecutionException {
        RpcRequest request = getRequest(proxy, method, args);

        CompletableFuture<RpcResponse> promise = rpcClient.send(request);

        // 等待服务器响应，代码阻塞在这里
        RpcResponse response = promise.get();

        if (response == null) {
            throw new RpcClientException("response is null. method: " + request.getMethodName());
        }
        if (!Objects.equals(request.getId(), response.getRequestId())) {
            throw new RpcClientException("not matched response. method: " + request.getMethodName());
        }
        if (response.getCode() != 200) {
            throw new RpcClientException("something in server went wrong. method: " + request.getMethodName());
        }
        return response.getData();
    }

    private RpcRequest getRequest(Object proxy, Method method, Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setMethodName(method.getName());
        request.setParamTypes(method.getParameterTypes());
        request.setParams(args);
        request.setServiceConfig(serviceConfig);
        // req id
        // request.setId(reqId.getAndIncrement());
        request.setId(snowflake.nextIdStr());
        return request;
    }

    @Override
    public String toString() {
        return "ServiceReferenceProxy{" +
               "serviceConfig=" + serviceConfig +
               ", rpcClient=" + rpcClient +
               '}';
    }
}
