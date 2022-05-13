package xyz.yanghaoyu.flora.rpc.base.serialize.support;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import xyz.yanghaoyu.flora.rpc.base.exception.SerializeException;
import xyz.yanghaoyu.flora.rpc.base.serialize.SmartSerializer;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestConfig;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSmartSerializer implements SmartSerializer {
    // 使用 ThreadLocal 隔离一下线程
    private final ThreadLocal<Kryo> KRYO = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequestConfig.class);
        kryo.register(RpcResponseBody.class);
        return kryo;
    });

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (
                ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                Input input = new Input(stream)
        ) {
            Object result = KRYO.get().readObject(input, clazz);
            KRYO.remove();
            return clazz.cast(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SerializeException("fail to deserialize");
        }
    }

    @Override
    public byte[] serialize(Object obj) {
        try (
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Output output = new Output(stream)
        ) {
            KRYO.get().writeObject(output, obj);
            KRYO.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializeException("fail to serialize");
        }
    }

    @Override
    public byte code() {
        return -1;
    }

    @Override
    public String name() {
        return "KRYO";
    }
}
