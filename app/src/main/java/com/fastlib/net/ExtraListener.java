package com.fastlib.net;

/**
 * Created by sgfb on 16/12/28.
 */

public interface ExtraListener<T> extends Listener<T>{
    /**
     * 原始字节数据回调
     */
    void onRawData(byte[] data);

    /**
     * 数据解析成字符串时回调,这个方法运行在子线程中,可以进行一些耗时操作(在Request中可以命令返回原始字节,那么这个方法将不会被回调)
     */
    void onTranslateJson(String json);
}
