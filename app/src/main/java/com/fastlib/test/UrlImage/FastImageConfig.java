package com.fastlib.test.UrlImage;

import java.io.File;

/**
 * Created by sgfb on 2017/11/5.
 * 全局配置
 */
public class FastImageConfig implements Cloneable{
    public File mSaveFolder;

    @Override
    protected FastImageConfig clone() throws CloneNotSupportedException {
        return (FastImageConfig) super.clone();
    }
}