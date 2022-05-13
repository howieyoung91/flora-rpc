/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.serialize.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.yanghaoyu.flora.rpc.base.serialize.SmartSerializer;

public class JsonSmartSerializer implements SmartSerializer {
    private final ObjectMapper o = new ObjectMapper();

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        return o.readValue(bytes, clazz);
    }

    @Override
    public byte[] serialize(Object obj) throws Exception {
        return o.writeValueAsBytes(obj);
    }

    @Override
    public String name() {
        return "JSON";
    }

    @Override
    public byte code() {
        return -2;
    }
}
