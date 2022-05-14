/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.compress;

public interface CompressorFactory {
    SmartCompressor getSmartCompressor(String name);

    Compressor getCompressor(String name);

    Decompressor getDecompressor(String name);

    SmartCompressor getSmartCompressor(byte code);

    Compressor getCompressor(byte code);

    Decompressor getDecompressor(byte code);
}
