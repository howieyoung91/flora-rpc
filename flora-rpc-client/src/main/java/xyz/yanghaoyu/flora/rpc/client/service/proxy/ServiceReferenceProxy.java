/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.service.proxy;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import xyz.yanghaoyu.flora.core.OrderComparator;
import xyz.yanghaoyu.flora.rpc.base.exception.RpcClientException;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceNotFoundException;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseBody;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcRequestAttribute;
import xyz.yanghaoyu.flora.rpc.client.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.client.service.ServiceReference;
import xyz.yanghaoyu.flora.rpc.client.service.config.DiscoveryAwareRpcServiceInterceptor;
import xyz.yanghaoyu.flora.rpc.client.service.config.ServiceInterceptor;
import xyz.yanghaoyu.flora.rpc.client.transport.RpcClient;
import xyz.yanghaoyu.flora.rpc.client.transport.RpcRequestConfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

public class ServiceReferenceProxy implements InvocationHandler {
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(0, 0);

    private RpcClient                                client;
    private ServiceReference                         reference;
    private ServiceDiscovery                         discovery;
    private Set<ServiceInterceptor>                  interceptors               = new TreeSet<>(OrderComparator.INSTANCE);
    private Set<DiscoveryAwareRpcServiceInterceptor> discoveryAwareInterceptors = new TreeSet<>(OrderComparator.INSTANCE);

    ServiceReferenceProxy(RpcClient client, ServiceReference serviceReference, ServiceDiscovery discovery) {
        this.client = client;
        this.reference = serviceReference;
        this.discovery = discovery;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(this, args);
        }
        return request(method, args);
    }

    private Object request(Method method, Object[] args) throws Exception {
        RpcRequestConfig  requestConfig = getRpcRequestConfig(method, args);
        InetSocketAddress target        = null;

        target = applyInterceptorsBeforeServiceDiscover(requestConfig);

        if (target == null) {
            // 在 zookeeper 中发现服务
            try {
                target = discovery.discover(requestConfig);
            } catch (ServiceNotFoundException e) {
                e.printStackTrace();
                return applyInterceptorOnNoServiceDiscovered(requestConfig);
            }
        }

        // 这里可以再一次修改 target
        target = applyInterceptorsAfterServiceDiscover(target);

        applyInterceptorsBeforeRequest(target, requestConfig);

        // 发送 rpc 请求
        CompletableFuture<RpcResponseBody> promise = client.send(requestConfig, target);

        applyInterceptorsAfterRequest();

        // 等待服务器响应，代码阻塞在这里
        RpcResponseBody respBody = promise.get();

        applyInterceptorsAfterResponse(respBody);

        checkResponse(requestConfig, respBody);
        return respBody.getData();
    }

    private Object applyInterceptorOnNoServiceDiscovered(RpcRequestConfig requestConfig) {
        Object result = null;
        for (DiscoveryAwareRpcServiceInterceptor interceptor : discoveryAwareInterceptors) {
            result = interceptor.onNoServiceDiscovered(requestConfig, reference);
        }
        return result;
    }

    private InetSocketAddress applyInterceptorsAfterServiceDiscover(InetSocketAddress target) {
        for (DiscoveryAwareRpcServiceInterceptor interceptor : discoveryAwareInterceptors) {
            InetSocketAddress address = interceptor.afterDiscoverService(target);
            if (address != null) {
                return address;
            }
        }
        return target;
    }

    private InetSocketAddress applyInterceptorsBeforeServiceDiscover(RpcRequestConfig reqConfig) {
        for (DiscoveryAwareRpcServiceInterceptor interceptor : discoveryAwareInterceptors) {
            InetSocketAddress address = interceptor.adviseTargetService(reqConfig);
            if (address != null) {
                return address;
            }
        }
        return null;
    }

    private void applyInterceptorsBeforeRequest(InetSocketAddress target, RpcRequestConfig requestConfig) {
        for (ServiceInterceptor interceptor : interceptors) {
            interceptor.beforeRequest(target, requestConfig);
        }
    }

    private void applyInterceptorsAfterRequest() {
        for (ServiceInterceptor interceptor : interceptors) {
            interceptor.afterRequest();
        }
    }

    private void applyInterceptorsAfterResponse(RpcResponseBody responseBody) {
        for (ServiceInterceptor interceptor : interceptors) {
            interceptor.afterResponse(responseBody);
        }
    }

    private void checkResponse(RpcRequestConfig requestConfig, RpcResponseBody responseBody) {
        if (responseBody == null) {
            throw new RpcClientException("response is null. method: " + requestConfig.getMethodName());
        }
        if (!Objects.equals(requestConfig.getId(), responseBody.getRequestId())) {
            throw new RpcClientException("not matched response. method: " + requestConfig.getMethodName());
        }
        if (responseBody.getCode() != 200) {
            throw new RpcClientException("something in server went wrong. method: " + requestConfig.getMethodName());
        }
    }

    private RpcRequestConfig getRpcRequestConfig(Method method, Object[] args) {
        RpcRequestConfig requestConfig = new RpcRequestConfig();

        // service reference config
        requestConfig.setMethodName(method.getName());
        requestConfig.setParamTypes(method.getParameterTypes());
        requestConfig.setParams(args);
        requestConfig.setServiceReferenceAttribute(reference.getServiceReferenceAttribute());
        requestConfig.setId(SNOWFLAKE.nextIdStr());

        applyRpcRequestAnnotationConfig(requestConfig);

        return requestConfig;
    }

    private void applyRpcRequestAnnotationConfig(RpcRequestConfig request) {
        RpcRequestAttribute attribute = reference.getRequestAttribute();
        if (attribute == null) {
            return;
        }

        request.setCompressor(attribute.getCompressorName());
        request.setSerializer(attribute.getSerializerName());
    }

    void addServiceInterceptor(List<ServiceInterceptor> interceptors) {
        for (ServiceInterceptor interceptor : interceptors) {
            this.interceptors.add(interceptor);
            if (interceptor instanceof DiscoveryAwareRpcServiceInterceptor) {
                this.discoveryAwareInterceptors.add((DiscoveryAwareRpcServiceInterceptor) interceptor);
            }
        }
    }
}