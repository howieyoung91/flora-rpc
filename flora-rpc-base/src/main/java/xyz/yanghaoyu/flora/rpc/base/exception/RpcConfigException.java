/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.exception;

public class RpcConfigException extends RuntimeException {
    public RpcConfigException() {
    }

    public RpcConfigException(String message) {
        super(message);
    }

    public RpcConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcConfigException(Throwable cause) {
        super(cause);
    }

    public RpcConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
