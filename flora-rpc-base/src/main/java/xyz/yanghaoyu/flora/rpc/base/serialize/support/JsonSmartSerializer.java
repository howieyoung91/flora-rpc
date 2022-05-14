/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.serialize.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.yanghaoyu.flora.rpc.base.exception.DeserializeException;
import xyz.yanghaoyu.flora.rpc.base.exception.SerializeException;
import xyz.yanghaoyu.flora.rpc.base.serialize.SmartSerializer;

import java.io.IOException;

public class JsonSmartSerializer implements SmartSerializer {
    public static final String NAME = "JSON";
    public static final int    CODE = -2;

    private final ObjectMapper o = new ObjectMapper();

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws DeserializeException {
        try {
            return o.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new DeserializeException("fail to deserialize", e);
        }
    }

    @Override
    public byte[] serialize(Object obj) throws SerializeException {
        try {
            return o.writeValueAsBytes(obj);
        } catch (IOException e) {
            throw new SerializeException("fail to serialize", e);
        }
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
