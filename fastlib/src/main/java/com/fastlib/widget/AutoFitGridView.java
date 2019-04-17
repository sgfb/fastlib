package com.fastlib.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fastlib.adapter.AutoFitGridAdapter;

/**
 * Created by sgfb on 16/6/18.
 * 自动换行网格布局
 */
public class AutoFitGridView extends LinearLayout {
    private boolean isInited=false;
    private int mSidesPadding;  //两边内边距,px单位
    private AutoFitGridAdapter mAdapter;
    private DataSetObserver mObserver=new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            readAdapter(getWidth());
        }
    };

    public AutoFitGridView(Context context, AttributeSet attrs){
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        mSidesPadding =getPaddingLeft()+getPaddingRight();
        post(new Runnable() {
            @Override
            public void run() {
                isInited=true;
                readAdapter(getWidth());
            }
        });
    }

    public void setAdapter(@NonNull AutoFitGridAdapter adapter){
        if(mAdapter!=null) mAdapter.unregisterDataSetObserver(mObserver);
        mAdapter=adapter;
        mAdapter.registerDataSetObserver(mObserver);
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }
        });
        if(isInited) readAdapter(getWidth());
    }

    /**
     * 读取适配器，绑定数据和视图
     * @param maxWidth 本视图最大宽度
     */
    @SuppressWarnings("unchecked")
    private void readAdapter(int maxWidth){
        removeAllViews();
        if(mAdapter==null) return;

        int realMaxWidth=maxWidth-mSidesPadding;
        int mCurrLineRemain = realMaxWidth;
        addNewLine();
        for(int i=0;i<mAdapter.getCount();i++){
            Object itemData=mAdapter.getItemAtPosition(i);
            Pair<Integer,View> child=mAdapter.getView(i,itemData);
            if(child.first> mCurrLineRemain){
                mCurrLineRemain =realMaxWidth;
                addNewLine();
            }
            mCurrLineRemain -=child.first;
            ((ViewGroup)getChildAt(getChildCount()-1)).addView(child.second);
        }
    }

    private int getLastChildRemainWidth(int maxWidth){
        ViewGroup child= (ViewGroup) getChildAt(getChildCount()-1);
        if(child.getChildCount()==0) return maxWidth;
        View lastChild=child.getChildAt(child.getChildCount()-1);
        return child.getRight()-lastChild.getRight();
    }

    /**
     * 增加新行
     */
    private void addNewLine(){
        LinearLayout linearLayout=new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        addView(linearLayout);
    }
}
