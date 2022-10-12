package xyz.yanghaoyu.flora.rpc.client.service;

import xyz.yanghaoyu.flora.rpc.base.annotation.RpcRequestAttribute;
import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceReferenceAttribute;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceKey;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/10/07 16:05]
 */
public interface GenericService {
    Object $invoke(ServiceReferenceAttribute referenceAttribute, String methodName, Class[] paramTypes, String[] args, RpcRequestAttribute requestAttribute);

    Object $invoke(ServiceReferenceAttribute referenceAttribute, String methodName, Class[] paramTypes, String... args);

    Object $invoke(ServiceKey serviceKey, String methodName, Class[] paramTypes, String... args);
}
