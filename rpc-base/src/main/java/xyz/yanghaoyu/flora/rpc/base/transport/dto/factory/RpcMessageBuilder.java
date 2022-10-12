package xyz.yanghaoyu.flora.rpc.base.transport.dto.factory;

import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcMessage;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/10/07 23:01]
 */
public final class RpcMessageBuilder<T> {
    private RpcMessage<T> rpcMessage;

    private RpcMessageBuilder() {
        rpcMessage = new RpcMessage();
    }

    public static <T> RpcMessageBuilder aRpcMessage() {
        return new RpcMessageBuilder<T>();
    }

    public RpcMessageBuilder type(byte type) {
        rpcMessage.setType(type);
        return this;
    }

    public RpcMessageBuilder serializer(String serializer) {
        rpcMessage.setSerializer(serializer);
        return this;
    }

    public RpcMessageBuilder compressor(String compressor) {
        rpcMessage.setCompressor(compressor);
        return this;
    }

    public RpcMessageBuilder body(T body) {
        rpcMessage.setBody(body);
        return this;
    }

    public RpcMessage build() {
        return rpcMessage;
    }
}
