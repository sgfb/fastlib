package com.fastlib.bean;

import com.fastlib.annotation.Database;
import com.fastlib.annotation.NetBeanWrapper;

/**
 * Created by sgfb on 17/5/5.
 * KV数据库使用.单键值表
 */
@NetBeanWrapper
public class StringTable {
    @Database(keyPrimary = true)
    public String key;
    public String value;

    public StringTable() {
    }

    public StringTable(String key, String value) {
        this.key = key;
        this.value = value;
    }
}