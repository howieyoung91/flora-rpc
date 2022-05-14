/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.serialize;

import xyz.yanghaoyu.flora.rpc.base.exception.DeserializeException;
import xyz.yanghaoyu.flora.rpc.base.exception.SerializeException;

public interface SerializeService {
    byte[] serialize(Object o, String name) throws Exception;

    <T> T deserialize(byte[] data, Class<T> clazz, String name) throws SerializeException;

    <T> T deserialize(byte[] data, Class<T> clazz, byte code) throws DeserializeException;
}
