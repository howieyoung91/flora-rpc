/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.transport.dto;

import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceReferenceAttribute;

import java.lang.reflect.Method;

public class RpcRequestConfig {
    private       String                    id;
    private final Method                    method;
    private       String                    methodName;
    private       Object[]                  params;
    private       Class<?>[]                paramTypes;
    private       String                    serializer;
    private       String                    compressor;
    private       String                    loadBalance;
    private       boolean                   alwaysRemote;
    private       ServiceReferenceAttribute serviceReferenceAttribute;

    public RpcRequestConfig(Method method) {
        this.method = method;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ServiceReferenceAttribute getServiceReferenceAttribute() {
        return serviceReferenceAttribute;
    }

    public void setServiceReferenceAttribute(ServiceReferenceAttribute serviceReferenceAttribute) {
        this.serviceReferenceAttribute = serviceReferenceAttribute;
    }

    public String getSerializer() {
        return serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public String getCompressor() {
        return compressor;
    }

    public void setCompressor(String compressor) {
        this.compressor = compressor;
    }

    public String getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(String loadBalance) {
        this.loadBalance = loadBalance;
    }

    public boolean isAlwaysRemote() {
        return alwaysRemote;
    }

    public void setAlwaysRemote(boolean alwaysRemote) {
        this.alwaysRemote = alwaysRemote;
    }

    public Method getMethod() {
        return method;
    }
}
