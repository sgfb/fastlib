package com.fastlib;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fastlib.base.OldViewHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sgfb on 17/4/14.
 */
public abstract class MultiAdapter extends BaseAdapter{
    private Context mContext;
    private List<ViewDataBean> mData;

    public MultiAdapter(Context context){
        mContext = context;
        mData =new ArrayList<>();
    }

    public abstract void binding(int position,ViewDataBean data, OldViewHolder holder);

    @Override
    public int getCount() {
        return mData ==null?0: mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position).mData;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewDataBean bean= mData.get(position);
        OldViewHolder holder=OldViewHolder.get(mContext,convertView,parent,bean.mLayoutId);
        binding(position,bean,holder);
        return holder.getConvertView();
    }

    public class ViewDataBean{
        public int mLayoutId;
        public Object mData;
    }

    public void addItem(int layoutId,Object data){
        ViewDataBean bean=new ViewDataBean();
        bean.mLayoutId=layoutId;
        bean.mData=data;
        mData.add(bean);
        notifyDataSetChanged();
    }

    public void addItems(int layoutId,List<Object> data){
        if(data==null||data.isEmpty())
            return;
        Iterator<Object> iterator=data.iterator();
        while(iterator.hasNext()){
            ViewDataBean bean=new ViewDataBean();
            bean.mLayoutId=layoutId;
            bean.mData= iterator.next();
        }
        notifyDataSetChanged();
    }
}