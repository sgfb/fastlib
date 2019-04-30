package com.fastlib.image_manager.exception;

/**
 * Create by sgfb on 2019/04/22
 * E-Mail:602687446@qq.com
 */
public class DuplicateLoadException extends IllegalStateException{

    public DuplicateLoadException() {
        super();
    }

    public DuplicateLoadException(String s) {
        super(s);
    }

    public DuplicateLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateLoadException(Throwable cause) {
        super(cause);
    }
}
