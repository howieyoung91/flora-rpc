/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.transport.dto;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import xyz.yanghaoyu.flora.rpc.base.annotation.RpcRequestAttribute;
import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceReferenceAttribute;

import java.util.Objects;

public class DefaultRpcRequestConfig implements RpcRequestConfig {
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(0, 0);

    private final String                    id;
    private       String                    methodName;
    private       Object[]                  args;
    private       Class<?>[]                paramTypes;
    private       String                    loadBalance;      // config
    private       RpcRequestAttribute       rpcRequestAttribute;
    private       ServiceReferenceAttribute serviceReferenceAttribute;

    private DefaultRpcRequestConfig() {
        id = SNOWFLAKE.nextIdStr();
    }

    @Override
    public String methodName() {
        return methodName;
    }

    @Override
    public Object[] args() {
        return args;
    }

    @Override
    public Class<?>[] paramTypes() {
        return paramTypes;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public ServiceReferenceAttribute serviceReferenceAttribute() {
        return serviceReferenceAttribute;
    }

    @Override
    public String serviceName() {
        return serviceReferenceAttribute.getServiceName();
    }

    @Override
    public String serializerName() {
        return rpcRequestAttribute.getSerializerName();
    }

    @Override
    public String compressorName() {
        return rpcRequestAttribute.getCompressorName();
    }

    public String loadBalance() {
        return loadBalance;
    }

    public boolean isAlwaysRemote() {
        return rpcRequestAttribute.isAlwaysRemote();
    }

    public void setRpcRequestAttribute(RpcRequestAttribute rpcRequestAttribute) {
        this.rpcRequestAttribute = rpcRequestAttribute;
    }

    public static final class Builder {
        private DefaultRpcRequestConfig rpcRequestConfig;

        private Builder() {
            rpcRequestConfig = new DefaultRpcRequestConfig();

        }

        public static Builder aRpcRequestConfig() {
            return new Builder();
        }

        public Builder methodName(String methodName) {
            Objects.requireNonNull(methodName);
            rpcRequestConfig.methodName = methodName;
            return this;
        }

        public Builder args(Object[] args) {
            rpcRequestConfig.args = args;
            return this;
        }

        public Builder paramTypes(Class<?>[] paramTypes) {
            rpcRequestConfig.paramTypes = paramTypes;
            return this;
        }

        public Builder loadBalance(String loadBalance) {
            rpcRequestConfig.loadBalance = loadBalance;
            return this;
        }

        public Builder rpcRequestAttribute(RpcRequestAttribute rpcRequestAttribute) {
            Objects.requireNonNull(rpcRequestAttribute);
            rpcRequestConfig.setRpcRequestAttribute(rpcRequestAttribute);
            return this;
        }

        public Builder serviceReferenceAttribute(ServiceReferenceAttribute serviceReferenceAttribute) {
            Objects.requireNonNull(serviceReferenceAttribute);
            Objects.requireNonNull(serviceReferenceAttribute.getServiceName());
            rpcRequestConfig.serviceReferenceAttribute = serviceReferenceAttribute;
            return this;
        }

        public DefaultRpcRequestConfig build() {
            return rpcRequestConfig;
        }
    }
}
