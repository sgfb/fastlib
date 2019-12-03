package com.fastlib.widget;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fastlib.utils.DensityUtils;
import com.fastlib.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 16/6/18.
 */
public class AutofitGridView extends LinearLayout implements View.OnClickListener{
    private AdapterView.OnItemClickListener mListener;
    private int mPaddingAndMargin; //外边距和内边距,px单位
    private int mIndex;
    private int mLimitWidth;
    private int mDividerWidth;
    private int mDividerHeight;

    public AutofitGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        LayoutParams lp= (LayoutParams) getLayoutParams();
        if(lp!=null)
            mPaddingAndMargin+=(lp.leftMargin+lp.rightMargin);
        mPaddingAndMargin=getPaddingLeft()+getPaddingRight();
        mLimitWidth=ScreenUtils.getScreenWidth();
        mDividerHeight=DensityUtils.dp2px(context,3);
        mDividerWidth=DensityUtils.dp2px(context,3);
    }

    public void addStrings(@LayoutRes final int resId, final String... ss){
        List<String> list=new ArrayList<>();
        for(int i=0;i<ss.length;i++)
            list.add(ss[i]);
        mIndex =0;
        addStrings(resId, list);
    }

    public void addStrings(@LayoutRes final int resId, final List<String> list){
        mIndex =0;
        addStringByCircle(resId, list);
    }

    private void addStringByCircle(@LayoutRes final int resId,final List<String> list){
        TextView tv= (TextView) LayoutInflater.from(getContext()).inflate(resId,null);
        LayoutParams lp=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        LayoutParams lp2=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);

        if(list==null||list.size()<=0){
            invalidate();
            return;
        }
        List<String> temp=new ArrayList<>(list);
        String str=temp.remove(0);
        lp.setMargins(0, 0,mDividerWidth, 0);
        lp2.setMargins(0, 0, 0,mDividerHeight);
        tv.setLayoutParams(lp);
        tv.setText(str);
        tv.setTag(mIndex++);
        tv.setOnClickListener(this);

        if(getChildCount()>0){
            LinearLayout ll=(LinearLayout)getChildAt(getChildCount()-1);
            int width = computeTextViewWidth(tv);
            int textRight=getSelfRight(ll)+width+mPaddingAndMargin;

            if (mLimitWidth<textRight){
                LinearLayout ll2 = new LinearLayout(getContext());

                ll2.setLayoutParams(lp2);
                ll2.setOrientation(LinearLayout.HORIZONTAL);
                ll2.addView(tv);
                addView(ll2);
            } else
                ll.addView(tv);
            addStringByCircle(resId, temp);
        }
        else{
            LinearLayout ll=new LinearLayout(getContext());
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setLayoutParams(lp2);
            ll.addView(tv);
            addView(ll);
            addStringByCircle(resId, temp);
        }
    }

    public void setOnItemListener(AdapterView.OnItemClickListener l){
        mListener=l;
    }

    private int getSelfRight(ViewGroup v){
        int right=0;
        int dp3=DensityUtils.dp2px(getContext(),3);
        for(int i=0;i<v.getChildCount();i++){
            TextView tv= (TextView) v.getChildAt(i);
            right+=(computeTextViewWidth(tv)+dp3);
        }
        return right;
    }

    public int computeTextViewWidth(TextView tv){
        int spec=MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        tv.measure(spec,spec);
        return tv.getMeasuredWidth();
    }

    public View findViewByPosition(int position){
        int childCount=getChildCount();
        int grandsonCount=0;
        for(int i=0;i<childCount;i++){
            ViewGroup gv= (ViewGroup) getChildAt(i);
            grandsonCount+=gv.getChildCount();
            if(grandsonCount>position)
                return gv.getChildAt(position-(grandsonCount-gv.getChildCount()));
        }
        return null;
    }

    @Override
    public void onClick(View v){
        if(mListener!=null){
            int index= (int) v.getTag();
            mListener.onItemClick(null,v,index,index);
        }
    }

    public void setLimitWidth(int limitWidth){
        mLimitWidth=limitWidth;
        invalidate();
    }

    public void setDividerWidth(int dividerWidth){
        mDividerWidth=dividerWidth;
        invalidate();
    }

    public void setDividerHeight(int dividerHeight){
        mDividerHeight=dividerHeight;
        invalidate();
    }
}
