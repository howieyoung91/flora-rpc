/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.compress.support;

import xyz.yanghaoyu.flora.rpc.base.compress.SmartCompressor;
import xyz.yanghaoyu.flora.rpc.base.exception.CompressException;
import xyz.yanghaoyu.flora.rpc.base.exception.DecompressException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipSmartCompressor implements SmartCompressor {
    private static final int    BUFFER_SIZE = 1024 * 4;
    public static final  byte   CODE        = -2;
    public static final  String NAME        = "GZIP";

    @Override
    public byte[] compress(byte[] data) {
        Objects.requireNonNull(data, "GZip compress: data is null!");

        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                GZIPOutputStream gzip = new GZIPOutputStream(out)
        ) {
            gzip.write(data);
            gzip.flush();
            gzip.finish();
            return out.toByteArray();
        } catch (IOException e) {
            throw new CompressException("gzip compress error, cause:", e);
        }
    }

    @Override
    public byte[] decompress(byte[] data) {
        Objects.requireNonNull(data, "GZip decompress: data is null!");

        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data))
        ) {
            byte[] buf = new byte[BUFFER_SIZE];
            int    len = 0;
            while ((len = gzip.read(buf)) > -1) {
                out.write(buf, 0, len);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new DecompressException("gzip compress error, cause:", e);
        }
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
