package com.fastlib.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.JsonToken;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fastlib.base.OldViewHolder;
import com.fastlib.db.RemoteCache;
import com.fastlib.interf.AdapterViewState;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.utils.BindingView;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 16/9/2<br>
 * 从服务器获取到的数据自动填充到对应的view<br>
 * 1.需要有相应的ViewResolve(数据对视图解析器)<br>
 * 2.需要view的id与接口字段名对齐
 */
public abstract class BindingAdapter2 extends BaseAdapter implements Listener{
    private int mItemLayoutId;
    private int mPerCount; //每次读取条数，默认为1
    protected boolean isRefresh,isMore,isLoading,isSaveCache;
    protected Context mContext;
    protected Request mRequest;
    protected BindingView mResolver;
    private AdapterViewState mViewState;
    private RemoteCache mRemoteCache;
    protected List<Object> mResult; //接口数据树
    protected List<Object> mData;

    public abstract Request getRequest();

    public abstract List<Object> handleRawData(Object rawData);

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

    public BindingAdapter2(Context context,int layoutId){
        mContext=context;
        mItemLayoutId=layoutId;
        mRequest=getRequest();
        mResolver=new BindingView(context,LayoutInflater.from(context).inflate(layoutId,null));
        mRequest.setListener(this);
        mRemoteCache =new RemoteCache(mRequest);
        refresh();
    }

    /**
     * 手动绑定视图
     * @param position
     * @param data
     * @param holder
     */
    public void binding(int position,Object data,OldViewHolder holder){

    }

    @Override
    public int getCount() {
        return mData==null?0:mData.size();
    }

    @Override
    public Object getItem(int position){
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final OldViewHolder viewHolder = getViewHolder(convertView, parent);
        if(position>=getCount()-1&&isMore&&!isLoading)
            loadMoreData();
        Object obj=getItem(position);
        if(obj!=null&&obj instanceof Map<?,?>)
            mResolver.fromMapData(viewHolder.getConvertView(),(Map<String, Object>) obj);
        binding(position,getItem(position),viewHolder);
        return viewHolder.getConvertView();
    }

    private OldViewHolder getViewHolder(View convertView, ViewGroup parent) {
        return OldViewHolder.get(mContext, convertView, parent, mItemLayoutId);
    }

    /**
     * 向服务器请求的参数
     */
    private void loadMoreData(){
        isLoading=true;
        isRefresh=false;
        if(mViewState!=null)
            mViewState.onStateChanged(AdapterViewState.STATE_LOADING);
        getMoreDataRequest(mRequest);
        if(isSaveCache)
            mRemoteCache.loadMore(mRequest.getParams());
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
            mRemoteCache.start();
        else
            NetQueue.getInstance().netRequest(mRequest);
    }

    private Object translate(JsonReader jsonReader)throws IOException {
        Object obj=null;
        JsonToken jt=jsonReader.peek();

        if(jt==JsonToken.BEGIN_ARRAY)
            obj=readJsonArray(jsonReader);
        else if(jt==JsonToken.BEGIN_OBJECT)
            obj=readJsonObj(jsonReader);
        jsonReader.close();
        return obj;
    }

    private Object readJsonObj(JsonReader jsonReader) throws IOException{
        jsonReader.beginObject();
        Map<String,Object> map=new HashMap<>();
        JsonToken jt=jsonReader.peek();
        String name=null;

        if(jt==JsonToken.NULL){
            jsonReader.endObject();
            return null;
        }
        while(jsonReader.hasNext()){
            if(jt==JsonToken.NAME)
                name=jsonReader.nextName();
            else if(jt==JsonToken.BEGIN_OBJECT)
                map.put(name,readJsonObj(jsonReader));
            else if(jt==JsonToken.BEGIN_ARRAY)
                map.put(name,readJsonArray(jsonReader));
            else if(jt==JsonToken.BOOLEAN)
                map.put(name,jsonReader.nextBoolean());
            else if(jt==JsonToken.NUMBER){
                double temp=jsonReader.nextDouble();
                if(Math.abs(temp)>0&&Math.abs(temp)<1)
                    map.put(name,temp);
                else if(Math.abs(temp)>Integer.MAX_VALUE)
                    map.put(name,(long)temp);
                else
                    map.put(name,(int)temp);
            }
            else if(jt==JsonToken.STRING)
                map.put(name,jsonReader.nextString());
            else if(jt==JsonToken.NULL){
                jsonReader.nextNull();
                map.put(name,null);
            }
            else jsonReader.skipValue();
            jt=jsonReader.peek();
        }
        jsonReader.endObject();
        return map;
    }

    private Object readJsonArray(JsonReader jsonReader) throws IOException{
        jsonReader.beginArray();
        List<Object> list=new ArrayList<>();
        JsonToken jt=jsonReader.peek();

        if(jt==JsonToken.NULL){
            jsonReader.endArray();
            return null;
        }
        while(jt!= JsonToken.END_ARRAY){
            if(jt==JsonToken.BEGIN_OBJECT)
                list.add(readJsonObj(jsonReader));
            else if(jt==JsonToken.BEGIN_ARRAY)
                list.add(readJsonArray(jsonReader));
            else if(jt==JsonToken.BOOLEAN)
                list.add(jsonReader.nextBoolean());
            else if(jt==JsonToken.NUMBER){
                double temp=jsonReader.nextDouble();
                if(Math.abs(temp)>0&&Math.abs(temp)<1)
                    list.add(temp);
                else if(Math.abs(temp)>Integer.MAX_VALUE)
                    list.add((long)temp);
                else
                    list.add((int)temp);
            }
            else if(jt==JsonToken.STRING)
                list.add(jsonReader.nextString());
            else if(jt==JsonToken.NULL)
                list.add(null);
            else jsonReader.skipValue();
            jt=jsonReader.peek();
        }
        jsonReader.endArray();
        return list;
    }

    @Override
    public void onResponseListener(Request r, String result){
        JsonReader jr=new JsonReader(new StringReader(result));
        Object obj=null;
        List<Object> dataList;
        try {
            obj = translate(jr);
        } catch (IOException e){
            //do noting
        }

        isLoading=false;
        if(obj==null){
            isMore=false;
            return;
        }
        dataList=handleRawData(obj);
        if(dataList==null||dataList.size()<=0){
            isMore=false;
            if(mViewState!=null)
                mViewState.onStateChanged(AdapterViewState.STATE_NO_MORE);
            return;
        }
        if(dataList.size()<mPerCount){
            isMore=false;
            if(mViewState!=null)
                mViewState.onStateChanged(AdapterViewState.STATE_NO_MORE);
        }
        if(isRefresh){
            mResult =new ArrayList<>();
            mData=dataList;
        }
        else mData.addAll(dataList);
        mResult.add(obj);
        notifyDataSetChanged();
    }

    @Override
    public void onErrorListener(Request r, String error){
        isLoading=false;
        System.out.println("BindingAdapter error:" + error);
    }

    public void setViewStateListener(AdapterViewState state){
        mViewState=state;
    }

    public void setLoadCount(int count){
        mPerCount=count;
    }

    public boolean isSaveCache(){
        return isSaveCache;
    }

    public void setIsSaveCache(boolean isSaveCache) {
        this.isSaveCache = isSaveCache;
    }
}
