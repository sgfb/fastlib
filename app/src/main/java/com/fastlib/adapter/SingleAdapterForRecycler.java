package com.fastlib.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastlib.base.CommonViewHolder;
import com.fastlib.base.Refreshable;
import com.fastlib.net.Listener;
import com.fastlib.net.Request;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 单请求绑定适配器,将视图与服务器中的数据捆绑
 * @param <T> 数据类型
 * @param <R> 返回类型
 */
public abstract class SingleAdapterForRecycler<T,R> extends BaseRecyAdapter<T>  implements Listener<R,Object,Object> {
    private boolean isRefresh,isLoading,isMore;
    private Refreshable mRefreshLayout;
    private int mPerCount; //每次读取条数，默认为1
    protected Request mRequest;

    /**
     * 生成网络请求
     * @return 网络请求
     */
    public abstract @NonNull Request generateRequest();

    /**
     * 返回的数据转换
     * @param result 接口返回的数据
     * @return 转换后适配器绑定数据列表
     */
    public abstract @Nullable List<T> translate(R result);

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
        mPerCount=1;
        isRefresh=true;
        isMore=true;
        isLoading=false;
        mRequest.setGenericType(new Type[]{getResponseType()});
        mRequest.setListener(this);
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
        mRequest.start(false);
    }

    public void refresh(){
        isLoading=true;
        isRefresh=true;
        //刷新之后也许有更多数据？
        isMore=true;
        getRefreshDataRequest(mRequest);
        mRequest.start(true);
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position){
        if(position>=getItemCount()-1&&isMore&&!isLoading)
            loadMoreData();
        binding(position,mData.get(position),holder);
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
        notifyDataSetChanged();
    }

    @Override
    public void onErrorListener(Request r, String error){
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