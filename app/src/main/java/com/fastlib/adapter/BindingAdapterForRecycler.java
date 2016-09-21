package com.fastlib.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fastlib.db.RemoteCacheServer;
import com.fastlib.bean.StateViewHelper;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.utils.BindingView;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 16/8/23.
 * RecyclerView集成适配器
 */
public abstract class BindingAdapterForRecycler<N> extends RecyclerView.Adapter<BindingAdapterForRecycler.ViewHolder> implements Listener{
    public static final String TAG=BindingAdapterForRecycler.class.getSimpleName();
    public static final int POSITION_HEAD =100;
    public static final int POSITION_FOOT =101;
    public static final int STATE_NONE=1;
    public static final int STATE_LOADING=2;
    public static final int STATE_ERROR_NET=2;
    public static final int STATE_COMPLETE=3;

    protected boolean isRefresh,isMore,isLoading,isSaveCache,isAutoBinding;
    private int mLayoutId;
    private int mPerCount; //每次读取条数，默认为1
    private Map<Integer,StateViewHelper> mStateViewHelper;
    protected List<N> mData;
    protected RemoteCacheServer mRemoteCacheServer;
    protected BindingView mResolver;
    protected Request mRequest;
    protected Gson gson=new Gson();
    protected StateViewHelper.ViewHelper mFootView,mHeadView;

    public BindingAdapterForRecycler(Context context, @LayoutRes int layoutId){
        this(context,layoutId,false);
    }

    public BindingAdapterForRecycler(Context context, @LayoutRes int layoutId, boolean saveCache){
        mLayoutId=layoutId;
        isRefresh=true;
        isMore=true;
        isSaveCache=saveCache;
        isLoading=false;
        isAutoBinding=true;
        mPerCount=1;
        mRequest=getRequest();
        mRequest.setListener(this);
        mStateViewHelper=new HashMap<>();
        mRemoteCacheServer =new RemoteCacheServer(mRequest);
        mResolver=new BindingView(context,LayoutInflater.from(context).inflate(mLayoutId,null));
        refresh();
    }

    /**
     * 获取定义的请求
     * @return
     */
    public abstract Request getRequest();

    public abstract void binding(BindingAdapterForRecycler.ViewHolder holder,N data,int position);

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

    /**
     * 定义数据转换
     * @param result
     * @return
     */
    public abstract List<N> translate(String result);

    /**
     * 向服务器请求的参数
     */
    private void loadMoreData(){
        isLoading=true;
        isRefresh=false;
        getMoreDataRequest(mRequest);
        if(isSaveCache)
            mRemoteCacheServer.loadMore(mRequest.getParams());
        else
            NetQueue.getInstance().netRequest(mRequest);
    }

    public void refresh(){
        isLoading=true;
        isRefresh=true;
        //刷新之后也许有更多数据？
        isMore=true;
        getRefreshDataRequest(mRequest);
        if(isSaveCache)
            mRemoteCacheServer.start();
        else
            NetQueue.getInstance().netRequest(mRequest);
    }

    public N getItem(int position){
        if(getItemViewType(position)==0)
            return mData.get(position-(mHeadView==null?0:1));
        return null;
    }

    @Override
    public void onBindViewHolder(BindingAdapterForRecycler.ViewHolder holder,int position){
        if(position>=getItemCount()-1&&isMore&&!isLoading)
            loadMoreData();
        if(getItemViewType(position)==0){
            if(isAutoBinding){
                String json=gson.toJson(getItem(position));
                mResolver.fromJson(json,holder.itemView);
            }
            binding(holder,getItem(position),position);
        }
    }

    @Override
    public int getItemViewType(int position){
        if(mHeadView!=null&&position==0)
            return POSITION_HEAD;
        if(mFootView!=null&&position==getItemCount()-1)
            return POSITION_FOOT;
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        switch (viewType){
            case 0:return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(mLayoutId,parent,false));
            case POSITION_HEAD:return new ViewHolder(mHeadView.getView());
            case POSITION_FOOT:return new ViewHolder(mFootView.getView());
        }
        return null;
    }

    @Override
    public int getItemCount(){
        int count=0;
        if(mHeadView!=null) count++;
        if(mFootView!=null) count++;
        if(mData!=null) count+=mData.size();
        return count;
    }

    @Override
    public void onResponseListener(Request r,String result){
        isLoading=false;
        int startItem=getItemCount();
        List<N> list=translate(result);
        if(list==null||list.size()<=0){
            isMore=false;
            return;
        }
        if(list.size()<mPerCount){
            isMore = false;
        }
        if(isRefresh){
            mData=list;
            startItem=0;
        }
        else mData.addAll(list);
        int endItem=getItemCount();
        notifyItemRangeChanged(startItem,endItem);
    }

    @Override
    public void onErrorListener(Request r,String error){
        isLoading=false;
        StateViewHelper helper=mStateViewHelper.get(STATE_ERROR_NET);
        if(helper!=null)
            setFootView(helper.helper);
        Log.d(TAG, "网络异常:" + error);
    }

    private void setHeadView(StateViewHelper.ViewHelper head){
        mHeadView=head;
        notifyItemInserted(0);
    }

    private void setFootView(StateViewHelper.ViewHelper foot){
        mFootView=foot;
        notifyItemInserted(getItemCount()-1);
    }

    public void putStateView(int state,StateViewHelper helper){
        mStateViewHelper.put(state,helper);
        if(state==STATE_NONE){
            if(helper.position==POSITION_HEAD) setHeadView(helper.helper);
            else setFootView(helper.helper);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private SparseArray<View> mViews;

        public ViewHolder(View itemView){
            super(itemView);
            mViews=new SparseArray<>();
        }

        /**
         * 获取子View
         * @param viewId
         * @return
         */
        @SuppressWarnings("unchecked")
        public <V extends View> V getView(int viewId) {
            View view = mViews.get(viewId);
            if (view==null){
                view=itemView.findViewById(viewId);
                mViews.put(viewId,view);
            }
            return (V) view;
        }

        /**
         * 绑定指定ID的文本信息
         *
         * @param viewId
         * @param str
         */
        public void setText(int viewId, String str) {
            TextView textView = getView(viewId);
            textView.setText(str);
        }
    }
}