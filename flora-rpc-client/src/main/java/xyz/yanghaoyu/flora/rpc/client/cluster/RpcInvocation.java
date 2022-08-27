/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.cluster;

import xyz.yanghaoyu.flora.rpc.base.cluster.Invocation;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestConfig;

public class RpcInvocation implements Invocation {
    private String     serviceName;
    private String     methodName;
    private Object[]   arguments;
    private Class<?>[] paramTypes;

    public RpcInvocation(String serviceName, String methodName, Object[] arguments, Class<?>[] paramTypes) {
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.arguments = arguments;
        this.paramTypes = paramTypes;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public static RpcInvocation of(String serviceName, RpcRequestConfig requestConfig) {
        return new RpcInvocation(serviceName, requestConfig.getMethodName(),
                requestConfig.getParams(), requestConfig.getParamTypes());
    }

}
