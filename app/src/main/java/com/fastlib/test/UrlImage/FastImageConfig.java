package com.fastlib.test.UrlImage;

import java.io.File;

/**
 * Created by sgfb on 2017/11/5.
 * 配置清单
 */
public class FastImageConfig implements Cloneable{
    public static final int STORE_STRATEGY_DEFAULT=1;
    public static final int STORE_STRATEGY_NO_MEMORY=2;
    public static final int STORE_STRATEGY_NO_SAVE=3;

    public File mSaveFolder;

    @Override
    protected FastImageConfig clone() throws CloneNotSupportedException{
        return (FastImageConfig) super.clone();
    }
}