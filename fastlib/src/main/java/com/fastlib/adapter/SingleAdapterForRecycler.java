package com.fastlib.adapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import com.fastlib.annotation.NetCallback;
import com.fastlib.base.RecyclerViewHolder;
import com.fastlib.base.Refreshable;
import com.fastlib.net.Request;
import com.fastlib.net.listener.Listener;

import java.util.List;

/**
 * 单请求绑定适配器,将视图与服务器中的数据捆绑
 * @param <T> 数据类型
 * @param <R> 返回类型
 */
@NetCallback("translate")
public abstract class SingleAdapterForRecycler<T,R> extends BaseRecyAdapter<T> implements Listener<R,Object,Object> {
    private static final int DEFAULT_ANIM_DURATION=300;
    protected boolean isRefresh,isLoading,isMore;
    protected Request mRequest;
    private Refreshable mRefreshLayout;

    /**
     * 生成网络请求
     * @return 网络请求
     */
    public abstract @NonNull
    Request generateRequest();

    /**
     * 返回的数据转换
     * @param result 接口返回的数据
     * @return 转换后适配器绑定数据列表
     */
    public abstract @Nullable
    List<T> translate(R result);

    /**
     * 请求更多数据时的请求
     * @param request 网络请求
     */
    public abstract void getMoreDataRequest(@NonNull Request request);

    /**
     * 刷新数据时的请求
     * @param request 网络请求
     */
    public abstract void getRefreshDataRequest(@NonNull Request request);

    public SingleAdapterForRecycler(){
        this(-1);
    }

    public SingleAdapterForRecycler(@LayoutRes int layoutId){
        this(layoutId,true);
    }

    public SingleAdapterForRecycler(@LayoutRes int layoutId, boolean startNow){
        super(layoutId);
        mRequest= generateRequest();
        isRefresh=true;
        isMore=true;
        isLoading=false;
        mRequest.setListener(this);
        if(startNow)
            refresh();
    }

    /**
     * 向服务器请求的参数
     */
    private void loadMoreData(){
        isLoading=true;
        isRefresh=false;
        getMoreDataRequest(mRequest);
        mRequest.start(false);
    }

    public void refresh(){
        isLoading=true;
        isRefresh=true;
        isMore=true;
        getRefreshDataRequest(mRequest);
        mRequest.start(true);
    }

    public void refreshWithAnim(){
        if(mRefreshLayout!=null){
            mRefreshLayout.setRefreshStatus(true);
            if(mRefreshLayout instanceof View){
                ((View)mRefreshLayout).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                },DEFAULT_ANIM_DURATION);
            }
        }
        else refresh();
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position){
        if(position>=getItemCount()-1&&isMore&&!isLoading)
            loadMoreData();
        super.onBindViewHolder(holder,position);
    }

    @Override
    public void onResponseListener(Request r, R result, Object none1, Object none2){
        if(mRefreshLayout!=null)
            mRefreshLayout.setRefreshStatus(false);
        List<T> list=translate(result);

        isLoading=false;
        if(list==null||list.isEmpty()){
            isMore=false;
            if(isRefresh&&mData!=null)
                mData.clear();
        }
        else{
            if(isRefresh)
                mData=list;
            else{
                if(mData==null)
                    mData=list;
                else
                    mData.addAll(list);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onErrorListener(Request r, Exception error){
        if(mRefreshLayout!=null)
            mRefreshLayout.setRefreshStatus(false);
        isLoading=false;
    }

    public Refreshable getRefreshLayout() {
        return mRefreshLayout;
    }

    public void setRefreshLayout(Refreshable refreshLayout) {
        mRefreshLayout = refreshLayout;
    }

    @Override
    public void onRawData(Request r, byte[] data) {
        //被适配
    }

    @Override
    public void onTranslateJson(Request r, String json) {
        //被适配
    }
}