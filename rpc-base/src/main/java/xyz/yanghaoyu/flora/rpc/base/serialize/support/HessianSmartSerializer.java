/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.serialize.support;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import xyz.yanghaoyu.flora.rpc.base.exception.SerializeException;
import xyz.yanghaoyu.flora.rpc.base.serialize.SmartSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSmartSerializer implements SmartSerializer {
    public static final String NAME = "HESSIAN";
    public static final int    CODE = -3;

    @Override
    public byte[] serialize(Object obj) throws SerializeException {
        try {
            ByteArrayOutputStream stream        = new ByteArrayOutputStream();
            HessianOutput         hessianOutput = new HessianOutput(stream);

            hessianOutput.writeObject(obj);
            byte[] data = stream.toByteArray();

            hessianOutput.close();
            stream.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            throw new SerializeException("fail to serialize");
        }
    }


    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            ByteArrayInputStream stream       = new ByteArrayInputStream(bytes);
            HessianInput         hessianInput = new HessianInput(stream);

            Object object = hessianInput.readObject();

            hessianInput.close();
            stream.close();
            return (T) object;
        } catch (Exception e) {
            throw new SerializeException("fail to deserialize");
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
