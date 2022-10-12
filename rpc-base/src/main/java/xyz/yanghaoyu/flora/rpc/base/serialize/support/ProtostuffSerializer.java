/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.serialize.support;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import xyz.yanghaoyu.flora.rpc.base.serialize.SmartSerializer;

public class ProtostuffSerializer implements SmartSerializer {
    private static final LinkedBuffer BUFFER = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    public static final  String       NAME   = "PROTOSTUFF";
    public static final  int          CODE   = -4;

    @Override
    public byte[] serialize(Object obj) {
        Schema schema = RuntimeSchema.getSchema(obj.getClass());
        byte[] bytes;
        try {
            bytes = ProtostuffIOUtil.toByteArray(obj, schema, BUFFER);
        } finally {
            BUFFER.clear();
        }
        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T         obj    = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public byte code() {
        return CODE;
    }
}
