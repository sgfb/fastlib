package com.fastlib.adapter;

import androidx.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.base.OldViewHolder;

import java.util.List;

/**
 * Created by sgfb on 16/12/29.
 * 单类型适配器
 */
public abstract class FastAdapter<T> extends CommonBaseAdapter<T>{

    public FastAdapter() {
        super();
    }

    public FastAdapter(@LayoutRes int itemId) {
        super(itemId);
    }

    public FastAdapter(List<T> initData) {
        super(initData);
    }

    public FastAdapter(@LayoutRes int itemId, List<T> initData) {
        super(itemId, initData);
    }

    public abstract void binding(int position, T data, OldViewHolder holder);

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        OldViewHolder holder=OldViewHolder.get(parent.getContext(),convertView,parent, mLayoutId);
        binding(position,mData.get(position),holder);
        return holder.getConvertView();
    }
}