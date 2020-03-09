package com.fastlib.adapter;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import androidx.core.util.Pair;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by sgfb on 2019/04/17
 * E-Mail:602687446@qq.com
 * 自适应网格基础适配器
 */
public abstract class AutoFitGridAdapter<T>{
    private DataSetObservable mDataSetObservable=new DataSetObservable();
    protected List<T> mData;
    protected Context mContext;

    public AutoFitGridAdapter(Context context){
        mContext=context;
    }

    public AutoFitGridAdapter(List<T> mData) {
        this.mData = mData;
    }

    public abstract Pair<Integer,View> getView(int position, T data);

    public T getItemAtPosition(int position){
        return mData.get(position);
    }

    public int getCount(){
        return mData==null?0:mData.size();
    }

    public void setData(List<T> data){
        mData=data;
        notifyDataSetChanged();
    }

    public void addData(T data){
        if(mData==null) mData=new ArrayList<>();
        mData.add(data);
        notifyDataSetChanged();
    }

    public void remove(int index){
        if(mData!=null){
            mData.remove(index);
            notifyDataSetChanged();
        }
    }

    public void registerDataSetObserver(DataSetObserver observer){
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer){
        mDataSetObservable.unregisterObserver(observer);
    }

    private void notifyDataSetChanged(){
        mDataSetObservable.notifyChanged();
    }
}