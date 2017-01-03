package com.fastlib.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fastlib.base.OldViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 16/12/29.
 * 单类型适配器
 */
public abstract class FastAdapter<T> extends BaseAdapter{
    private int mItemId;
    private List<T> mData;
    protected Context mContext;

    public abstract void binding(int position,T data,OldViewHolder holder);

    public FastAdapter(Context context,int itemId){
        this(context,itemId,new ArrayList<T>());
    }

    public FastAdapter(Context context,int itemId,@NonNull List<T> data){
        mContext=context;
        mItemId=itemId;
        mData=data;
    }

    @Override
    public int getCount() {
        return mData==null?0:mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        OldViewHolder holder=OldViewHolder.get(mContext,convertView,parent,mItemId);
        binding(position,mData.get(position),holder);
        return holder.getConvertView();
    }

    public void addData(T data){
        mData.add(data);
        notifyDataSetChanged();
    }

    public void addData(List<T> data){
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void remove(T data){
        mData.remove(data);
        notifyDataSetChanged();
    }

    public void remove(int position){
        mData.remove(position);
        notifyDataSetChanged();
    }
}