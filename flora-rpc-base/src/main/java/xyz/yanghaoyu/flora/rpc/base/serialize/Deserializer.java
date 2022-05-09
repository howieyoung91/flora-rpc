package xyz.yanghaoyu.flora.rpc.base.serialize;

public interface Deserializer {
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
