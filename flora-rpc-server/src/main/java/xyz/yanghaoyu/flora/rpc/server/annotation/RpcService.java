/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {
    String EMPTY_NAMESPACE      = "";
    String EMPTY_INTERFACE_NAME = "";
    String EMPTY_GROUP          = "";
    String EMPTY_VERSION        = "";
    Class  EMPTY_INTERFACE_TYPE = Object.class;

    String namespace() default EMPTY_NAMESPACE;

    String interfaceName() default EMPTY_INTERFACE_NAME;

    Class interfaceType() default Object.class;

    String group() default EMPTY_GROUP;

    String version() default EMPTY_VERSION;
}
