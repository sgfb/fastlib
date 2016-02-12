package com.fastlib.utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 打开或关闭软键盘
 * 
 * @author zhy
 * 
 */
public class KeyBoardUtils
{
	/**
	 * 打开软键盘
	 * 
	 * @param mEditText
	 *            输入框
	 * @param mContext
	 *            上下文
	 */
	public static void openKeybord(EditText mEditText, Context mContext)
	{
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
				InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	/**
	 * 关闭软键盘
	 * 
	 * @param mEditText
	 *            输入框
	 * @param mContext
	 *            上下文
	 */
	public static void closeKeybord(EditText mEditText, Context mContext)
	{
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
	}
	
	public static void closeKeybord(Activity act){
		if(act.getCurrentFocus()==null){
			return;
		}
		InputMethodManager imm = (InputMethodManager) act
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED);
	}
	
	
	public static void openKeybord(Activity act)
	{
		if(act.getCurrentFocus()==null){
			return;
		}
		InputMethodManager imm = (InputMethodManager) act
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(act.getCurrentFocus(), InputMethodManager.HIDE_NOT_ALWAYS);
	
	}
	
}
