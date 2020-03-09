package com.fastlib.adapter;

import androidx.annotation.LayoutRes;
import android.widget.BaseAdapter;

import com.fastlib.annotation.ContentView;
import com.fastlib.utils.Reflect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 18/3/7.
 * 对BaseAdapter做操作抽象封装
 */
public abstract class CommonBaseAdapter<T> extends BaseAdapter{
    protected int mLayoutId;
    protected List<T> mData;

    public CommonBaseAdapter(){
        this(-1);
    }

    public CommonBaseAdapter(@LayoutRes int itemId){
        this(itemId,null);
    }

    public CommonBaseAdapter(List<T> initData){
        this(-1,initData);
    }

    public CommonBaseAdapter(@LayoutRes int itemId,List<T> initData){
        if(itemId>-1) mLayoutId =itemId;
        else{
            ContentView itemView= Reflect.upFindAnnotation(getClass(),ContentView.class);
            if(itemView!=null)
                mLayoutId =itemView.value();
            else throw new IllegalArgumentException("item id和ContentView同时为空");
        }
        if(initData==null) mData=new ArrayList<>();
        else mData=initData;
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

    /**
     * 设置数据到适配器
     * @param list
     */
    public void setData(List<T> list){
        if(list==null) mData.clear();
        else mData=list;
        notifyDataSetChanged();
    }

    /**
     * 增加数据到适配器
     * @param data
     */
    public void addData(T data){
        mData.add(data);
        notifyDataSetChanged();
    }

    /**
     * 增加一组数据到适配器
     * @param data
     */
    public void addData(List<T> data){
        mData.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 如果已存在，不加入到适配器，否则加入
     * @param data
     */
    public void addIfNotExist(T data){
        if(!mData.contains(data))
            addData(data);
    }

    /**
     * 如果已存在，不加入到适配器，否则加入
     * @param data
     */
    public void addIfNotExist(List<T> data){
        for(T t:data)
            addIfNotExist(t);
    }

    /**
     * 从适配器中移除某对象
     * @param data
     */
    public void remove(T data){
        mData.remove(data);
        notifyDataSetChanged();
    }

    /**
     * 从适配器中移除某个位置的对象
     * @param position
     */
    public void remove(int position){
        mData.remove(position);
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return mData;
    }
}