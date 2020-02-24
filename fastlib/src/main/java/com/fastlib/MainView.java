package com.fastlib;

import android.content.Context;

import com.fastlib.aspect.OptionalComponent;
import com.fastlib.base.OldViewHolder;
import com.fastlib.utils.ContextHolder;
import com.fastlib.utils.N;

/**
 * Created by sgfb on 2020\02\24.
 */
public class MainView{
    @OptionalComponent
    Context mContext;
    @OptionalComponent
    OldViewHolder mOldViewHolder;


    public void showToast(String message){
        N.showShort(mContext,message);
        System.out.println(Thread.currentThread());
    }
}
