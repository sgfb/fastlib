package com.fastlib.utils.local_data;

import android.os.Parcelable;
import android.view.View;

import java.io.Serializable;

/**
 * Created by sgfb on 18/3/6.
 * 实现和适配掉所有方法{@link LocalDataViewActive}
 */
public abstract class LocalDataViewActiveImpl<V extends View> implements LocalDataViewActive<V>{

    @Override
    public void bind(V view, boolean arg) {

    }

    @Override
    public void bind(V view, boolean[] arg) {

    }

    @Override
    public void bind(V view, char arg) {

    }

    @Override
    public void bind(V view, char[] arg) {

    }

    @Override
    public void bind(V view, short arg) {

    }

    @Override
    public void bind(V view, short[] arg) {

    }

    @Override
    public void bind(V view, int arg) {

    }

    @Override
    public void bind(V view, int[] arg) {

    }

    @Override
    public void bind(V view, byte arg) {

    }

    @Override
    public void bind(V view, byte[] arg) {

    }

    @Override
    public void bind(V view, float arg) {

    }

    @Override
    public void bind(V view, float[] arg) {

    }

    @Override
    public void bind(V view, long arg) {

    }

    @Override
    public void bind(V view, long[] arg) {

    }

    @Override
    public void bind(V view, double arg) {

    }

    @Override
    public void bind(V view, double[] arg) {

    }

    @Override
    public void bind(V view, String arg) {

    }

    @Override
    public void bind(V view, String[] arg) {

    }

    @Override
    public void bind(V view, Serializable serializable) {

    }

    @Override
    public void bind(V view, Parcelable parcelable) {

    }

    public static <V extends View> void inflaterData(LocalDataViewActive<V> active,V v,Object data){
        Class cla=data.getClass();
        if(cla==boolean.class||cla== Boolean.class)
            active.bind(v,(boolean) data);
        else if(cla==boolean[].class||cla==Boolean[].class)
            active.bind(v,(boolean[])data);
        else if(cla==char.class)
            active.bind(v,(char)data);
        else if(cla==char[].class)
            active.bind(v,(char[])data);
        else if(cla==short.class)
            active.bind(v,(short)data);
        else if(cla==short[].class)
            active.bind(v,(short[])data);
        else if(cla==int.class||cla==Integer.class)
            active.bind(v,(int)data);
        else if(cla==int[].class||cla==Integer[].class)
            active.bind(v,(int[])data);
        else if(cla==float.class||cla==Float.class)
            active.bind(v,(float)data);
        else if(cla==float[].class||cla==Float[].class)
            active.bind(v,(float[])data);
        else if(cla==long.class||cla==Long.class)
            active.bind(v,(long)data);
        else if(cla==double.class||cla==Double.class)
            active.bind(v,(double)data);
        else if(cla==String.class)
            active.bind(v,(String)data);
        else if(data instanceof Serializable)
            active.bind(v,(Serializable)data);
        else if(data instanceof Parcelable)
            active.bind(v,(Parcelable)data);
    }
}
