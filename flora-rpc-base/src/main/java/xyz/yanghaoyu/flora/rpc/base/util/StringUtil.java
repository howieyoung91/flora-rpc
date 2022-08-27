/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.util;

import java.util.Objects;

public interface StringUtil {
    static String getStringOrDefaultNonNull(String namespace, String defaultNamespace, String nullMessage) {
        if (Objects.equals(namespace, "")) {
            namespace = defaultNamespace;
        }
        Objects.requireNonNull(namespace, nullMessage);
        return namespace;
    }

    static String getStringOrDefault(String namespace, String defaultNamespace) {
        if (Objects.equals(namespace, "")) {
            namespace = defaultNamespace;
        }
        return namespace;
    }

}
