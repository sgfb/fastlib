package com.fastlib;

/**
 * Created by sgfb on 19/2/12.
 * E-mail: 602687446@qq.com
 * 未定义图像源
 */
public class UndefineSourceException extends IllegalArgumentException{

    public UndefineSourceException() {
        super();
    }

    public UndefineSourceException(String s) {
        super(s);
    }
}
