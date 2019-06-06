package com.fastlib;

import com.fastlib.annotation.Database;

/**
 * Create by sgfb on 2019/06/05
 * E-Mail:602687446@qq.com
 */
public class Bean{
    @Database(keyPrimary = true,autoincrement = true)
    public int id;
    public String text;

    public Bean(String text) {
        this.text = text;
    }
}
