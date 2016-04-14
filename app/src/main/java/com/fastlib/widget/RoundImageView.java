package com.fastlib.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.fastlib.R;

import java.lang.ref.WeakReference;

/**
 * Created by sgfb on 16/3/22.
 */
public class RoundImageView extends ImageView {
    /**
     * 图片的类型，圆形or圆角
     */
    public static final int TYPE_CIRCLE = 0;
    public static final int TYPE_ROUND = 1;
    private int type;
    private int radius;
    private Paint mPaint;
    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private Bitmap mMaskBitmap;

    private WeakReference<Bitmap> mWeakBitmap;

    public RoundImageView(Context context){
        super(context);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        radius=ta.getInt(R.styleable.RoundImageView_radius,5);
        ta.recycle();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        // 在缓存中取出bitmap
        Bitmap bitmap = mWeakBitmap == null ? null : mWeakBitmap.get();

        if (null == bitmap || bitmap.isRecycled()){
            Drawable drawable = getDrawable();
            if(drawable==null)
                return;
            int dWidth = drawable.getIntrinsicWidth();
            int dHeight = drawable.getIntrinsicHeight();

            // 创建bitmap
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            float scale = 1.0f;
            // 创建画布
            Canvas drawCanvas = new Canvas(bitmap);
            // 按照bitmap的宽高，以及view的宽高，计算缩放比例；因为设置的src宽高比例可能和imageview的宽高比例不同，这里我们不希望图片失真；
            if (type == TYPE_ROUND) {
                // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
                scale = Math.max(getWidth() * 1.0f / dWidth, getHeight() * 1.0f / dHeight);
            } else {
                scale = getWidth() * 1.0F / Math.min(dWidth, dHeight);
            }
            // 根据缩放比例，设置bounds，相当于缩放图片了
            drawable.setBounds(0, 0, (int) (scale * dWidth), (int) (scale * dHeight));
            drawable.draw(drawCanvas);
            if (mMaskBitmap == null || mMaskBitmap.isRecycled()) {
                mMaskBitmap = getBitmap();
            }
            // Draw Bitmap.
            mPaint.reset();
            mPaint.setFilterBitmap(false);
            mPaint.setXfermode(mXfermode);
            // 绘制形状
            drawCanvas.drawBitmap(mMaskBitmap, 0, 0, mPaint);
            mPaint.setXfermode(null);
            // 将准备好的bitmap绘制出来
            canvas.drawBitmap(bitmap, 0, 0, null);
            // bitmap缓存起来，避.免每次调用onDraw，分配内存
            mWeakBitmap = new WeakReference<>(bitmap);
        }
        else{
            mPaint.setXfermode(null);
            canvas.drawBitmap(bitmap,0,0, mPaint);
        }
    }

    /**
     * 绘制形状
     *
     * @return
     */
    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        if (type == TYPE_ROUND) {
            canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()), radius, radius, paint);
        } else {
            canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2 - 5, paint);
        }
        return bitmap;
    }
}
