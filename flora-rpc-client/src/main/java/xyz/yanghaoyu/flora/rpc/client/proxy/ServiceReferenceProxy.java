/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.proxy;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import xyz.yanghaoyu.flora.rpc.base.exception.RpcClientException;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceNotFoundException;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseBody;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcRequestAttribute;
import xyz.yanghaoyu.flora.rpc.client.annotation.ServiceReferenceAttribute;
import xyz.yanghaoyu.flora.rpc.client.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.client.transport.RpcClient;
import xyz.yanghaoyu.flora.rpc.client.transport.RpcRequestConfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ServiceReferenceProxy implements InvocationHandler {
    private static final Snowflake snowflake =
            IdUtil.getSnowflake(0, 0);

    private RpcClient                 rpcClient;
    private ServiceReferenceAttribute serviceRefAttr;
    private RpcRequestAttribute       rpcReqAttr;
    private ServiceDiscovery          discovery;

    public ServiceReferenceProxy(RpcClient rpcClient, ServiceReferenceAttribute serviceRefAttr, RpcRequestAttribute rpcReqAttr, ServiceDiscovery serviceDiscovery) {
        this.rpcClient = rpcClient;
        this.serviceRefAttr = serviceRefAttr;
        this.rpcReqAttr = rpcReqAttr;
        this.discovery = serviceDiscovery;
    }

    // public ServiceReferenceProxy(RpcClient rpcClient, ServiceReferenceAttribute serviceReferenceConfig, RpcRequestAttribute requestConfig) {
    //     this.rpcClient = rpcClient;
    //     this.serviceRefAttr = serviceReferenceConfig;
    //     this.rpcReqAttr = requestConfig;
    // }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(this, args);
        }

        return request(proxy, method, args);
    }

    private Object request(Object proxy, Method method, Object[] args) throws InterruptedException, ExecutionException {
        RpcRequestConfig reqConfig = getRpcRequestConfig(method, args);

        // 在 zookeeper 中发现服务
        InetSocketAddress target = null;
        try {
            target = discovery.discover(reqConfig);
        } catch (ServiceNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        // 发送 rpc 请求
        CompletableFuture<RpcResponseBody> promise = rpcClient.send(reqConfig, target);

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
        reqConfig.setServiceRefAttr(serviceRefAttr);
        reqConfig.setId(snowflake.nextIdStr());

        applyRpcRequestAnnotationConfig(reqConfig);

        return reqConfig;
    }

    private void applyRpcRequestAnnotationConfig(RpcRequestConfig request) {
        if (rpcReqAttr == null) {
            return;
        }

        request.setCompressor(rpcReqAttr.getCompressorName());
        request.setSerializer(rpcReqAttr.getSerializerName());
    }

    @Override
    public String toString() {
        return "ServiceReferenceProxy{" +
               "serviceReferenceConfig=" + serviceRefAttr +
               '}';
    }
}
