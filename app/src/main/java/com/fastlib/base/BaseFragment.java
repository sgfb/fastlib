package com.fastlib.base;

import com.fastlib.interf.OnFragmentState;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * fragment基类
 * 
 * @author shenhaofeng
 * 
 */
public class BaseFragment extends Fragment implements OnFragmentState {

	private Resources mResources;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mResources = getResources();
	}

	@Override
	public void onLoading(String str) {
		// TODO Auto-generated method stub

	}

	public void onLoading(int resId) {
		String str = mResources.getString(resId);
		onLoading(str);
	}

	@Override
	public void onLoaded() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(String str) {
		// TODO Auto-generated method stub
	}

	public void onError(int resId) {
		String str = mResources.getString(resId);
		onError(str);
	}

}
