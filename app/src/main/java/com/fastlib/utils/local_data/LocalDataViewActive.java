package com.fastlib.utils.local_data;

import android.os.Parcelable;
import android.view.View;

import java.io.Serializable;

/**
 * Created by sgfb on 18/3/6.
 * 本地数据与视图注入绑定行为
 * @param <V> 注入视图
 */
public interface LocalDataViewActive<V extends View>{

    void bind(V view,boolean arg);
    void bind(V view,boolean[] arg);

    void bind(V view,char arg);
    void bind(V view,char[] arg);

    void bind(V view,short arg);
    void bind(V view,short[] arg);

    void bind(V view,int arg);
    void bind(V view,int[] arg);

    void bind(V view,byte arg);
    void bind(V view,byte[] arg);

    void bind(V view,float arg);
    void bind(V view,float[] arg);

    void bind(V view,long arg);
    void bind(V view,long[] arg);

    void bind(V view,double arg);
    void bind(V view,double[] arg);

    void bind(V view,String arg);
    void bind(V view,String[] arg);

    void bind(V view, Serializable serializable);
    void bind(V view, Parcelable parcelable);
}