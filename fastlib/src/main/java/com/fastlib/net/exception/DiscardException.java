package com.fastlib.net.exception;

/**
 * Created by sgfb on 18/6/8.
 * 被网络框架因某种原因判定丢弃这个请求
 */
public class DiscardException extends NetException{

    public DiscardException() {
    }

    public DiscardException(String detailMessage) {
        super(detailMessage);
    }
}
