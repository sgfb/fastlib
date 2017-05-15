package com.fastlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * Created by sgfb on 17/5/15.
 * TitleBar复合加上进度条
 */
public class TitleBarWithProgress extends LinearLayout{
    private TitleBar mTitleBar;
    private ProgressBar mProgressBar;

    public TitleBarWithProgress(Context context) {
        super(context);
        init();
    }

    public TitleBarWithProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mTitleBar=new TitleBar(getContext());
        mProgressBar=new ProgressBar(getContext());
    }
}
