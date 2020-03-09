package com.fastlib.anim;

import androidx.viewpager.widget.ViewPager;
import android.view.View;

/**
 * Created by sgfb on 17/2/5.
 * 渐出或渐入页面转换动画
 */
public class AlphaPageTransformer implements ViewPager.PageTransformer{

    @Override
    public void transformPage(View page, float position){
        if(position>-1&&position<0){
            page.setAlpha(1-Math.abs(position));
            page.setTranslationX(page.getWidth()*Math.abs(position));
        }
        else if(position>0&&position<1){
            page.setAlpha(1);
            page.setTranslationX(page.getWidth()*position*-1);
        }
        else{
            page.setAlpha(1);
            page.setTranslationX(0);
        }
    }
}
