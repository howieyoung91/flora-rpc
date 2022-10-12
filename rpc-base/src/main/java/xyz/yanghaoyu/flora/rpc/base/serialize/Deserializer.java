package xyz.yanghaoyu.flora.rpc.base.serialize;

import xyz.yanghaoyu.flora.rpc.base.exception.DeserializeException;

public interface Deserializer {
    <T> T deserialize(byte[] bytes, Class<T> clazz) throws DeserializeException;

    byte code();

    String name();
}
