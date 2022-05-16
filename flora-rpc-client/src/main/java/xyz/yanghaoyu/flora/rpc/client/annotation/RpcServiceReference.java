/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcServiceReference {
    String EMPTY_NAMESPACE      = "";
    String EMPTY_GROUP          = "";
    String EMPTY_INTERFACE_NAME = "";
    String EMPTY_VERSION        = "";

    String namespace() default EMPTY_NAMESPACE;

    String interfaceName() default EMPTY_INTERFACE_NAME;

    String group() default EMPTY_GROUP;

    String version() default EMPTY_VERSION;
}
