/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.compress.support;

import xyz.yanghaoyu.flora.rpc.base.compress.*;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractCompressorService
        implements CompressService, CompressorRegistry, CompressorFactory {
    private final Map<String, SmartCompressor> compressorsByName = new ConcurrentHashMap<>();
    private final Map<Byte, SmartCompressor>   compressorsByCode = new ConcurrentHashMap<>();

    @Override
    public byte[] compress(byte[] data, String name) throws Exception {
        return getSmartCompressorNonNull(name).compress(data);
    }

    @Override
    public byte[] decompress(byte[] data, String name) throws Exception {
        return getSmartCompressorNonNull(name).decompress(data);
    }

    @Override
    public void addCompressor(SmartCompressor compressor) {
        add(compressor);
    }

    @Override
    public void addCompressor(SmartCompressor... compressors) {
        for (SmartCompressor compressor : compressors) {
            addCompressor(compressor);
        }
    }

    @Override
    public boolean containsCompressor(String compressorName) {
        return compressorsByName.containsKey(compressorName);
    }

    @Override
    public SmartCompressor getSmartCompressor(String name) {
        return compressorsByName.get(name);
    }

    @Override
    public Compressor getCompressor(String name) {
        return getSmartCompressor(name);
    }

    @Override
    public Decompressor getDecompressor(String name) {
        return getSmartCompressor(name);
    }

    @Override
    public SmartCompressor getSmartCompressor(byte code) {
        return compressorsByCode.get(code);
    }

    @Override
    public Compressor getCompressor(byte code) {
        return getSmartCompressor(code);
    }

    @Override
    public Decompressor getDecompressor(byte code) {
        return getSmartCompressor(code);
    }

    private void add(SmartCompressor compressor) {
        compressorsByName.put(compressor.name(), compressor);
        compressorsByCode.put(compressor.code(), compressor);
    }

    private SmartCompressor getSmartCompressorNonNull(String name) {
        SmartCompressor compressor = getSmartCompressor(name);
        Objects.requireNonNull(compressor, "unknown smart compressor [" + name + "]");
        return compressor;
    }

    private SmartCompressor getSmartCompressorNonNull(Byte code) {
        SmartCompressor compressor = getSmartCompressor(code);
        Objects.requireNonNull(compressor, "unknown smart compressor with code [" + code + "]");
        return compressor;
    }
}
