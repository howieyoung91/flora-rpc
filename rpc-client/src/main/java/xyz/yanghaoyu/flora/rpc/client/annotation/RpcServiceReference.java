/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * serviceName -> <group>#<namespace>@<version>
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcServiceReference {
    String EMPTY_NAMESPACE      = "";
    String EMPTY_GROUP          = "";
    String EMPTY_INTERFACE_NAME = "";
    String EMPTY_VERSION        = "";
    Class  EMPTY_INTERFACE_TYPE = Object.class;

    /**
     * rpc service 所在的命名空间
     */
    String namespace() default EMPTY_NAMESPACE;

    /**
     * rpc service 的接口名
     */
    String interfaceName() default EMPTY_INTERFACE_NAME;

    /**
     * rpc service 的接口名（直接使用类进行配置）
     */
    Class interfaceType() default Object.class;

    /**
     * rpc service 的群组
     */
    String group() default EMPTY_GROUP;

    /**
     * rpc service 的版本
     */
    String version() default EMPTY_VERSION;
}
