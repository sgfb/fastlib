package com.fastlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by sgfb on 17/6/12.
 */
public class PrinterTextView extends TextView{

    public PrinterTextView(Context context) {
        super(context);
    }

    public PrinterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void drawText(Canvas canvas,String str){
        Rect rect=new Rect(getPaddingLeft(),getPaddingTop(),
                getWidth()-getPaddingRight(),getHeight()-getPaddingBottom());
        Paint.FontMetricsInt metricsInt=getPaint().getFontMetricsInt();
        int baseLine=(rect.top+rect.bottom-metricsInt.top-metricsInt.bottom)/2;
        canvas.drawText(str,getPaddingLeft(),baseLine,getPaint());
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        drawText(canvas,"hello,world!");
    }
}