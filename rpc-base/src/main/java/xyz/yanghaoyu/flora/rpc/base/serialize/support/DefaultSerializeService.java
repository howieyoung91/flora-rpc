/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.serialize.support;

public class DefaultSerializeService extends AbstractSerializeService {
    public DefaultSerializeService() {
        addSerializer(new KryoSmartSerializer());
        addSerializer(new JsonSmartSerializer());
        addSerializer(new HessianSmartSerializer());
        addSerializer(new ProtostuffSerializer());
    }
}
