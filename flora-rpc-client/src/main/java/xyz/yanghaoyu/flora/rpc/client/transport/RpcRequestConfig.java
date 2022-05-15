/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.transport;

import xyz.yanghaoyu.flora.rpc.client.annotation.ServiceReferenceAttribute;

public class RpcRequestConfig {
    private String                    id;
    private String                    methodName;
    private Object[]                  params;
    private Class<?>[]                paramTypes;
    private String                    serializer;
    private String                    compressor;
    private String                    loadBalance;
    private ServiceReferenceAttribute serviceRefAttr;

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

    public ServiceReferenceAttribute getServiceRefAttr() {
        return serviceRefAttr;
    }

    public void setServiceRefAttr(ServiceReferenceAttribute serviceRefAttr) {
        this.serviceRefAttr = serviceRefAttr;
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
}
