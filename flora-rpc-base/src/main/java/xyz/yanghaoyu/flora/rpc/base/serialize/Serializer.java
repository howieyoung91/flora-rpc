package xyz.yanghaoyu.flora.rpc.base.serialize;

public interface Serializer {
    byte[] serialize(Object obj) throws Exception;

    String name();

    byte code();

}
