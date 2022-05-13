/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config;

import xyz.yanghaoyu.flora.rpc.base.serialize.SmartSerializer;

import java.util.ArrayList;
import java.util.List;

public interface ClientConfigurer {
    default List<SmartSerializer> addSerializers() {
        return new ArrayList<>(0);
    }

    default String setDefaultSerializer() {
        return null;
    }
}
