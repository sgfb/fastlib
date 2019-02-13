package com.fastlib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.fastlib.R;
import com.fastlib.utils.ScreenUtils;

/**
 * Created by sgfb on 17/3/27.
 * 通用标题栏,minHeight为?android:actionBarSize
 */
public class TitleBar extends LinearLayout {
    private TextView mTitle;
    private TextView mLeftText;
    private TextView mRightText;
    private ImageView mLeftIcon;
    private ImageView mRightIcon;
    private FrameLayout mLeftBackground;
    private FrameLayout mRightBackground;

    public TitleBar(Context context, AttributeSet attrs){
        super(context, attrs);
        init(attrs);
    }

    public TitleBar(Context context){
        super(context);
        init();
    }

    private void init(){
        setOrientation(VERTICAL);

        LayoutInflater layoutInflater= LayoutInflater.from(getContext());
        FrameLayout mainView=new FrameLayout(getContext());
        mTitle=new TextView(getContext());
        mLeftBackground= (FrameLayout) layoutInflater.inflate(R.layout.title_button,null);
        mRightBackground=(FrameLayout) layoutInflater.inflate(R.layout.title_button,null);
        mLeftText=mLeftBackground.findViewById(R.id.text);
        mLeftIcon=mLeftBackground.findViewById(R.id.image);
        mRightText=mRightBackground.findViewById(R.id.text);
        mRightIcon=mRightBackground.findViewById(R.id.image);
        FrameLayout.LayoutParams titleLp=new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        FrameLayout.LayoutParams leftBackgroundLp=new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        FrameLayout.LayoutParams rightBackgroundLp=new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mainView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        titleLp.gravity= Gravity.CENTER;
        leftBackgroundLp.gravity= Gravity.LEFT;
        rightBackgroundLp.gravity= Gravity.RIGHT;
        mTitle.setLayoutParams(titleLp);
        mLeftBackground.setLayoutParams(leftBackgroundLp);
        mRightBackground.setLayoutParams(rightBackgroundLp);

        //文字有左右10内边距，标题默认17sp大小
        mTitle.setTextSize(17);
        mTitle.setMaxLines(1);
        mTitle.setEllipsize(TextUtils.TruncateAt.END);
        mTitle.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});

        Space statusSpaceView=new Space(getContext());
        statusSpaceView.setMinimumHeight(ScreenUtils.getStatusHeight(getContext()));
        addView(statusSpaceView);
        addView(mainView);
        mainView.addView(mTitle);
        mainView.addView(mLeftBackground);
        mainView.addView(mRightBackground);

        TypedValue tv=new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize,tv,true);
        setMinimumHeight(getContext().getResources().getDimensionPixelSize(tv.resourceId));
    }

    private void init(AttributeSet attrs){
        init();
        TypedArray ta=getContext().obtainStyledAttributes(attrs, R.styleable.TitleBar);

        mTitle.setText(ta.getString(R.styleable.TitleBar_titleText));
        mTitle.setTextColor(ta.getColor(R.styleable.TitleBar_titleColor,mTitle.getCurrentTextColor()));
        mLeftText.setText(ta.getString(R.styleable.TitleBar_leftText));
        mLeftText.setTextColor(ta.getColor(R.styleable.TitleBar_leftTextColor,mLeftText.getCurrentTextColor()));
        mRightText.setText(ta.getString(R.styleable.TitleBar_rightText));
        mRightText.setTextColor(ta.getColor(R.styleable.TitleBar_rightTextColor,mRightText.getCurrentTextColor()));
        mLeftIcon.setImageDrawable(ta.getDrawable(R.styleable.TitleBar_leftIcon));
        mRightIcon.setImageDrawable(ta.getDrawable(R.styleable.TitleBar_rightIcon));
        ta.recycle();
    }

    /**
     * 设置左视图点击事件
     * @param listener
     */
    public void setOnLeftClickListener(OnClickListener listener){
        mLeftBackground.setOnClickListener(listener);
    }

    /**
     * 点击右视图点击事件
     * @param listener
     */
    public void setOnRightClickListener(OnClickListener listener){
        mRightBackground.setOnClickListener(listener);
    }

    public TextView getTitle() {
        return mTitle;
    }

    public TextView getLeftText() {
        return mLeftText;
    }

    public TextView getRightText() {
        return mRightText;
    }

    public ImageView getLeftIcon() {
        return mLeftIcon;
    }

    public ImageView getRightIcon() {
        return mRightIcon;
    }
}
