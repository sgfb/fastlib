package com.fastlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fastlib.utils.DensityUtils;
import com.fastlib.utils.Utils;

import java.util.Locale;

/**
 * Created by sgfb on 2017/10/12.
 * 柱状进度条
 */
public class RectProgress extends LinearLayout{
    final int PROGRESS_WIDTH_DP=70;
    final int PROGRESS_HEIGHT_DP=100;

    private TextView mProgress;
    private TextView mTitle;
    private FrameLayout mProgressLayout;
    private View mProgressBackground;
    private View mProgressbar;

    public RectProgress(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        mProgress=new TextView(getContext());
        mTitle=new TextView(getContext());
        mProgressLayout=new FrameLayout(getContext());
        mProgressBackground =new View(getContext());
        mProgressbar=new View(getContext());
        LayoutParams progressLp=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        LayoutParams titleLp=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        LayoutParams progressLayoutLp=new LayoutParams(DensityUtils.dp2px(getContext(),PROGRESS_WIDTH_DP),DensityUtils.dp2px(getContext(),PROGRESS_HEIGHT_DP));
        FrameLayout.LayoutParams backgroundLp=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        FrameLayout.LayoutParams progressbarLp=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,0);
        final int dp7= DensityUtils.dp2px(getContext(),7);
        final int dp10=DensityUtils.dp2px(getContext(),10);

        //LayoutParams和基本视图属性生成
        progressLp.gravity= Gravity.CENTER_HORIZONTAL;
        mProgress.setLayoutParams(progressLp);
        mProgress.setPadding(dp7,dp7,dp7,dp7);
        mProgress.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.font_size_normal_high));

        titleLp.gravity=Gravity.CENTER_HORIZONTAL;
        mTitle.setLayoutParams(titleLp);
        mTitle.setPadding(dp10,dp10,dp10,dp10);
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.font_size_normal_high));

        progressLayoutLp.gravity=Gravity.CENTER_HORIZONTAL;
        mProgressLayout.setLayoutParams(progressLayoutLp);
        mProgressLayout.addView(mProgressBackground);
        mProgressLayout.addView(mProgressbar);

        mProgressBackground.setLayoutParams(backgroundLp);

        progressbarLp.gravity=Gravity.BOTTOM;
        mProgressbar.setLayoutParams(progressbarLp);

        //自定义属性
        TypedArray ta=getContext().obtainStyledAttributes(attrs,R.styleable.RectProgress);
        float progressPercent=ta.getFloat(R.styleable.RectProgress_progressPercent,0);

        mTitle.setText(ta.getText(R.styleable.RectProgress_progressTitle));
        setProgressPercent(progressPercent);
        addView(mProgress);
        addView(mProgressLayout);
        addView(mTitle);
        ta.recycle();
    }

    public TextView getProgress() {
        return mProgress;
    }

    public TextView getTitle() {
        return mTitle;
    }

    public View getProgressBackground() {
        return mProgressBackground;
    }

    public View getProgressbar() {
        return mProgressbar;
    }

    /**
     * 设置进度条百分比
     * @param percent 百分比
     */
    public void setProgressPercent(float percent){
        mProgress.setText(String.format(Locale.getDefault(),"%s%%",percent));
        FrameLayout.LayoutParams progressbarLp= (FrameLayout.LayoutParams) mProgressbar.getLayoutParams();
        progressbarLp.height= DensityUtils.dp2px(getContext(),PROGRESS_HEIGHT_DP*percent/100);
        mProgressbar.setLayoutParams(progressbarLp);
    }

    /**
     * 获取进度百分比
     * @return 百分比进度
     */
    public float getProgressPercent(){
        String percent=mProgress.getText().toString();
        percent=percent.substring(0,percent.length()-1);
        return Utils.safeToString(percent,0f);
    }
}