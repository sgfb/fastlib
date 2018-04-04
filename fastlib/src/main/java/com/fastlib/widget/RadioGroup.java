package com.fastlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;

/**
 * Created by sgfb on 18/3/10.
 * 自定义RadioGroup扩展官方RadioGroup只支持LinearLayout限制
 */
public class RadioGroup extends FrameLayout{
    private int mCurrCheckId=-1;
    private CompoundButton.OnCheckedChangeListener mCheckChangeListener=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
            System.out.println("change checked");
            if(isChecked){
                if(mCurrCheckId!=buttonView.getId()&&mCurrCheckId!=-1){
                    View checkedView=findViewById(mCurrCheckId);
                    if(checkedView!=null&&checkedView instanceof RadioButton)
                        ((RadioButton)checkedView).setChecked(false);
                }
                mCurrCheckId = buttonView.getId();
            }
        }
    };

    public RadioGroup(Context context) {
        super(context);
        init();
    }

    public RadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadioGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 遍历view树来给存在RadioButton加入监听
     * @param v view元素
     */
    private void traverseFindRadioButton(View v){
        if(v instanceof RadioButton){
            RadioButton rb= (RadioButton) v;
            rb.setOnCheckedChangeListener(mCheckChangeListener);
            if(mCurrCheckId!=-1&&rb.isChecked())
                rb.setChecked(false);
        }
        else if(v instanceof ViewGroup){
            ViewGroup vg= (ViewGroup) v;
            vg.setOnHierarchyChangeListener(mChildHierarchyChangeListener);
            for(int i=0;i<vg.getChildCount();i++)
                traverseFindRadioButton(vg.getChildAt(i));
            final RadioButton firstRb=getFirstRadioButton(vg);
            try{
                if(firstRb!=null) vg.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v){
                        firstRb.setChecked(true);
                    }
                });
            }catch (Exception e){
                //有些View不支持OnClickListener
            }
        }
    }

    private RadioButton getFirstRadioButton(ViewGroup vg){
        for(int i=0;i<vg.getChildCount();i++){
            View child=vg.getChildAt(i);
            if(child instanceof RadioButton) return (RadioButton) child;
        }
        return null;
    }

    /**
     * 遍历view树来移除事件
     * @param v view元素
     */
    private void traverseRemoveRadioButtonEvent(View v){
        if(v instanceof RadioButton){
            RadioButton rb= (RadioButton) v;
            rb.setOnCheckedChangeListener(null);
        }
        else if(v instanceof ViewGroup){
            ViewGroup vg= (ViewGroup) v;
            vg.setOnHierarchyChangeListener(null);
            for(int i=0;i<vg.getChildCount();i++)
                traverseRemoveRadioButtonEvent(vg.getChildAt(i));
        }
    }

    private OnHierarchyChangeListener mChildHierarchyChangeListener=new OnHierarchyChangeListener() {
        @Override
        public void onChildViewAdded(View parent, View child){
            if(child instanceof RadioButton){
                RadioButton rb= (RadioButton) child;
                rb.setOnCheckedChangeListener(mCheckChangeListener);
                if(mCurrCheckId!=-1&&rb.isChecked())
                    rb.setChecked(false);
            }
            else if(child instanceof ViewGroup) {
                ViewGroup vg= (ViewGroup) child;
                vg.setOnHierarchyChangeListener(this);
            }
        }

        @Override
        public void onChildViewRemoved(View parent, View child){
            if(child instanceof RadioButton)
                ((RadioButton)child).setOnCheckedChangeListener(null);
            else if(child instanceof ViewGroup)
                ((ViewGroup)child).setOnHierarchyChangeListener(null);
        }
    };

    private OnHierarchyChangeListener mHandleRadioButtonHierarchyChangeListener=new OnHierarchyChangeListener() {
        @Override
        public void onChildViewAdded(View parent, View child){
            traverseFindRadioButton(child);
        }

        @Override
        public void onChildViewRemoved(View parent,View child) {
            traverseRemoveRadioButtonEvent(child);
        }
    };

    private void init(){
        setOnHierarchyChangeListener(mHandleRadioButtonHierarchyChangeListener);
    }

    public void check(int id){
        for(int i=0;i<getChildCount();i++){
            View child=getChildAt(i);
            if(child.getId()==id){
                if(child instanceof RadioButton)
                    ((RadioButton)child).setChecked(true);
            }
        }
    }

    public int getCheckedId(){
        return mCurrCheckId;
    }
}
