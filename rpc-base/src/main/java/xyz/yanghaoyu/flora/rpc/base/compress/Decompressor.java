/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.compress;

public interface Decompressor {
    byte[] decompress(byte[] data);

    byte code();

    String name();
}
