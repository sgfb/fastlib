package com.fastlib.adapter;

import com.fastlib.base.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 2020\03\02.
 * 抽出刷新列表和加载下一页列表,仅关注数据源解释和数据与列表项的绑定
 */
public abstract class RemoteBindAdapter<T, R> extends BaseRecyAdapter<T> implements OnRemoteRequestResultListener<R> {
    protected boolean isRefresh,isLoading, isMore;
    private OnLoadPageListener<R> mLoadPageListener;
    private OnRemoteAnswerListener mRemoteAnswerListener;

    public RemoteBindAdapter(){}

    public RemoteBindAdapter(OnLoadPageListener<R> loadPageListener,OnRemoteAnswerListener remoteAnswerListener){
        this.mLoadPageListener = loadPageListener;
        this.mRemoteAnswerListener=remoteAnswerListener;
    }

    /**
     * 数据源解释
     * @param resultData    页数据
     * @return              解释后适配器支持的数据列表
     */
    protected abstract List<T> translate(R resultData);

    public abstract void binding(int position, T data, RecyclerViewHolder holder);

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        if (position >= getItemCount() - 1 && isMore && !isLoading && mLoadPageListener != null) {
            isLoading = true;
            mLoadPageListener.onLoadNextPageListener(this);
        }
        super.onBindViewHolder(holder, position);
    }

    public void refresh() {
        isRefresh=true;
        isMore=true;
        isLoading=true;
        if(mLoadPageListener!=null) mLoadPageListener.onRefreshPageListener(this);
    }

    @Override
    public void onRemoteDataResult(R resultData) {
        if(mRemoteAnswerListener!=null)
            mRemoteAnswerListener.onAnswered();

        List<T> newDataList = translate(resultData);
        if (newDataList == null || newDataList.isEmpty()) isMore = false;
        isLoading = false;

        if(newDataList==null) newDataList=new ArrayList<>();
        if(isRefresh) setData(newDataList);
        else addData(newDataList);
        isRefresh=false;
    }

    @Override
    public void onErrorResult(Exception e) {
        if(mRemoteAnswerListener!=null)
            mRemoteAnswerListener.onAnswered();
    }

    public void setOnLoadPageListener(OnLoadPageListener<R> loadPageListener){
        setOnLoadPageListener(loadPageListener,false);
    }

    public void setOnLoadPageListener(OnLoadPageListener<R> loadPageListener,boolean startRefresh){
        mLoadPageListener=loadPageListener;
        if(startRefresh)
            refresh();
    }

    public void setOnRemoteAnswerListener(OnRemoteAnswerListener remoteAnswerListener){
        mRemoteAnswerListener=remoteAnswerListener;
    }

    /**
     * 页事件监听
     */
    public interface OnLoadPageListener<R> {

        /**
         * 刷新页
         */
        void onRefreshPageListener(OnRemoteRequestResultListener<R> listener);

        /**
         * 加载下一页
         */
        void onLoadNextPageListener(OnRemoteRequestResultListener<R> listener);
    }

    /**
     * 数据源响应事件回调
     */
    public interface OnRemoteAnswerListener{

        void onAnswered();
    }
}
