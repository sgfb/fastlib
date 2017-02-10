package com.fastlib.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fastlib.base.OldViewHolder;
import com.fastlib.base.Refreshable;
import com.fastlib.db.DatabaseListListener;
import com.fastlib.db.FastDatabase;
import com.fastlib.db.FilterCommand;

import java.util.List;

/**
 * Created by sgfb on 17/1/12.
 * 关联FastDatabase适配数据
 */
public abstract class DatabaseAdapter<T> extends BaseAdapter implements DatabaseListListener<T>{
    protected boolean isAsc,isRefresh,isMore;
    protected int mCurrentIndex=0,mPerCount=10; //当前位置索引和每次读取列表长度
    protected int mLayoutId;
    protected List<T> mData;
    protected Class<T> mCla;
    protected Context mContext;
    protected FilterCommand mFilterCommand; //过滤条件
    protected Refreshable mRefreshable;

    protected abstract void binding(int position,T data,OldViewHolder holder);

    /**
     * 使用上下文和类，布局id构造
     * @param context 上下文
     * @param cla 数据类
     * @param layoutId 布局id
     */
    public DatabaseAdapter(Context context, Class<T> cla, @LayoutRes int layoutId){
    	this(context,cla,layoutId,true,true);
    }

    /**
     * 使用上下文和类，布局id。指定排序顺序（需要数据类指定主键）和是否立即刷新
     * @param context 上下文
     * @param cla 数据类
     * @param layoutId 布局id
     * @param asc 排序
     * @param startNow 立即刷新
     */
    public DatabaseAdapter(Context context,Class<T> cla,@LayoutRes int layoutId,boolean asc,boolean startNow){
        mContext=context;
        mCla=cla;
        isAsc=asc;
        mLayoutId=layoutId;
        if(startNow)
            refresh();
    }

    @Override
    public int getCount(){
        return mData==null?0:mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(position>=getCount()-1)
            loadMore();
        OldViewHolder holder=OldViewHolder.get(mContext,convertView,parent,mLayoutId);
        binding(position,getItem(position),holder);
        return holder.getConvertView();
    }

    public void refresh(){
    	isRefresh=true;
    	isMore=true;
        mCurrentIndex=0;
        FastDatabase.getDefaultInstance(mContext)
                .addFilter(mFilterCommand)
                .limit(mCurrentIndex,mPerCount)
                .orderBy(isAsc)
                .getAsync(mCla,this);
    }

    protected void loadMore(){
    	isRefresh=false;
        mCurrentIndex+=mPerCount;
        FastDatabase.getDefaultInstance(mContext)
                .addFilter(mFilterCommand)
                .limit(mCurrentIndex,mCurrentIndex+mPerCount)
                .orderBy(isAsc)
                .getAsync(mCla,this);
    }

    @Override
    public void onResult(List<T> result){
        if(mRefreshable!=null)
            mRefreshable.setRefreshStatus(false);
        if(result==null||result.isEmpty()){
            isMore = false;
            return;
        }
        if(isRefresh)
            mData=result;
        else
            mData.addAll(result);
        notifyDataSetChanged();
    }

    public FilterCommand getFilterCommand(){
        return mFilterCommand;
    }

    public void setFilterCommand(FilterCommand filterCommand){
        mFilterCommand = filterCommand;
    }

    public boolean isAsc() {
        return isAsc;
    }

    public void setAsc(boolean asc) {
        isAsc = asc;
    }

    public int getPerCount() {
        return mPerCount;
    }

    public void setPerCount(int perCount) {
        mPerCount = perCount;
    }

    public Refreshable getRefreshable() {
        return mRefreshable;
    }

    public void setRefreshable(Refreshable refreshable) {
        mRefreshable = refreshable;
    }
}
