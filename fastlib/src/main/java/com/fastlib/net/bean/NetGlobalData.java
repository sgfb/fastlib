package com.fastlib.net.bean;

import android.support.v4.util.Pair;

import com.fastlib.net.Request;

import java.util.List;

/**
 * Created by sgfb on 17/5/14.
 * 网络全局数据
 */
public class NetGlobalData{
    public Request.ExtraHeader[] mHeads;
    public Pair<String,String>[] mParams;
    public List<Pair<String, String>> mCookies;
}