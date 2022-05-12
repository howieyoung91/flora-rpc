/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.transport.dto;

import xyz.yanghaoyu.flora.rpc.base.service.config.ServiceReferenceConfig;

import java.io.Serializable;

public class RpcRequestConfig implements Serializable {
    private String                 id;
    private String                 methodName;
    private Object[]               params;
    private Class<?>[]             paramTypes;
    private String                 serializerName;
    private ServiceReferenceConfig serviceReferenceConfig;

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

    public ServiceReferenceConfig getServiceReferenceConfig() {
        return serviceReferenceConfig;
    }

    public void setServiceReferenceConfig(ServiceReferenceConfig serviceReferenceConfig) {
        this.serviceReferenceConfig = serviceReferenceConfig;
    }

    public String getSerializerName() {
        return serializerName;
    }

    public void setSerializerName(String serializerName) {
        this.serializerName = serializerName;
    }
}
