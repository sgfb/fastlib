package com.fastlib.net;

import java.io.File;

/**
 * Created by sgfb on 16/2/22.
 */
public interface Downloadable{

    /**
     * 获取下载目标文件位置
     * @return 目标文件
     */
    File getTargetFile();

    void setTargetFile(File f);
}
