package com.fastlib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fastlib.R;

/**
 * Created by sgfb on 16/4/22.
 * 快速输入布局
 */
public class InputLayout extends LinearLayout{
    private TextView mTitle;
    private EditText mContent;

    public InputLayout(Context context){
        super(context);
        init(null);
    }

    public InputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet set){
        setOrientation(LinearLayout.HORIZONTAL);
        mTitle=new TextView(getContext());
        mContent=new EditText(getContext());
        TypedArray typedArray=getContext().obtainStyledAttributes(set, R.styleable.InputLayout);
        String title=typedArray.getString(R.styleable.InputLayout_input_title);
        String hit=typedArray.getString(R.styleable.InputLayout_input_hit);
        String content=typedArray.getString(R.styleable.InputLayout_input_content);
        mTitle.setText(title);
        mContent.setText(content);
        mContent.setHint(hit);
        typedArray.recycle();
        LayoutParams contentLp=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        mContent.setLayoutParams(contentLp);
        addView(mTitle);
        addView(mContent);
    }

    public TextView getTitle() {
        return mTitle;
    }

    public EditText getContent() {
        return mContent;
    }
}
