package com.fastlib.temp;

import android.util.SparseArray;

import com.fastlib.annotation.Database;

import java.util.Map;

/**
 * Created by sgfb on 17/6/12.
 */
public class Bean{
    @Database(keyPrimary = true,autoincrement = true)
    public int id;
    public String content;
}