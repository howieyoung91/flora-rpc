package xyz.yanghaoyu.flora.rpc.serialize;

public interface Deserializer {
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
