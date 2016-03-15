package com.fastlib.widget;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.Button;

import com.fastlib.base.RoundDrawable;

/**
 * Created by sgfb on 16/3/7.
 */
public class RoundButton extends Button{
    private int mCorner;
    private Drawable mPress,mRelease;

    public RoundButton(Context context){
        super(context);
    }

    public RoundButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCorner=5;
    }

    public void setState(Drawable press,Drawable release){
        StateListDrawable sld=new StateListDrawable();
        RoundDrawable rd1=new RoundDrawable(press),rd2=new RoundDrawable(release);

        mPress=press;
        mRelease=release;
        rd1.setCorner(mCorner);
        rd2.setCorner(mCorner);
        sld.addState(new int[]{android.R.attr.state_pressed},rd1);
        sld.addState(new int[]{},rd2);
        if(Build.VERSION.SDK_INT>=16)
            setBackground(sld);
        else
            setBackgroundDrawable(sld);
    }

    public void setCorner(int corner){
        mCorner=corner;
        if(mPress!=null&&mRelease!=null)
            setState(mPress,mRelease);
    }
}