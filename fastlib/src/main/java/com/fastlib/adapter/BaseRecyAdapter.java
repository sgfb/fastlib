package com.fastlib.adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.annotation.ContentView;
import com.fastlib.base.CommonViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 18/3/9.
 * 抽出公共功能的基本RecyclerView适配器.支持ContentView注解
 */
public abstract class BaseRecyAdapter<T> extends RecyclerView.Adapter<CommonViewHolder>{
    public static final int TYPE_HEAD=1;
    public static final int TYPE_BOTTOM=2;

    protected int mItemId;
    protected List<T> mData;
    protected CommonViewHolder mHeadViewHolder;
    protected CommonViewHolder mBottomViewHolder;

    /**
     * 数据绑定
     * @param position 位置
     * @param data 数据
     * @param holder 视图持有者
     */
    public abstract void binding(int position,T data,CommonViewHolder holder);

    public BaseRecyAdapter(){
        this(-1,null);
    }

    public BaseRecyAdapter(List<T> initList){
        this(-1,initList);
    }

    public BaseRecyAdapter(@LayoutRes int layoutId){
        this(layoutId,null);
    }

    public BaseRecyAdapter(@LayoutRes int layoutId,List<T> initList){
        //如果layoutId参数不标准的情况下尝试使用ContentView注解
        if(layoutId>0) mItemId=layoutId;
        else{
            ContentView cv=getClass().getAnnotation(ContentView.class);
            if(cv==null) throw new IllegalArgumentException("没有指定LayoutId和ContentView注解");
            mItemId=cv.value();
        }
        if(initList==null) mData=new ArrayList<>();
        else mData=initList;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0&&mHeadViewHolder!=null) return TYPE_HEAD;
        else if(position==getItemCount()-1&&mBottomViewHolder!=null) return TYPE_BOTTOM;
        return 0;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if(viewType==TYPE_HEAD) return mHeadViewHolder;
        else if(viewType==TYPE_BOTTOM) return mBottomViewHolder;
        return new CommonViewHolder(LayoutInflater.from(parent.getContext()).inflate(mItemId,parent,false));
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position){
        int type=getItemViewType(position);
        if(type!=TYPE_HEAD&&type!=TYPE_BOTTOM)
            binding(position,getItemAtPosition(position),holder);
    }

    @Override
    public int getItemCount(){
        int extraItem=(mHeadViewHolder!=null?1:0)+(mBottomViewHolder!=null?1:0);
        return mData==null?extraItem:mData.size()+extraItem;
    }

    /**
     * 设置头部视图
     * @param headView 头部视图
     */
    public void setHeadView(View headView){
        mHeadViewHolder=new CommonViewHolder(headView);
        notifyDataSetChanged();
    }

    /**
     * 设置尾部视图
     * @param bottomView 尾部视图
     */
    public void setBottomView(View bottomView){
        mBottomViewHolder=new CommonViewHolder(bottomView);
        notifyDataSetChanged();
    }

    /**
     * 设置绑定数据列表
     * @param list 数据列表
     */
    public void setData(List<T> list){
        mData=list;
        notifyDataSetChanged();
    }

    /**
     * 获取绑定数据列表
     * @return 数据列表
     */
    public List<T> getData(){
        return mData;
    }

    /**
     * 获取指定位置数据
     * @param position 指定位置
     * @return 指定位置数据
     */
    public T getItemAtPosition(int position){
        if((mHeadViewHolder!=null&&position==0)||(mBottomViewHolder!=null&&position==getItemCount()-1)) return null;
        return mData.get(mHeadViewHolder!=null?position-1:position);
    }

    /**
     * 插入数据
     * @param data 数据
     * @param position 插入位置
     * @param anim 是否显示插入动画效果
     */
    public void addData(T data,int position,boolean anim){
        mData.add(position,data);
        if(anim) notifyItemInserted(position);
        else notifyDataSetChanged();
    }

    /**
     * 插入到尾部，没有动画效果
     * @param data
     */
    public void addData(T data){
        mData.add(data);
        notifyDataSetChanged();
    }

    /**
     * 插入列表数据
     * @param data 数据
     * @param position 位置
     * @param anim 是否显示插入动画效果
     */
    public void addAllData(List<T> data,int position,boolean anim){
        mData.addAll(position,data);
        if(anim) notifyItemRangeInserted(position,data.size());
        else notifyDataSetChanged();
    }

    /**
     * 插入数据到尾部，不显示动画效果
     * @param list 数据列表
     */
    public void addData(List<T> list){
        mData.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 删除某个数据，不显示动画效果
     * @param data 数据
     */
    public void remove(T data){
        mData.remove(data);
        notifyDataSetChanged();
    }

    /**
     * 删除某个位置的数据，不显示动画效果
     * @param position 指定删除位置
     */
    public void remove(int position){
        remove(position,false);
    }

    /**
     * 删除某个位置数据
     * @param position 位置
     * @param anim 是否显示动画
     */
    public void remove(int position,boolean anim){
        mData.remove(position);
        if(anim) notifyItemRemoved(position);
        else notifyDataSetChanged();
    }

    /**
     * 删除多个数据
     * @param position 起始位置
     * @param count 总数
     * @param anim 是否显示动画
     */
    public void remove(int position,int count,boolean anim){
        for(int i=0;i<count;i++)
            mData.remove(position);
        if(anim) notifyItemRangeRemoved(position,count);
        else notifyDataSetChanged();
    }
}