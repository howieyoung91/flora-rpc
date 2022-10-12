/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用该注解可以对某一个服务的 RPC 请求进行配置
 * 如果没有使用该注解，将会使用 yaml 的配置
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcRequest {
    boolean DEFAULT_ALWAYS_REMOTE = false;

    /**
     * 指定序列化机制
     *
     * @see xyz.yanghaoyu.flora.rpc.base.serialize.support.KryoSmartSerializer      KRYO
     * @see xyz.yanghaoyu.flora.rpc.base.serialize.support.HessianSmartSerializer   HESSION
     * @see xyz.yanghaoyu.flora.rpc.base.serialize.support.JsonSmartSerializer      JSON
     * @see xyz.yanghaoyu.flora.rpc.base.serialize.support.ProtostuffSerializer     PROTOSTUFF
     */
    String serializer() default "";

    /**
     * 指定压缩机制
     *
     * @see xyz.yanghaoyu.flora.rpc.base.compress.support.GzipSmartCompressor       GZIP
     * @see xyz.yanghaoyu.flora.rpc.base.compress.support.NoCompressSmartCompressor NOCOMPRESS
     */
    String compressor() default "";

    /**
     * 是否强制调用远程服务
     * 启用此选项，如果本地存在目标服务，那么将不会调用本地服务
     * 默认关闭
     */
    boolean alwaysRemote() default DEFAULT_ALWAYS_REMOTE;
}
