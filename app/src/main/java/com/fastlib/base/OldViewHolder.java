package com.fastlib.base;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fastlib.app.GlobalConfig;

/**
 * 通用View持有者
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

	/**
	 * 给某个id视图设置监听
	 * @param viewId
	 * @param listener
     */
	public void setOnClickListener(int viewId, View.OnClickListener listener){
		getView(viewId).setOnClickListener(listener); //如果奔溃，就让它奔溃
	}

	/**
	 * 给这条布局设置点击监听
	 * @param listener
     */
	public void setOnClickListener(View.OnClickListener listener){
		mConvertView.setOnClickListener(listener);
	}

	/**
	 * 给某个id视图设置长点击监听
	 * @param viewId
	 * @param listener
     */
	public void setOnLongClickListener(int viewId, View.OnLongClickListener listener){
		getView(viewId).setOnLongClickListener(listener);
	}

	/**
	 * 给这条布局设置长点击监听
	 * @param listener
     */
	public void setOnLongClickListener(View.OnLongClickListener listener){
		mConvertView.setOnLongClickListener(listener);
	}
}