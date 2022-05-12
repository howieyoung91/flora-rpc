/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.serialize;

public enum SerializeType {
    KRYO((byte) 0);
    private byte code;

    SerializeType(byte code) {
        this.code = code;
    }

    public byte code() {
        return code;
    }
}
