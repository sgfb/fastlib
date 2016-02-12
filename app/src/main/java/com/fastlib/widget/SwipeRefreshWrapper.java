package com.fastlib.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;

/**
 * 偷懒版SwipeRefreshLayout.可以包裹非AbsListView正常使用
 */
public class SwipeRefreshWrapper extends FrameLayout {
    private SwipeRefreshLayout mRefresh;
    private ListView mDefaultListView;

    public SwipeRefreshWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRefresh=new SwipeRefreshLayout(context);
        mDefaultListView=new ListView(context);
        mRefresh.addView(mDefaultListView);
        addView(mRefresh);
    }

    public SwipeRefreshLayout getRefresh(){
        return mRefresh;
    }
}
