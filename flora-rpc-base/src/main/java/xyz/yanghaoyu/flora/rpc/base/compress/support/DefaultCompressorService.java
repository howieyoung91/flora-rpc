/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.compress.support;

public class DefaultCompressorService extends AbstractCompressorService {
    public DefaultCompressorService() {
        addCompressor(new NoCompressSmartCompressor());
        addCompressor(new GzipCompressor());
    }
}
