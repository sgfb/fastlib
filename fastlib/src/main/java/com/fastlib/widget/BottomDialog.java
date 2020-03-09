package com.fastlib.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fastlib.R;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.SupportBack;
import com.fastlib.utils.ScreenUtils;
import com.fastlib.utils.bind_view.ViewInject;

/**
 * Created by sgfb on 16/9/20.
 * 底部dialog，带动画
 * TODO 重构 layout替换
 */
public abstract class BottomDialog extends Fragment implements SupportBack {
//    public static final String ARG_INT_LAYOUT_ID ="layoutId";   //必传的布局id
//    public static final String ARG_INT_COLOR ="colorId";        //背景颜色代码
//
//    private ObjectAnimator mStartAnimator,mBgAnimation;
//    private View mView;
//    private View mBg;
//
//    /**
//     * 绑定视图
//     */
//    protected abstract void bindView();
//
//    @Override
//    public boolean onBackPressed() {
//        return false;
//    }
//
//    public void show(FragmentManager fm){
//        fm.beginTransaction()
//                .replace(android.R.id.content,this)
//                .commit();
//    }
//
//    /**
//     * 获取对应的布局id 优先arguments中去后ContentView注解
//     * @return 布局id
//     * @throws IllegalArgumentException 如果两个中都取不到id弹出异常
//     */
//    private int getLayoutId()throws IllegalArgumentException{
//        int layoutId=-1;
//        if(getArguments()!=null)
//            layoutId=getArguments().getInt(ARG_INT_LAYOUT_ID,-1);
//        if(layoutId==-1){
//            ContentView cv=getClass().getAnnotation(ContentView.class);
//            if(cv==null) throw new IllegalArgumentException("没有设置对应的布局");
//            layoutId=cv.value();
//        }
//        return layoutId;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
//        ViewGroup parent= (ViewGroup) inflater.inflate(R.layout.dialog_bottom,null);
//        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
//        int bgColor=-1;
//        mBg=parent.findViewById(R.id.bg);
//        mView=inflater.inflate(getLayoutId(),null);
//
//        if(getArguments()!=null)
//            bgColor=getArguments().getInt(ARG_INT_COLOR,getResources().getColor(R.color.translucent_dialog));
//        if(bgColor!=-1)
//            mBg.setBackgroundColor(bgColor);
//        mView.setLayoutParams(lp);
//        mView.setTranslationY(ScreenUtils.getScreenHeight()-ScreenUtils.getStatusHeight(getContext()));
//        parent.addView(mView);
//        ViewInject.inject(this,parent);
//        bindView();
//        mBg.setOnClickListener(v -> dismiss());
//        mView.setOnClickListener(v -> {
//            //nothing for defense touch event dispatch
//        });
//        return parent;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mView.post(new Runnable() {
//            @Override
//            public void run(){
//                mStartAnimator=ObjectAnimator.ofFloat(mView,"translationY",mView.getTranslationY(),ScreenUtils.getScreenHeight()-ScreenUtils.getStatusHeight(getContext())-mView.getHeight()).setDuration(220);
//                mBgAnimation=ObjectAnimator.ofFloat(mBg,"alpha",0,1).setDuration(220);
//                mStartAnimator.start();
//                mBgAnimation.start();
//            }
//        });
//    }
//
//    public void dismiss(){
//        if(mStartAnimator!=null){
//            mStartAnimator.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    super.onAnimationEnd(animation);
//                    if(getActivity()!=null)
//                        getFragmentManager().beginTransaction().remove(BottomDialog.this).commit();
//                }
//            });
//            mBgAnimation.reverse();
//            mStartAnimator.reverse();
//        }
//        else getFragmentManager().beginTransaction().remove(this).commit();
//    }
}