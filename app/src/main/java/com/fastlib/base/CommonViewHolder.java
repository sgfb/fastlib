package com.fastlib.base;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

/**
 * Created by sgfb on 17/2/22.
 * Recycler通用ViewHolder。应只使用在单类型列表中
 */
public class CommonViewHolder extends RecyclerView.ViewHolder{
    private SparseArray<View> mViews;
    private View mConvertView;

    public CommonViewHolder(View itemView) {
        super(itemView);
        mViews=new SparseArray<>();
        mConvertView=itemView;
    }

    /**
     * 获取根View
     * @return
     */
    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 获取子View
     * @param viewId
     * @return
     */
    @SuppressWarnings("unchecked")
    public <V extends View> V getView(int viewId) {
        View view = mViews.get(viewId);
        if (view==null) {
            view=mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (V) view;
    }

    /**
     * 绑定指定ID的文本信息
     * @param viewId
     * @param str
     */
    public void setText(int viewId, String str) {
        TextView textView = getView(viewId);
        textView.setText(str);
    }

    public void setText(int viewId,SpannableStringBuilder ss){
        TextView textView = getView(viewId);
        textView.setText(ss);
    }

    /**
     * 设置文本到到指定textview后面
     * @param viewId
     * @param str
     */
    public void appendText(int viewId,String str){
        TextView textView=getView(viewId);
        textView.append(str);
    }

    /**
     * 设置文本到到指定textview前面
     * @param viewId
     * @param str
     */
    public void insertFront(int viewId,String str){
        TextView textView=getView(viewId);
        String temp= TextUtils.isEmpty(textView.getText().toString())?"":textView.getText().toString();
        textView.setText(str+temp);
    }
}
