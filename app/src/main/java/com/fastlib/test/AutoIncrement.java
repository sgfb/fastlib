package com.fastlib.test;

import com.fastlib.annotation.DatabaseInject;

/**
 * Created by sgfb on 16/3/21.
 */
public class AutoIncrement{
    @DatabaseInject(keyPrimary = true,autoincrement = true)
    public int id;
    public int i;
}
