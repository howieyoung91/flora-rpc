package xyz.yanghaoyu.flora.rpc.serialize.support;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import xyz.yanghaoyu.flora.rpc.exception.SerializeException;
import xyz.yanghaoyu.flora.rpc.serialize.SmartSerializer;
import xyz.yanghaoyu.flora.rpc.transport.dto.Request;
import xyz.yanghaoyu.flora.rpc.transport.dto.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements SmartSerializer {
    // 使用 ThreadLocal 隔离一下线程
    private final ThreadLocal<Kryo> KRYO = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(Request.class);
        kryo.register(Response.class);
        return kryo;
    });

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                Input input = new Input(byteArrayInputStream)
        ) {
            Object result = KRYO.get().readObject(input, clazz);
            KRYO.remove();
            return clazz.cast(result);
        } catch (Exception e) {
            throw new SerializeException("fail to deserialize");
        }
    }

    @Override
    public byte[] serialize(Object obj) {
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Output output = new Output(byteArrayOutputStream)
        ) {
            KRYO.get().writeObject(output, obj);
            KRYO.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializeException("fail to serialize");
        }
    }
}
