package com.fastlib.anim;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.fastlib.utils.ScreenUtils;

/**
 * Created by sgfb on 2018/7/16.
 * 画廊风格转换动画
 */
public class GalleryPageTransformer implements ViewPager.PageTransformer{
    private float mSideScale =0.5F;     //两边page缩放
    private float mMiddleScale =0.8F;   //中间page缩放
    private float mSideMargin =40;     //两边往中间挤的长度(减去因缩放的误差)

    @Override
    public void transformPage(@NonNull View page, float position) {
        final float absPosition=Math.abs(position);

        if(position==0){        //中间
            page.setScaleX(mMiddleScale);
            page.setScaleY(mMiddleScale);
            page.setTranslationX(0);
        }
        else if(position>-1&&position<0){   //左边
            float scaleRate= mSideScale +((mMiddleScale - mSideScale)*(1-absPosition));
            page.setScaleY(scaleRate);
            page.setScaleX(scaleRate);
        }
        else if(position>0&&position<1){    //右边
            float scaleRate= mMiddleScale -(mMiddleScale - mSideScale)*position;
            page.setScaleY(scaleRate);
            page.setScaleX(scaleRate);
        }
        else{   //position==1 或 position==-1 左右顶端
            page.setScaleX(mSideScale);
            page.setScaleY(mSideScale);
        }

        float offset=(mMiddleScale-(mMiddleScale-mSideScale))* ScreenUtils.getScreenWidth()/2*position*-1;   //因缩放造成的误差
        page.setTranslationX(position*-1* mSideMargin+offset);
    }

    public float getSideScale() {
        return mSideScale;
    }

    public void setSideScale(float mSideScale) {
        this.mSideScale = mSideScale;
    }

    public float getMiddleScale() {
        return mMiddleScale;
    }

    public void setMiddleScale(float mMiddleScale) {
        this.mMiddleScale = mMiddleScale;
    }

    public float getSideMargin() {
        return mSideMargin;
    }

    public void setSideMargin(float mSideMargin) {
        this.mSideMargin = mSideMargin;
    }
}
