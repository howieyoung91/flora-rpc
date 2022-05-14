package xyz.yanghaoyu.flora.rpc.base.serialize;

import xyz.yanghaoyu.flora.rpc.base.exception.SerializeException;

public interface Serializer {
    byte[] serialize(Object obj) throws SerializeException;

    String name();

    byte code();

}
