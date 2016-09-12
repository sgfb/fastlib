package com.fastlib;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by sgfb on 16/9/9.
 */
public class MyBanner extends Banner{

    public MyBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected HandlePage getHandleImageWithEvent(){
        return new HandlePage() {
            @Override
            public void handle(ImageView iv, Object element){
                String str=(String)element;
                Glide.with(getContext()).load(str).into(iv);
            }
        };
    }
}