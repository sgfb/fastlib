package com.fastlib.adapter;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.R;
import com.fastlib.base.OldViewHolder;
import com.fastlib.base.Refreshable;
import com.fastlib.db.DatabaseListGetCallback;
import com.fastlib.db.FastDatabase;

import java.util.List;

/**
 * Created by sgfb on 17/1/12.
 * 关联FastDatabase适配数据
 */
public abstract class DatabaseAdapter<T> extends CommonBaseAdapter<T> implements DatabaseListGetCallback<T> {
    protected boolean isRefresh,isMore,isLoading;
    protected int mCurrentIndex=0,mPerCount=10; //当前位置索引和每次读取列表长度
    protected Class<T> mCla;
    protected Refreshable mRefreshable;

    /**
     * 具体绑定
     * @param position 位置
     * @param data 数据
     * @param holder 视图持有者
     */
    protected abstract void binding(int position,T data,OldViewHolder holder);

    /**
     * 更新数据时获取数据库实例
     * @param isRefresh 刷新还是获取下一页时
     * @return 数据库实例
     */
    protected abstract FastDatabase generateDB(boolean isRefresh);

    /**
     * 使用上下文和类，布局id构造
     * @param cla 数据类
     * @param layoutId 布局id
     */
    public DatabaseAdapter(Class<T> cla, @LayoutRes int layoutId){
    	this(cla,layoutId,true);
    }

    public DatabaseAdapter(Class<T> cla,boolean startNow){
        super();
        init(cla,startNow);
    }

    /**
     * 使用上下文和类，布局id。指定排序顺序（需要数据类指定主键）和是否立即刷新
     * @param cla 数据类
     * @param layoutId 布局id
     * @param startNow 立即刷新
     */
    public DatabaseAdapter(Class<T> cla,@LayoutRes int layoutId,boolean startNow){
        super(layoutId);
        init(cla,startNow);
    }

    private void init(Class<T> cla,boolean startNow){
        mCla=cla;
        if(startNow)
            refresh();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(!isLoading&&isMore&&position>=getCount()-1)
            loadMore();
        OldViewHolder holder=OldViewHolder.get(parent.getContext(),convertView,parent, mLayoutId);
        binding(position,getItem(position),holder);
        return holder.getConvertView();
    }

    /**
     * 刷新数据
     */
    public void refresh(){
    	isRefresh=true;
    	isMore=true;
        mCurrentIndex=0;
        generateDB(true).limit(mCurrentIndex,mPerCount)
                .getAsync(mCla,this);
    }

    /**
     * 读取下一页
     */
    protected void loadMore(){
        isLoading=true;
    	isRefresh=false;
        mCurrentIndex+=mPerCount;
        generateDB(false).limit(mCurrentIndex,mPerCount)
                .getAsync(mCla,this);
    }

    @Override
    public void onResult(List<T> result){
        isLoading=false;
        if(mRefreshable!=null)
            mRefreshable.setRefreshStatus(false);
        if(result==null||result.isEmpty())
            isMore = false;
        if(isRefresh)
            mData=result;
        else
            if(result!=null)
                mData.addAll(result);
        notifyDataSetChanged();
    }

    /**
     * 获取每次读取的条数
     * @return 每次读取的条数
     */
    public int getPerCount() {
        return mPerCount;
    }

    /**
     * 设置每次读取的条数
     * @param perCount 每次读取的条数
     */
    public void setPerCount(int perCount) {
        mPerCount = perCount;
    }

    /**
     * 获取刷新组件
     * @return 刷新组件
     */
    public Refreshable getRefreshable() {
        return mRefreshable;
    }

    /**
     * 设置刷新组件
     * @param refreshable 刷新组件
     */
    public void setRefreshable(Refreshable refreshable) {
        mRefreshable = refreshable;
    }
}