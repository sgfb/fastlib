package com.fastlib;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fastlib.widget.AbsBanner;

/**
 * Created by sgfb on 2018/7/18.
 */

public class MyBanner extends AbsBanner<String>{

    public MyBanner(Context context) {
        super(context);
    }

    public MyBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected HandlePage<String> dataBindView() {
        return new HandlePage<String>() {
            @Override
            public void handle(View v, String element) {
                Glide.with(v.getContext()).load(element).override(400,400).into((ImageView)v.findViewById(R.id.image));
            }
        };
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item;
    }
}
