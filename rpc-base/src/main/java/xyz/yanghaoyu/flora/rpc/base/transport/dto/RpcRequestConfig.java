package xyz.yanghaoyu.flora.rpc.base.transport.dto;

import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceReferenceAttribute;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/10/08 13:10]
 */
public interface RpcRequestConfig {
    String id();

    ServiceReferenceAttribute serviceReferenceAttribute();

    String serviceName();

    String methodName();

    Class<?>[] paramTypes();

    Object[] args();

    String serializerName();

    String compressorName();

    boolean isAlwaysRemote();

    String loadBalance();
}
