package com.fastlib.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *  带导航条的具有Fragment切换功能的Fragment基类
 * 
 * @author shenhaofeng
 * 
 */
public abstract class BaseViewPagerFragment extends BaseFragment {

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}

}
