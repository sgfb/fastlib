package com.fastlib.net;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sgfb on 17/2/10.
 * 特殊的使用InputStream和OutputStream来调用网络请求
 */
public interface HttpStreamInterface{
    void callback(InputStream in,OutputStream out);
}
