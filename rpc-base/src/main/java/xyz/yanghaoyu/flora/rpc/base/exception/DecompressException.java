/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.exception;

public class DecompressException extends RuntimeException {
    public DecompressException() {
    }

    public DecompressException(String message) {
        super(message);
    }

    public DecompressException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecompressException(Throwable cause) {
        super(cause);
    }

    public DecompressException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
