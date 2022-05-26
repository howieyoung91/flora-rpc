/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service;

import xyz.yanghaoyu.flora.rpc.base.annotation.RpcRequestAttribute;
import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceReferenceAttribute;
import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceReferenceContext;

public class ServiceReference {
    private RpcRequestAttribute       requestAttribute;
    private ServiceReferenceAttribute serviceReferenceAttribute;
    private ServiceReferenceContext   context;

    public ServiceReference(RpcRequestAttribute rpcRequestAttribute, ServiceReferenceAttribute serviceReferenceAttribute, ServiceReferenceContext context) {
        this.requestAttribute = rpcRequestAttribute;
        this.serviceReferenceAttribute = serviceReferenceAttribute;
        this.context = context;
    }

    public RpcRequestAttribute getRequestAttribute() {
        return requestAttribute;
    }

    public ServiceReferenceAttribute getServiceReferenceAttribute() {
        return serviceReferenceAttribute;
    }

    public ServiceReferenceContext getContext() {
        return context;
    }
}
