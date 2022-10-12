/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.compress.support;

import xyz.yanghaoyu.flora.rpc.base.compress.SmartCompressor;

public class NoCompressSmartCompressor implements SmartCompressor {
    public static final String NAME = "NOCOMPRESS";
    public static final int    CODE = -1;

    @Override
    public byte[] compress(byte[] data) {
        return data;
    }

    @Override
    public byte[] decompress(byte[] data) {
        return data;
    }

    @Override
    public byte code() {
        return CODE;
    }

    @Override
    public String name() {
        return NAME;
    }
}
