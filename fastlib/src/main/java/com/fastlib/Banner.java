package com.fastlib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fastlib.image_manager.ImageManager;
import com.fastlib.image_manager.request.Callback2ImageView;
import com.fastlib.image_manager.request.ImageRequest;
import com.fastlib.widget.AbsBanner;

/**
 * Create by sgfb on 2019/04/22
 * E-Mail:602687446@qq.com
 */
public class Banner extends AbsBanner<String>{

    public Banner(Context context) {
        super(context);
    }

    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected HandlePage<String> dataBindView() {
        return new HandlePage<String>() {
            @Override
            public void handle(View v, String element) {
//                ImageRequest.create(element).setAnimator(null).setCallbackParcel(new Callback2ImageView((ImageView) v)).start();
                Glide.with(getContext()).load(element).into((ImageView)v);
            }
        };
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.banner;
    }
}
