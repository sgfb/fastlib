package com.fastlib.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fastlib.app.FastActivity;
import com.fastlib.base.CommonViewHolder;
import com.fastlib.base.Refreshable;
import com.fastlib.net.Listener;
import com.fastlib.net.Request;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 单请求绑定适配器,将视图与服务器中的数据捆绑
 * @param <T> 数据类型
 * @param <R> 返回类型
 */
public abstract class SingleAdapterForRecycler<T,R> extends RecyclerView.Adapter<CommonViewHolder>  implements Listener<R,Object,Object> {
    private boolean isRefresh,isLoading,isMore;
    private List<T> mData;
    private Refreshable mRefreshLayout;
    private ThreadPoolExecutor mThreadPool;
    private int mPerCount; //每次读取条数，默认为1
    private int mLayoutId;
    protected Context mContext;
    protected Request mRequest;

    public abstract Request generateRequest();

    /**
     * 数据绑定视图
     * @param position
     * @param data
     * @param holder
     */
    public abstract void binding(int position,T data,CommonViewHolder holder);

    /**
     * 返回的数据转换
     * @param result
     * @return
     */
    public abstract List<T> translate(R result);

    /**
     * 请求更多数据时的请求
     * @param request
     */
    public abstract void getMoreDataRequest(Request request);

    /**
     * 刷新数据时的请求
     * @param request
     */
    public abstract void getRefreshDataRequest(Request request);

    public SingleAdapterForRecycler(Context context, @LayoutRes int layoutId){
        this(context,layoutId,true);
    }

    public SingleAdapterForRecycler(Context context, @LayoutRes int layoutId, boolean startNow){
        mContext=context;
        mRequest= generateRequest();
        mPerCount=1;
        isRefresh=true;
        isMore=true;
        isLoading=false;
        mLayoutId=layoutId;
        mRequest.setGenericType(new Type[]{getResponseType()});
        mRequest.setListener(this);
        if(mContext instanceof FastActivity)
            ((FastActivity)mContext).addRequest(mRequest);
        if(startNow)
            refresh();
    }

    private Type getResponseType(){
        Method[] methods=getClass().getDeclaredMethods();
        for(Method m:methods){
            if(m.getName().equals("translate")) {
                Type type=m.getGenericParameterTypes()[0];
                if(type!=Object.class)
                    return type;
            }
        }
        return null;
    }

    /**
     * 向服务器请求的参数
     */
    private void loadMoreData(){
        isLoading=true;
        isRefresh=false;
        getMoreDataRequest(mRequest);
        mRequest.setExecutor(mThreadPool).start(false);
    }

    public void refresh(){
        isLoading=true;
        isRefresh=true;
        //刷新之后也许有更多数据？
        isMore=true;
        getRefreshDataRequest(mRequest);
        mRequest.setExecutor(mThreadPool).start(true);
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new CommonViewHolder(LayoutInflater.from(mContext).inflate(mLayoutId,parent,false));
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position){
        if(position>=getItemCount()-1&&isMore&&!isLoading)
            loadMoreData();
        binding(position,mData.get(position),holder);
    }

    @Override
    public int getItemCount(){
        return mData==null?0:mData.size();
    }

    @Override
    public void onResponseListener(Request r, R result, Object none1, Object none2){
        if(mRefreshLayout!=null)
            mRefreshLayout.setRefreshStatus(false);
        List<T> list=translate(result);

        isLoading=false;
        if(list==null||list.size()<=0){
            isMore=false;
            return;
        }
        if(list.size()<mPerCount){
            isMore = false;
        }
        if(isRefresh)
            mData=list;
        else{
            if(mData==null)
                mData=list;
            else
                mData.addAll(list);
        }
        notifyItemRangeInserted(getItemCount()-list.size(),list.size());
    }

    @Override
    public void onErrorListener(Request r, String error){
        if(mRefreshLayout!=null)
            mRefreshLayout.setRefreshStatus(false);
        isLoading=false;
    }

    public ThreadPoolExecutor getThreadPool() {
        return mThreadPool;
    }

    public void setThreadPool(ThreadPoolExecutor threadPool) {
        mThreadPool = threadPool;
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