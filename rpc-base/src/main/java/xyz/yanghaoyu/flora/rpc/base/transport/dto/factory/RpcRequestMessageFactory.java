package xyz.yanghaoyu.flora.rpc.base.transport.dto.factory;

import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcMessage;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.DefaultRpcRequestConfig;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/10/07 23:07]
 * todo
 */
public interface RpcRequestMessageFactory {
    RpcMessage createMessage(DefaultRpcRequestConfig config);
}
