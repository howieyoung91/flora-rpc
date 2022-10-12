package xyz.yanghaoyu.flora.rpc.base.exception;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/10/12 13:07]
 */
public class ServiceKeyException extends ServiceException {
    public ServiceKeyException() {
    }

    public ServiceKeyException(String message) {
        super(message);
    }

    public ServiceKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceKeyException(Throwable cause) {
        super(cause);
    }

    public ServiceKeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
