package com.fastlib.aspect.exception;

/**
 * Created by sgfb on 2020\02\21.
 */
public class LockNotFoundException extends RuntimeException{

    public LockNotFoundException() {
        super();
    }

    public LockNotFoundException(String message) {
        super(message);
    }

    public LockNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockNotFoundException(Throwable cause) {
        super(cause);
    }

    protected LockNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
