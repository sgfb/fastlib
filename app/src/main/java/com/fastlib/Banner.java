package com.fastlib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.fastlib.anim.PushPageTransformer;
import com.fastlib.widget.AbsBanner;

/**
 * Created by sgfb on 17/2/4.
 */
public class Banner extends AbsBanner{
    int[] ids={R.drawable.a,R.drawable.b,R.drawable.c};
    int i=0;

    public Banner(Context context) {
        super(context);
    }

    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPageTransformer(true,new PushPageTransformer());
        setInterval(100000);
    }

    @Override
    protected HandlePage getHandleImageWithEvent(){
        return new HandlePage() {
            @Override
            public void handle(View v, Object element){
                ((ImageView)v).setScaleType(ImageView.ScaleType.FIT_XY);
                ((ImageView)v).setImageResource(ids[i++%ids.length]);
            }
        };
    }

    @Override
    protected int getItemLayoutId() {
        return 0;
    }
}
