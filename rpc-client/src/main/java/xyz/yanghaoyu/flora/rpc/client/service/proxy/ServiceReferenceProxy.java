/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.service.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.framework.core.OrderComparator;
import xyz.yanghaoyu.flora.rpc.base.annotation.RpcRequestAttribute;
import xyz.yanghaoyu.flora.rpc.base.exception.RpcClientException;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceNotFoundException;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceReference;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.DefaultRpcRequestConfig;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseBody;
import xyz.yanghaoyu.flora.rpc.client.service.config.DiscoveryAwareServiceReferenceInterceptor;
import xyz.yanghaoyu.flora.rpc.client.service.config.ServiceReferenceInterceptor;
import xyz.yanghaoyu.flora.rpc.client.transport.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

public class ServiceReferenceProxy implements InvocationHandler {
    private static final Logger    LOGGER    = LoggerFactory.getLogger(ServiceReferenceProxy.class);

    private RpcClient                                      client;
    private ServiceReference                               reference;
    private ServiceDiscovery                               discovery;
    private Set<ServiceReferenceInterceptor>               interceptors               = new TreeSet<>(OrderComparator.INSTANCE);
    private Set<DiscoveryAwareServiceReferenceInterceptor> discoveryAwareInterceptors = new TreeSet<>(OrderComparator.INSTANCE);

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
        DefaultRpcRequestConfig requestConfig = buildRpcRequestConfig(method, args);
        InetSocketAddress       target        = null;

        target = applyInterceptorsBeforeServiceDiscover(requestConfig);

        if (target == null) {
            try {
                target = discovery.discover(requestConfig);  // 在 zookeeper 中发现服务
            }
            catch (ServiceNotFoundException e) {
                e.printStackTrace();
                return applyInterceptorOnNoServiceDiscovered(requestConfig);
            }
        }
        else {
            LOGGER.info("advised service: {}", target);
        }

        target = applyInterceptorsAfterServiceDiscover(target); // 这里可以再一次修改 target
        applyInterceptorsBeforeRequest(target, requestConfig);
        CompletableFuture<RpcResponseBody> promise = client.send(requestConfig, target); // 发送 rpc 请求
        applyInterceptorsAfterRequest(promise, requestConfig);

        RpcResponseBody respBody = promise.get();  // 等待服务器响应，代码阻塞在这里
        applyInterceptorsAfterResponse(respBody);

        checkResponse(requestConfig, respBody);
        return respBody.getData();
    }

    private Object applyInterceptorOnNoServiceDiscovered(DefaultRpcRequestConfig requestConfig) {
        Object result = null;
        for (DiscoveryAwareServiceReferenceInterceptor interceptor : discoveryAwareInterceptors) {
            result = interceptor.onNoServiceDiscovered(requestConfig, reference);
        }
        return result;
    }

    private InetSocketAddress applyInterceptorsAfterServiceDiscover(InetSocketAddress target) {
        return discoveryAwareInterceptors.stream()
                .map(interceptor -> interceptor.afterDiscoverService(target))
                .filter(Objects::nonNull).findFirst().orElse(target);
    }

    private InetSocketAddress applyInterceptorsBeforeServiceDiscover(DefaultRpcRequestConfig reqConfig) {
        for (DiscoveryAwareServiceReferenceInterceptor interceptor : discoveryAwareInterceptors) {
            InetSocketAddress address = interceptor.adviseTargetService(reqConfig);
            if (address != null) {
                return address;
            }
        }
        return null;
    }

    private void applyInterceptorsBeforeRequest(InetSocketAddress target, DefaultRpcRequestConfig requestConfig) {
        for (ServiceReferenceInterceptor interceptor : interceptors) {
            interceptor.beforeRequest(target, requestConfig);
        }
    }

    private void applyInterceptorsAfterRequest(CompletableFuture<?> promise, DefaultRpcRequestConfig requestConfig) {
        for (ServiceReferenceInterceptor interceptor : interceptors) {
            interceptor.afterRequest(promise, requestConfig);
        }
    }

    private void applyInterceptorsAfterResponse(RpcResponseBody responseBody) {
        for (ServiceReferenceInterceptor interceptor : interceptors) {
            interceptor.afterResponse(responseBody);
        }
    }

    private void checkResponse(DefaultRpcRequestConfig requestConfig, RpcResponseBody responseBody) {
        if (responseBody == null) {
            throw new RpcClientException("response is null. method: " + requestConfig.methodName());
        }
        if (!Objects.equals(requestConfig.id(), responseBody.getRequestId())) {
            throw new RpcClientException("not matched response. method: " + requestConfig.methodName());
        }
        if (responseBody.getCode() != 200) {
            throw new RpcClientException("something in server went wrong. method: " + requestConfig.methodName());
        }
    }

    private DefaultRpcRequestConfig buildRpcRequestConfig(Method method, Object[] args) {
        DefaultRpcRequestConfig.Builder builder = DefaultRpcRequestConfig.Builder.aRpcRequestConfig()
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .args(args)
                .serviceReferenceAttribute(reference.getServiceReferenceAttribute());

        RpcRequestAttribute attribute = reference.getRequestAttribute();
        Objects.requireNonNull(attribute);
        builder.rpcRequestAttribute(attribute);
        return builder.build();
    }

    void addServiceInterceptor(List<ServiceReferenceInterceptor> interceptors) {
        for (ServiceReferenceInterceptor interceptor : interceptors) {
            this.interceptors.add(interceptor);
            if (interceptor instanceof DiscoveryAwareServiceReferenceInterceptor) {
                this.discoveryAwareInterceptors.add((DiscoveryAwareServiceReferenceInterceptor) interceptor);
            }
        }
    }
}
