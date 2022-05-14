/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.compress;

public interface CompressService {
    byte[] compress(byte[] data, String name) throws Exception;

    byte[] decompress(byte[] data, String name) throws Exception;
}
