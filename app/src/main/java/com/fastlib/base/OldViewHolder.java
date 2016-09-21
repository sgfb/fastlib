package com.fastlib.base;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 通用View持有者
 * 
 * @author Administrator
 * 
 */
public class OldViewHolder {

	private SparseArray<View> mViews;
	private View mConvertView;
	private int mLayoutId;

	private OldViewHolder(Context context, ViewGroup parent, int layoutId){
		this.mViews = new SparseArray<>();
		this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
		this.mLayoutId=layoutId;
		mConvertView.setTag(this);
	}

	/**
	 * 获取ViewHolder实例
	 * 
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @return
	 */
	public static OldViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId) {
		if (convertView == null)
			return new OldViewHolder(context, parent, layoutId);
		else if (convertView.getTag() instanceof OldViewHolder&&((OldViewHolder)convertView.getTag()).mLayoutId==layoutId)
			return(OldViewHolder)convertView.getTag();
		else
			return new OldViewHolder(context,parent,layoutId);
	}

	/**
	 * 获取根View
	 * 
	 * @return
	 */
	public View getConvertView() {
		return mConvertView;
	}

	/**
	 * 获取子View
	 * 
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
	 * 
	 * @param viewId
	 * @param str
	 */
	public void setText(int viewId, String str) {
		TextView textView = getView(viewId);
		textView.setText(str);
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
