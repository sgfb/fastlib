package com.fastlib.interf;


/**
 * Fragment状态接口
 * @author shenhaofeng
 *
 */
public interface OnFragmentState {

	void onLoading(String resId);

	void onLoaded();

	void onError(String resId);

}
